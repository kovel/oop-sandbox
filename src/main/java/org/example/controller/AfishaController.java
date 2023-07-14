package org.example.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.parser.Parser;
import org.example.parser.Seance;
import org.example.router.RouteParameters;

import java.util.List;

public class AfishaController implements IController {

    @Override
    public ControllerResponse run(RouteParameters args) {
        String url = "https://afisha.relax.by/kino/minsk/";
        List<Seance> seances = new Parser(url).parseSchedule();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            String response = objectMapper.writeValueAsString(seances);
            return ControllerResponse.of(response).header("Content-Type", "application/json");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
