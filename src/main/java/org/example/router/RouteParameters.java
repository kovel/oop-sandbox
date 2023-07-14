package org.example.router;

import java.util.List;

public class RouteParameters {
    private static final Integer ACTION_INDEX = 0;

    private final List<String> parameters;

    public RouteParameters(List<String> parameters) {
        this.parameters = parameters.subList(Router.CONTROLLER_INDEX + 1, parameters.size());
    }

    public String getAction() {
        return this.parameters.get(ACTION_INDEX);
    }

    public List<String> arguments() {
        return this.parameters.subList(ACTION_INDEX + 1, this.parameters.size());
    }
}
