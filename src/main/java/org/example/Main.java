package org.example;

import org.example.controller.AfishaController;
import org.example.controller.BackupController;
import org.example.controller.FavIconController;
import org.example.controller.IndexController;
import org.example.router.DAORouterPlugin;
import org.example.router.FrontController;

public class Main {
    public static void main(String[] args) {
        var fc = new FrontController<>(args);

        var router = fc.getRouter();

        // plugins
        router.registerPlugin(new DAORouterPlugin());

        // controllers
        router.register("index", IndexController.class);
        router.register("backup", BackupController.class);
        router.register("favicon.ico", FavIconController.class);
        router.register("afisha", AfishaController.class);

        fc.dispatch();
    }
}