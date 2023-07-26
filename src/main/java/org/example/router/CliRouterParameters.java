package org.example.router;

import org.example.router.impl.CliRouter;

import java.util.List;

public class CliRouterParameters implements RouteParameters {
    private final List<String> parameters;

    public CliRouterParameters(List<String> parameters) {
        this.parameters = parameters.subList(CliRouter.CONTROLLER_INDEX + 1, parameters.size());
    }


    public String getAction() {
        return this.parameters.get(CliRouter.ACTION_INDEX);
    }

    public List<String> arguments() {
        return this.parameters.subList(CliRouter.ACTION_INDEX + 1, this.parameters.size());
    }
}
