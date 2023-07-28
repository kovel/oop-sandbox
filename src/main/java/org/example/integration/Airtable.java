package org.example.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.java.Log;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.example.integration.entities.Field;
import org.example.integration.entities.Table;
import org.example.parser.Seance;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Log
public class Airtable {

    ObjectMapper objectMapper = new ObjectMapper();

    private Table table() {
        Table table = new Table();
        table.setDescription("Table with film sessions");
        table.setName("Seances");
        List<Field> fields = new ArrayList<>();
        fields.add(new Field("filmName", "filmName", "singleLineText"));
        fields.add(new Field("dateTime", "dateTime", "singleLineText"));
        fields.add(new Field("cinema", "cinema", "singleLineText"));
        table.setFields(fields);
        return table;
    }

    public void createTable() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            log.info(String.format("Create table, url: %s", AirtableProperties.tablesUrl()));
            HttpPost request = new HttpPost(AirtableProperties.tablesUrl());
            request.addHeader("Authorization", String.format("Bearer %s", AirtableProperties.token()));
            request.addHeader("Content-Type", "application/json");
            StringEntity entity = new StringEntity(objectMapper.writeValueAsString(table()), ContentType.APPLICATION_JSON);
            request.setEntity(entity);

            String response = client.execute(request, httpResponse ->
                    String.format("%s %s %s", httpResponse.getCode(), httpResponse.getReasonPhrase(),
                            EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8)));
            log.info(String.format("Table created: %s", response));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Table> getSchemaTables() {
        List<Table> tables;
        log.info(String.format("Get tables, url: %s", AirtableProperties.tablesUrl()));
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(AirtableProperties.tablesUrl());
            request.addHeader("Authorization", String.format("Bearer %s", AirtableProperties.token()));
            request.addHeader("Content-Type", "application/json");
            String response = client.execute(request, new AirtableResponseHandler());
            log.info(response);
            tables = objectMapper.readValue(response, new TypeReference<>() {
            });
            log.info(String.format("Tables: %s", tables));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return tables;
    }

    public boolean isTableExist(String tableName) {
        return getSchemaTables().stream()
                .anyMatch(table -> table.getName().equals(tableName));
    }

    public void addRecords(List<Seance> seances) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            log.info(String.format("Add records, url: %s", AirtableProperties.tableUrl()));
            HttpPost request = new HttpPost(AirtableProperties.tableUrl());
            request.addHeader("Authorization", String.format("Bearer %s", AirtableProperties.token()));
            request.addHeader("Content-Type", "application/json");

            String requestBody = seancesToRecords(seances);
            StringEntity entity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
            request.setEntity(entity);

            String response = client.execute(request, httpResponse ->
                    String.format("%s %s %s", httpResponse.getCode(), httpResponse.getReasonPhrase(),
                            EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8)));
            log.info(String.format("Records added: %s", response));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("Added records");
    }

    private String seancesToRecords(List<Seance> seances) {
        ArrayNode fieldsArray = objectMapper.createArrayNode();
        for (Seance seance : seances) {
            ObjectNode outerObject = objectMapper.createObjectNode();
            outerObject.putPOJO("fields", objectMapper.valueToTree(seance));
            fieldsArray.add(outerObject);
        }
        ObjectNode outerObject = objectMapper.createObjectNode();
        outerObject.putPOJO("records", fieldsArray);
        return outerObject.toString();
    }
}

class AirtableResponseHandler implements HttpClientResponseHandler<String> {
    @Override
    public String handleResponse(ClassicHttpResponse response) throws HttpException, IOException {
        String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        return responseBody.substring(responseBody.indexOf('['), responseBody.length() - 1);
    }
}


