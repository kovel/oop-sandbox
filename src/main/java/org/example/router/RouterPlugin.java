package org.example.router;

import org.example.controller.IController;

public interface RouterPlugin {
    void initialize(Router<? extends Class<? extends IController>> router);

    void processController(IController controller);
}
