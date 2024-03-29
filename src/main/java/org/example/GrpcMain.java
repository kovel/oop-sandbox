package org.example;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.stub.StreamObserver;
import lombok.extern.java.Log;
import org.example.service.PdfServiceGrpc;
import org.example.service.Pdfs;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Log
public class GrpcMain {
    private Server server;

    private void start() throws IOException {
        /* The port on which the server should run */
        int port = 50051;
        server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
                .addService(new PdfServiceImpl())
                .build()
                .start();
        log.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    GrpcMain.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        final GrpcMain server = new GrpcMain();
        server.start();
        server.blockUntilShutdown();
    }

    static class PdfServiceImpl extends PdfServiceGrpc.PdfServiceImplBase {
        @Override
        public void getPdf(Pdfs.PdfRequest request, StreamObserver<Pdfs.PdfResponse> responseObserver) {
            Pdfs.PdfResponse response = Pdfs.PdfResponse.newBuilder().setData("./path/to/pdf").build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
