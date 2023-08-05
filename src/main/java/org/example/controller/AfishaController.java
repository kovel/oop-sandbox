package org.example.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.StatusRuntimeException;
import org.example.integration.Airtable;
import org.example.parser.Parser;
import org.example.parser.Seance;
import org.example.router.HttpRouterParameters;
import org.example.router.RouteParameters;
import org.example.service.PdfServiceGrpc;
import org.example.service.Pdfs;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class AfishaController implements IController {

    @Override
    public ControllerResponse run(RouteParameters args) {
        if (args instanceof HttpRouterParameters httpArgs) {
            var httpMethod = httpArgs.getRequestLine().getMethod();
            switch (httpMethod) {
                case "GET" -> {
                    if (String.join("/", httpArgs.getRequestLine().getPath()).endsWith("/pdf")) {
                        var channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create())
                                .build();
                        var blockingStub = PdfServiceGrpc.newBlockingStub(channel);

                        Pdfs.PdfRequest request = Pdfs.PdfRequest.newBuilder().setUrl("https://afisha.relax.by/kino/minsk/").build();
                        Pdfs.PdfResponse response;
                        try {
                            response = blockingStub.getPdf(request);
                            return ControllerResponse.of(Base64.getDecoder().decode(response.getData().getBytes()))
                                    .header("Content-Type", "application/pdf");
                        } catch (StatusRuntimeException e) {
                            e.printStackTrace();
                        }
                    }
                    return this.indexAction();
                }
                case "POST" -> {
                    return this.createAction();
                }
                case "DELETE" -> {
                    return this.deleteAction(httpArgs.getRequestLine().getPath().get(2));
                }
                default -> throw new RuntimeException(String.format("Wrong method: %s", httpMethod));
            }
        } else {
            return this.indexAction();
        }
    }

    private ControllerResponse deleteAction(String index) {
        var idx = Integer.parseInt(index);
        System.out.println("Deleting #" + idx);

        try (var fos = new FileOutputStream("response.json")) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            var seanses = new LinkedList<>();
            var json = getJson();
            for (int i = 0; i < json.size(); i++) {
                if (i != idx) {
                    seanses.add(json.get(i));
                }
            }

            fos.write(objectMapper.writeValueAsString(seanses).getBytes());

            return ControllerResponse.of().status(204);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ControllerResponse createAction() {
        try (var fos = new FileOutputStream("response.json")) {
            fos.write(getAfishaContent().getBytes());

            ObjectMapper objectMapper = new ObjectMapper();
            return ControllerResponse
                    .of(objectMapper.writeValueAsString(true))
                    .header("Content-Type", "application/json")
                    .status(204);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ControllerResponse indexAction() {
        return ControllerResponse.of(getAfishaContent())
                .header("Content-Type", "application/json");
    }

    private List<Seance> getJson() {
        String url = "https://afisha.relax.by/kino/minsk/";
        return new Parser(url).parseSchedule();
    }

    private String getAfishaContent() {
        List<Seance> seances = getJson();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            addRecordsToAirtable(seances);
            return objectMapper.writeValueAsString(seances);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void addRecordsToAirtable(List<Seance> seances) throws JsonProcessingException {
        Airtable airtable = new Airtable();
        if (!airtable.isTableExist("Seances")) {
            airtable.createTable();
        }
        airtable.addRecords(seances);
    }

}
