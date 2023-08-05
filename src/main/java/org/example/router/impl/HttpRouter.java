package org.example.router.impl;

import org.example.controller.ControllerResponse;
import org.example.controller.IController;
import org.example.router.HttpRouterParameters;
import org.example.router.Router;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class HttpRouter<T extends Class<? extends IController>> extends Router<T> {
    public static final Integer CONTROLLER_INDEX = 1;

    public static final Integer ACTION_INDEX = 2;


    public HttpRouter(String[] args) {
        ;
    }

    @Override
    public ControllerResponse process(IController controller) {
        System.out.println("Starting HTTP server...");
        try (var ss = new ServerSocket(8080)) {
            do {
                var s = ss.accept();
                new Thread(new Worker(s)).start();
            } while(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ControllerResponse processHttpArguments(HttpRequestLine requestLineDto, HashMap<String, String> headers) {
        try {
            var cls = this.controllers.get(requestLineDto.path.get(1));
            var controller = cls.getDeclaredConstructor().newInstance();

            super.process(controller);

            var response = controller.run(new HttpRouterParameters(requestLineDto, headers));
            response.setPrintable(false);
            return response;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static class HttpRequestLine {
        private final String method;
        private final List<String> path;
        private final String protocol;

        public HttpRequestLine(String method, List<String> path, String protocol) {
            this.method = method;
            this.path = path;
            this.protocol = protocol;
        }

        public String getMethod() {
            return method;
        }

        public List<String> getPath() {
            return path;
        }

        public String getProtocol() {
            return protocol;
        }
    }

    private class Worker implements Runnable {
        private final Socket s;

        public Worker(Socket s) {
            this.s = s;
        }

        @Override
        public void run() {
            try (var scanner = new Scanner(this.s.getInputStream())) {
                var requestLine = scanner.nextLine();
                var requestLineParts = Arrays.asList(requestLine.split(" "));

                // parsing request line
                var method = requestLineParts.get(0);
                var path = requestLineParts.get(1);
                var protocol = requestLineParts.get(1);
                var pathParts = Arrays.asList(path.split("/"));
                var requestLineDto = new HttpRequestLine(method, pathParts, protocol);

                // parsing headers
                String headerLine;
                var headers = new HashMap<String, String>();
                do {
                    headerLine = scanner.nextLine();
                    var separator = headerLine.indexOf(':');
                    headers.put(headerLine.substring(0, separator), headerLine.substring(separator + 1).trim());
                } while (headerLine.isBlank());

                var response = processHttpArguments(requestLineDto, headers);
                writeResponse(this.s.getOutputStream(), response.getStatusCode(), response.getData(), response.renderHeaders());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        private void writeResponse(OutputStream os, int statusCode, byte[] content, String headers) throws Exception {
            String response = "HTTP/1.1 " + statusCode + " OK\r\n" +
                    "Server: Http Server/2009-09-09\r\n" +
                    headers+
                    "Content-Length: " + content.length + "\r\n" +
                    "Connection: close\r\n\r\n";
            os.write(response.getBytes());
            os.write(content);
            os.flush();
        }
    }
}
