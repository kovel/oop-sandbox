package org.example.router;

import org.example.annotation.processor.DAOProcessor;
import org.example.controller.IController;
import org.example.dao.ImageDAO;

public class DAORouterPlugin implements RouterPlugin {
    private final DAOProcessor daoProcessor;
    public DAORouterPlugin() {
        // annotation processor
        this.daoProcessor = new DAOProcessor();
        this.daoProcessor.registerDAO("image", ImageDAO.class); // ??
    }

    @Override
    public void initialize(Router<? extends Class<? extends IController>> router) {

    }

    @Override
    public void processController(IController controller) {
        this.daoProcessor.setController(controller)
                .run();
    }
}
