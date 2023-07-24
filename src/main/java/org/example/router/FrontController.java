package org.example.router;

import org.example.controller.IController;

public class FrontController<C extends Class<? extends IController>> {
    private final Router<C> router;

    public FrontController(String[] args) {
        this.router = Router.of(args);
    }

    public Router<C> getRouter() {
        return router;
    }

    public void dispatch() {
        var response = router.process(null);
        if (response.isPrintable()) {
            System.out.println(response);
        }
    }
}
