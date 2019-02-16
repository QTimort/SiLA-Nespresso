package fr.diguiet.nespresso.server;

import io.grpc.stub.StreamObserver;
import lombok.NonNull;
import sila2.org.silastandard.SiLAFramework;
import sila2.org.silastandard.examples.greetingprovider.v1.GreetingProviderGrpc;
import sila2.org.silastandard.examples.greetingprovider.v1.GreetingProviderOuterClass;
import sila_java.library.core.utils.SiLAErrors;
import sila_java.library.server_base.SiLAServer;
import sila_java.library.server_base.identification.ServerInformation;
import sila_java.library.server_base.utils.ArgumentHelper;

import java.io.IOException;
import java.time.Year;

import static sila_java.library.core.utils.Utils.blockUntilStop;
import static sila_java.library.core.utils.FileUtils.getResourceContent;

/**
 * Example minimal SiLA Server
 */
public class Server implements AutoCloseable {
    // Every SiLA Server needs to define a type
    public static final String SERVER_TYPE = "Hello SiLA Server";

    // SiLA Server constructed in constructor
    private final SiLAServer server;

    /**
     * Application Class using command line arguments
     * @param argumentHelper Custom Argument Helper
     */
    Server(@NonNull final ArgumentHelper argumentHelper) {
        /*
        Start the minimum SiLA Server with the Meta Information
        and the gRPC Server Implementations (found below)
          */
        final ServerInformation serverInfo = new ServerInformation(
                SERVER_TYPE,
                "Simple Example of a SiLA Server",
                "www.sila-standard.org",
                "v0.0"
        );

        try {
            /*
            A configuration file has to be given if the developer wants to persist server configurations
            (such as the generated UUID)
             */
            final SiLAServer.Builder builder;
            if (argumentHelper.getConfigFile().isPresent()) {
                builder = SiLAServer.Builder.withConfig(argumentHelper.getConfigFile().get(), serverInfo);
            } else {
                builder = SiLAServer.Builder.withoutConfig(serverInfo);
            }

            /*
            Additional optional arguments are used, if no port is given it's automatically chosen, if
            no network interface is given, discovery is not enabled.
             */
            argumentHelper.getPort().ifPresent(builder::withPort);
            argumentHelper.getInterface().ifPresent(builder::withDiscovery);

            builder.addFeature(
                    "org.silastandard/examples/v1/GreetingProvider",
                    getResourceContent("Nespresso.sila.xml"),
                    new GreeterImpl()
            );

            this.server = builder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        this.server.close();
    }

    /**
     * Simple main function that starts the server and keeps it alive
     */
    public static void main(final String[] args) throws IOException {
        final ArgumentHelper argumentHelper = new ArgumentHelper(args, SERVER_TYPE);

        try (final Server server = new Server(argumentHelper)) {
            // Always add shut down hook in main to ensure clean shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(server::close));
            blockUntilStop();
        }

        System.out.println("termination complete.");
    }

    /**
     * Implementation of the gRPC Service mapped from the "GreetingProvider" Feature, the stubs
     * are automatically generated from the maven plugin.
     */
    static class GreeterImpl extends GreetingProviderGrpc.GreetingProviderImplBase {
        /**
         * Implementation of the SayHello Command, the mapping can be refered to from Part B
         */
        @Override
        public void sayHello(
                GreetingProviderOuterClass.SayHello_Parameters req,
                StreamObserver<GreetingProviderOuterClass.SayHello_Responses> responseObserver
        ) {
            /*
             Different parameters can be checked, it is mandatory to throw Validation Errors in case of
             missing parameters, which will be done automatically in the future.
              */
            if (!req.hasName()) {
                responseObserver.onError(SiLAErrors.generateValidationError(
                        "Name",
                        "Name parameter was not set",
                        "Specify a name with at least one character"));
                return;
            }

            // Custom ValidationError example
            String name = req.getName().getValue();
            if ("error".equalsIgnoreCase(name)) {
                responseObserver.onError(
                        SiLAErrors.generateValidationError(
                                "Name",
                                "Name was called error therefore throw an error :)",
                                "Specify a name that is not \"error\"")
                );
                return;
            }

            // The result of the command is created with the according protobuf builders
            final String msg = "Hello " + name;
            GreetingProviderOuterClass.SayHello_Responses result =
                    GreetingProviderOuterClass.SayHello_Responses
                            .newBuilder()
                            .setGreeting(SiLAFramework.String.newBuilder()
                                    .setValue(msg)
                            )
                            .build();
            responseObserver.onNext(result);
            responseObserver.onCompleted();
            System.out.println("Request received on " + SERVER_TYPE + " " + msg);
        }

        /**
         * Implementation of the unobservable property Start Year
         *
         * It works just like a command but doesn't have any parameters and shouldn't change any
         * state of the server or some underlying instrument or database.
         */
        @Override
        public void getStartYear(
                GreetingProviderOuterClass.Get_StartYear_Parameters request,
                StreamObserver<GreetingProviderOuterClass.Get_StartYear_Responses> responseObserver
        ) {
            responseObserver.onNext(
                    GreetingProviderOuterClass.Get_StartYear_Responses.newBuilder()
                            .setStartYear(
                                    SiLAFramework.Integer.newBuilder()
                                            .setValue(Year.now().getValue())
                                    )
                            .build()
            );
            responseObserver.onCompleted();
        }
    }
}
