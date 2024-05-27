package com.example.web_server.core.http;

import java.util.Map;

public class Http {
    private String method;
    private String body;
    private String path;
    private Map<String, String> headers;

    public Http(String method, String body, String path, Map<String, String> headers) {
        this.method = method;
        this.body = body;
        this.path = path;
        this.headers = headers;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
