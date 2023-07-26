package org.example.router.impl;

import org.example.controller.ControllerResponse;
import org.example.controller.IController;
import org.example.router.CliRouterParameters;
import org.example.router.Router;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class CliRouter<T extends Class<? extends IController>> extends Router<T> {
    public static final Integer CONTROLLER_INDEX = 0;

    public static final Integer ACTION_INDEX = 0;

    private final List<String> arguments;

    public CliRouter(String[] args) {
        this.arguments = List.of(args);
    }

    public List<String> getArguments() {
        return arguments;
    }

    private String getControllerName() {
        return this.arguments.get(CONTROLLER_INDEX);
    }

    @Override
    public ControllerResponse process(IController ctl) {
        try {
            var controllerCls = this.controllers.get(this.getControllerName());
            var controller = controllerCls.getDeclaredConstructor().newInstance();

            // wiring with annotation processors
            super.process(controller);

            // running controller
            return controller.run(new CliRouterParameters(arguments));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
