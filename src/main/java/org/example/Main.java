package org.example;

import org.example.controller.*;
import org.example.router.DAORouterPlugin;
import org.example.router.Router;

public class Main {
    public static void main(String[] args) {
        var router = Router.of(args);

        // plugins
        router.registerPlugin(new DAORouterPlugin());

        // controllers
        router.register("index", IndexController.class);
        router.register("backup", BackupController.class);
        router.register("favicon.ico", FavIconController.class);
        router.register("afisha", AfishaController.class);

        router.process();
    }
}