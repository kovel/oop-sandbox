package org.example;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.StatusRuntimeException;
import org.example.service.PdfServiceGrpc;
import org.example.service.Pdfs;

public class GrpcClient {
    public static void main(String[] args) {
        var channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create())
                .build();
        var blockingStub = PdfServiceGrpc.newBlockingStub(channel);

        Pdfs.PdfRequest request = Pdfs.PdfRequest.newBuilder().setUrl("http://example.org/").build();
        Pdfs.PdfResponse response;
        try {
            response = blockingStub.getPdf(request);
            System.out.println(response);
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
        }
    }
}
