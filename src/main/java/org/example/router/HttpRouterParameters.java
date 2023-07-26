package org.example.router;

import org.example.router.impl.HttpRouter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HttpRouterParameters implements RouteParameters {
    private HttpRouter.HttpRequestLine requestLine;

    private Map<String, String> headers;

    public HttpRouterParameters(HttpRouter.HttpRequestLine requestLine, Map<String, String> headers) {
        this.requestLine = requestLine;
        this.headers = Collections.unmodifiableMap(headers);
    }

    @Override
    public String getAction() {
        return null;
    }

    @Override
    public List<String> arguments() {
        return null;
    }

    public HttpRouter.HttpRequestLine getRequestLine() {
        return requestLine;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
