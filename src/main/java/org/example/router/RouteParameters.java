package org.example.router;

import java.util.List;

public interface RouteParameters {

    String getAction();

    List<String> arguments();
}
