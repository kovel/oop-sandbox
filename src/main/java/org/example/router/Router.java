package org.example.router;

import org.example.controller.ControllerResponse;
import org.example.controller.IController;
import org.example.router.impl.CliRouter;
import org.example.router.impl.HttpRouter;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class Router<T extends Class<? extends IController>> {
    private final List<RouterPlugin> plugins = new LinkedList<>();
    protected final Map<String, T> controllers = new HashMap<>();

    public static <T extends Class<? extends IController>> Router<T> of(String[] args) {
        Router<T> router;
        switch (Optional.ofNullable(System.getenv("ROUTER")).orElse("")) {
            case "http" -> router = new HttpRouter<T>(args);
            default -> router = new CliRouter<T>(args);
        }
        return router;
    }

    public void registerPlugin(RouterPlugin plugin) {
        this.plugins.add(plugin);
    }

    public void register(String route, T controllerCls) {
        this.controllers.put(route, controllerCls);
    }

    public ControllerResponse process(IController controller) {
        this.plugins.forEach(plg -> plg.processController(controller));
        return ControllerResponse.of();
    }
}
