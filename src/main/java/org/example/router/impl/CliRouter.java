package org.example.router.impl;

import org.example.controller.IController;
import org.example.router.Router;

public class CliRouter<T extends Class<? extends IController>> extends Router<T> {
    public CliRouter(String[] args) {
        super(args);
    }

    @Override
    public void process() {
        var response = this.processArguments(this.getArguments());
        System.out.println(new String(response.getData()));
    }
}
