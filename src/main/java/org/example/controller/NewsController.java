package org.example.controller;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.StatusRuntimeException;
import org.example.router.RouteParameters;
import org.example.service.PdfServiceGrpc;
import org.example.service.Pdfs;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Base64;

public class NewsController implements IController {
    @Override
    public ControllerResponse run(RouteParameters args) {
        var channel = Grpc.newChannelBuilder("127.0.0.1:50051", InsecureChannelCredentials.create())
                .build();
        var blockingStub = PdfServiceGrpc.newBlockingStub(channel);

        Pdfs.PdfRequest request = Pdfs.PdfRequest.newBuilder().setUrl("https://news.google.com/").build();
        Pdfs.PdfResponse response;
        try {
            response = blockingStub.getPdf(request);
            return ControllerResponse.of(Base64.getDecoder().decode(response.getData().getBytes()))
                    .header("Content-Type", "application/pdf");
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
        }
        return null;
    }
}
