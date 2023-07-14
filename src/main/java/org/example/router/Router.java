package org.example.router;

import org.example.controller.ControllerResponse;
import org.example.controller.IController;
import org.example.router.impl.CliRouter;
import org.example.router.impl.HttpRouter;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class Router<T extends Class<? extends IController>> {
    static final Integer CONTROLLER_INDEX = 0;

    private final List<String> arguments;
    private final List<RouterPlugin> plugins = new LinkedList<>();
    private final Map<String, T> controllers = new HashMap<>();

    public Router(String[] args) {
        this.arguments = Arrays.asList(args);
    }

    public List<String> getArguments() {
        return arguments;
    }

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

    private String getControllerName() {
        return this.arguments.get(CONTROLLER_INDEX);
    }

    public ControllerResponse processArguments(List<String> arguments) {
        try {
            var controllerCls = this.controllers.get(this.getControllerName());
            var controller = controllerCls.getDeclaredConstructor().newInstance();

            // wiring with annotation processors
            this.plugins.forEach(plg -> plg.processController(controller));

            // running controller
            return controller.run(new RouteParameters(arguments));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract void process();
}
