package org.example.controller;

import org.example.router.RouteParameters;

import java.net.UnknownHostException;
import java.util.List;

public interface IController {
    ControllerResponse run(RouteParameters args);
}
