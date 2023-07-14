package org.example.controller;

import org.example.router.RouteParameters;

public class FavIconController implements IController {


    @Override
    public ControllerResponse run(RouteParameters args) {
        return ControllerResponse.of();
    }
}
