package org.example;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.StatusRuntimeException;
import org.example.service.PdfServiceGrpc;
import org.example.service.Pdfs;

import java.util.Base64;

public class GrpcClient {
    public static void main(String[] args) {
        var channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create())
                .build();
        var blockingStub = PdfServiceGrpc.newBlockingStub(channel);

        Pdfs.PdfRequest request = Pdfs.PdfRequest.newBuilder().setUrl("https://news.google.com/").build();
        Pdfs.PdfResponse response;
        try {
            response = blockingStub.getPdf(request);
            //System.out.println(new String(Base64.getDecoder().decode(response.getData().getBytes())));
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
        }
    }
}
