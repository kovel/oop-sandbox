package org.example.router.impl;

import org.example.controller.IController;
import org.example.router.Router;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class HttpRouter<T extends Class<? extends IController>> extends Router<T> {
    public HttpRouter(String[] args) {
        super(args);
    }

    @Override
    public void process() {
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

    private class Worker implements Runnable {
        private final Socket s;

        public Worker(Socket s) {
            this.s = s;
        }

        @Override
        public void run() {
            try (var scanner = new Scanner(this.s.getInputStream())) {
                var requestLine = scanner.nextLine();
                var path = Arrays.asList(requestLine.split(" ")).get(1);
                var pathParts = Arrays.asList(path.split("/"));

                var response = HttpRouter.this.processArguments(pathParts.subList(1, pathParts.size()));
                writeResponse(this.s.getOutputStream(), new String(response.getData()), response.renderHeaders());
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


        private void writeResponse(OutputStream os, String s, String headers) throws Exception {
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Server: Http Server/2009-09-09\r\n" +
                    headers+
                    "Content-Length: " + s.length() + "\r\n" +
                    "Connection: close\r\n\r\n";
            String result = response + s;
            os.write(result.getBytes());
            os.flush();
        }
    }
}
