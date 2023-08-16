package org.example;

import org.example.controller.*;
import org.example.router.DAORouterPlugin;
import org.example.router.FrontController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        var fc = new FrontController<>(args);

        var router = fc.getRouter();

        // plugins
        router.registerPlugin(new DAORouterPlugin());


        // controllers
        router.register("index", IndexController.class);
        router.register("backup", BackupController.class);
        router.register("favicon.ico", FavIconController.class);
        router.register("afisha", AfishaController.class);
        router.register("news", NewsController.class);

        fc.dispatch();
    }
}