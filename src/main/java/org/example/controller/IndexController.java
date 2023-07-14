package org.example.controller;

import org.example.annotation.DAO;
import org.example.dao.ImageDAO;
import org.example.router.RouteParameters;

import java.util.stream.Collectors;

public class IndexController implements IController {
    @DAO(value = "image", path = "./images.txt")
    private ImageDAO dao;
    @DAO(value = "image", path = "./images-backup.txt")
    private ImageDAO backupDAO;

    @Override
    public ControllerResponse run(RouteParameters parameters) {
        switch (parameters.getAction()) {
            case "list" -> {
                return ControllerResponse.of(String.join("\n", this.dao.lines())).header("Content-Type",
                        "text/plain");
            }
            case "create" -> {
                this.dao.create(parameters.arguments());
                this.backupDAO.create(parameters.arguments());
                return ControllerResponse.of("<h1>create successfull</h1>");
            }
            case "delete" -> {
                this.dao.delete(parameters.arguments());
                return ControllerResponse.of("<h1>deleted</h1>");
            }
        }
        return ControllerResponse.of();
    }
}
