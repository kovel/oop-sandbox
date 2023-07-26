package org.example.controller;

import java.util.Map;
import java.util.TreeMap;

public class ControllerResponse {
    private boolean isPrintable = true;
    private final byte[] data;
    private int statusCode = 200;
    private final Map<String, String> headers = new TreeMap<>();

    private ControllerResponse(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
    public  ControllerResponse header (String name, String value) {
        this.headers.put(name, value);
        return this;

    }

    public boolean isPrintable() {
        return isPrintable;
    }

    public void setPrintable(boolean printable) {
        isPrintable = printable;
    }

    public ControllerResponse status(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String renderHeaders () {
        StringBuilder builder = new StringBuilder();
        headers.forEach((name, value) -> builder.append(name).append(": ").append(value).append("\r\n"));
        return builder.toString();

    }

    public static ControllerResponse of(byte[] data) {
        return new ControllerResponse(data);
    }

    public static ControllerResponse of(String data) {
        return new ControllerResponse(data.getBytes());
    }

    public static ControllerResponse of() {
        return new ControllerResponse(new byte[0]);
    }

    @Override
    public String toString() {
        return new String(this.data);
    }
}
