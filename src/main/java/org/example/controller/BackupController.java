package org.example.controller;

import org.example.annotation.DAO;
import org.example.dao.ImageDAO;
import org.example.router.RouteParameters;

import java.util.Objects;
import java.util.stream.Collectors;

public class BackupController implements IController {
    @DAO(value = "image", path = "./images-backup.txt")
    private ImageDAO backupDAO;


    @Override
    public ControllerResponse run(RouteParameters args) {
        var results = this.backupDAO
                .lines()
                .stream()
                .filter(Objects::nonNull)
                .filter(line -> !line.isEmpty())
                .filter(line -> !line.isBlank())
                .collect(Collectors.joining("\n"));
        return ControllerResponse.of(results);
    }
}
