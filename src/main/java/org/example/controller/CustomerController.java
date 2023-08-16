package org.example.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.StatusRuntimeException;
import org.example.annotation.DAO;
import org.example.dao.DatabaseDAO;
import org.example.integration.Airtable;
import org.example.parser.Parser;
import org.example.parser.Seance;
import org.example.router.HttpRouterParameters;
import org.example.router.RouteParameters;
import org.example.service.PdfServiceGrpc;
import org.example.service.Pdfs;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CustomerController implements IController {

    @DAO("database")
    private DatabaseDAO dao;

    @Override
    public ControllerResponse run(RouteParameters args) {
        if (args instanceof HttpRouterParameters httpArgs) {
            var httpMethod = httpArgs.getRequestLine().getMethod();
            switch (httpMethod) {
                case "GET" -> {
                    return this.indexAction(httpArgs);
                }
                case "POST" -> {
                    return this.postAction(httpArgs);
                }
                case "PUT" -> {
                    return this.putAction(httpArgs);
                }
                case "DELETE" -> {
                    return this.deleteAction(httpArgs.getRequestLine().getPath().get(2));
                }
                default -> {
                    return ControllerResponse.of().status(400);
                }
            }
        } else {
            return ControllerResponse.of().status(400);
        }
    }

    private ControllerResponse indexAction(HttpRouterParameters httpArgs) {
        var query = httpArgs.getRequestLine().getQuery();
        var c = this.dao.getConnection();

        List<Map<String, Object>> customers = new LinkedList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        var startedAt = System.currentTimeMillis();
        try (var ps = c.prepareStatement("SELECT * FROM public.oop_customer ORDER BY lastname, firstname")) {
            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    customers.add(
                            Map.of(
                                    "id", rs.getInt(1),
                                    "firstname", rs.getString(2),
                                    "lastname", rs.getString(3)
                            )
                    );
                }
            }
            System.out.printf("customer list done in %d ms\n", System.currentTimeMillis() - startedAt);

            return ControllerResponse.of(objectMapper.writeValueAsString(customers))
                    .header("Content-Type", "application/json");
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private ControllerResponse postAction(HttpRouterParameters args) {
        var query = args.getRequestLine().getQuery();
        var c = this.dao.getConnection();

        var startedAt = System.currentTimeMillis();
        try (var ps = c.prepareStatement("INSERT INTO public.oop_customer (firstname, lastname) VALUES (?, ?)")) {
            ps.setString(1, query.stream().filter(pair -> pair.containsKey("firstname")).map(pair -> pair.get("firstname")).findFirst().orElseThrow(RuntimeException::new));
            ps.setString(2, query.stream().filter(pair -> pair.containsKey("lastname")).map(pair -> pair.get("lastname")).findFirst().orElseThrow(RuntimeException::new));
            System.out.println(ps.executeUpdate());
            System.out.printf("%d ms\n", System.currentTimeMillis() - startedAt);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ControllerResponse.of();
    }

    private ControllerResponse putAction(HttpRouterParameters args) {
        var query = args.getRequestLine().getQuery();
        var c = this.dao.getConnection();

        var startedAt = System.currentTimeMillis();
        try (var ps = c.prepareStatement("UPDATE public.oop_customer SET firstname = ?, lastname = ? WHERE id = ?")) {
            ps.setString(1, query.stream().filter(pair -> pair.containsKey("firstname")).map(pair -> pair.get("firstname")).findFirst().orElseThrow(RuntimeException::new));
            ps.setString(2, query.stream().filter(pair -> pair.containsKey("lastname")).map(pair -> pair.get("lastname")).findFirst().orElseThrow(RuntimeException::new));
            ps.setInt(3, query.stream().filter(pair -> pair.containsKey("id")).map(pair -> Integer.parseInt(pair.get("id"))).findFirst().orElseThrow(RuntimeException::new));
            System.out.println(ps.executeUpdate());
            System.out.printf("%d ms\n", System.currentTimeMillis() - startedAt);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ControllerResponse.of().status(204);
    }

    private ControllerResponse deleteAction(String index) {
        var idx = Integer.parseInt(index);
        var c = this.dao.getConnection();

        var startedAt = System.currentTimeMillis();
        try (var ps = c.prepareStatement("DELETE FROM public.oop_customer WHERE id = ?")) {
            ps.setInt(1, idx);
            System.out.println(ps.executeUpdate());
            System.out.printf("Deleted in %d ms\n", System.currentTimeMillis() - startedAt);

            return ControllerResponse.of().status(204);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ControllerResponse deleteInFile(String index) {
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

    public ControllerResponse indexAfishaAction(HttpRouterParameters httpArgs) {
        if (String.join("/", httpArgs.getRequestLine().getPath()).endsWith("/pdf")) {
            var channel = Grpc.newChannelBuilder("puppeteer:50051", InsecureChannelCredentials.create())
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
            //addRecordsToAirtable(seances);
            fillPostgres(seances);
            return objectMapper.writeValueAsString(seances);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void fillPostgres(List<Seance> seances) {
        var c = this.dao.getConnection();
        seances.forEach(s -> {
            try (var ps = c.prepareStatement("INSERT INTO public.oop_movie_theatre (name) VALUES (?)")) {
                ps.setString(1, s.getCinema());
                System.out.println(ps.executeUpdate());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void addRecordsToAirtable(List<Seance> seances) throws JsonProcessingException {
        Airtable airtable = new Airtable();
        if (!airtable.isTableExist("Seances")) {
            airtable.createTable();
        }
        airtable.addRecords(seances);
    }

}
