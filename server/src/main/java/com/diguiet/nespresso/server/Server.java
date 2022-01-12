package com.diguiet.nespresso.server;

import lombok.NonNull;
import sila_java.library.core.encryption.SelfSignedCertificate;
import sila_java.library.server_base.SiLAServer;
import sila_java.library.server_base.identification.ServerInformation;
import sila_java.library.server_base.utils.ArgumentHelper;

import java.io.IOException;

import static sila_java.library.core.utils.Utils.blockUntilStop;
import static sila_java.library.core.utils.FileUtils.getResourceContent;

/**
 * Nespresso SiLA Server
 *
 * Allow clients to see and add Nespresso coffees into a shopping list
 */
public class Server implements AutoCloseable {
    private static final String SERVER_TYPE = "Nespresso SiLA Server";
    private static final String SERVER_DESC = "Allow clients to see and add Nespresso coffees into a shopping list";
    private static final String SERVER_VENDOR = "https://github.com/QTimort";
    private static final String SERVER_VERSION = "0.42";
    private final SiLAServer siLAServer;

    public static void main(final String[] args) {
        final ArgumentHelper argumentHelper = new ArgumentHelper(args, SERVER_TYPE);
        // Start Server
        try (final Server server = new Server(argumentHelper)) {
            Runtime.getRuntime().addShutdownHook(new Thread(server::close));
            blockUntilStop();
        }
        System.out.println("Server closed.");
    }

    @Override
    public void close() {
        this.siLAServer.close();
    }

    private Server(@NonNull final ArgumentHelper argumentHelper) {
        final ServerInformation serverInfo = new ServerInformation(
                SERVER_TYPE, SERVER_DESC, SERVER_VENDOR, SERVER_VERSION
        );
        try {
            final SiLAServer.Builder builder;
            if (argumentHelper.getConfigFile().isPresent()) {
                builder = SiLAServer.Builder.withConfig(argumentHelper.getConfigFile().get(), serverInfo);
            } else {
                builder = SiLAServer.Builder.withoutConfig(serverInfo);
            }
            argumentHelper.getPort().ifPresent(builder::withPort);
            argumentHelper.getInterface().ifPresent(builder::withDiscovery);

            if (argumentHelper.useEncryption()) {
                builder.withSelfSignedCertificate();
            }

            builder.addFeature(
                    getResourceContent("Nespresso.sila.xml"),
                    new NespressoRpcImpl()
            );

            this.siLAServer = builder.start();
        } catch (IOException | SelfSignedCertificate.CertificateGenerationException e) {
            throw new RuntimeException(e);
        }

    }
}
