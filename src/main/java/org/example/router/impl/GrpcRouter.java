package org.example.router.impl;

import org.example.controller.IController;
import org.example.router.Router;

public class GrpcRouter<T extends Class<? extends IController>> extends Router<T> {
}
