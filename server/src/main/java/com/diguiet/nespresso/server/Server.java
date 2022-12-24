package com.diguiet.nespresso.server;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import sila_java.library.server_base.SiLAServer;
import sila_java.library.server_base.identification.ServerInformation;
import sila_java.library.server_base.utils.ArgumentHelper;

import static sila_java.library.core.utils.FileUtils.getResourceContent;

/**
 * Nespresso SiLA Server
 *
 * Allow clients to see and add Nespresso coffees into a shopping list
 */
@Slf4j
public class Server implements AutoCloseable {
    private static final String SERVER_TYPE = "NespressoSiLAServer";
    private static final String SERVER_DESC = "Allow clients to see and add Nespresso coffees into a shopping list";
    private static final String SERVER_VENDOR = "https://github.com/QTimort";
    private static final String SERVER_VERSION = "2.42";
    private final SiLAServer siLAServer;

    public static void main(final String[] args) {
        final ArgumentHelper argumentHelper = new ArgumentHelper(args, SERVER_TYPE);
        final Server server = new Server(argumentHelper);
        log.info("To stop the server press CTRL + C.");
        server.siLAServer.blockUntilShutdown();
        log.info("termination complete.");
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
            final SiLAServer.Builder builder = SiLAServer.Builder.newBuilder(serverInfo);

            builder.withPersistentConfig(argumentHelper.getConfigFile().isPresent());

            argumentHelper.getConfigFile().ifPresent(builder::withPersistentConfigFile);

            builder.withPersistentTLS(
                    argumentHelper.getPrivateKeyFile(),
                    argumentHelper.getCertificateFile(),
                    argumentHelper.getCertificatePassword()
            );

            if (argumentHelper.useUnsafeCommunication()) {
                builder.withUnsafeCommunication(true);
            }

            argumentHelper.getHost().ifPresent(builder::withHost);
            argumentHelper.getPort().ifPresent(builder::withPort);
            argumentHelper.getInterface().ifPresent(builder::withNetworkInterface);
            builder.withDiscovery(argumentHelper.hasNetworkDiscovery());

            builder.addFeature(
                    getResourceContent("Nespresso.sila.xml"),
                    new NespressoRpcImpl()
            );
            this.siLAServer = builder.start();
        } catch (Exception e) {
            log.error("Something went wrong when building / starting server", e);
            throw new RuntimeException(e);
        }

    }
}
