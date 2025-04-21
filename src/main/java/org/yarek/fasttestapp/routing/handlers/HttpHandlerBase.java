package org.yarek.fasttestapp.routing.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class HttpHandlerBase implements HttpHandler {
    List<String> pathRegExList;

    public HttpHandlerBase() {
        pathRegExList = new ArrayList<>();
    }

    public HttpHandlerBase(List<String> pathRegExList) {
        setProcessingPaths(pathRegExList);
    }

    @Override
    public boolean isProcessingPath(String path) {
        for (String pathRegEx : pathRegExList) {
            if (path.matches(pathRegEx)) {
                return true;
            }
        }
        return false;
    }

    public void setProcessingPaths(List<String> pathList) {
        this.pathRegExList = pathList;
    }

    public void addPath(String path) {
        pathRegExList.add(path);
    }

    // --------------------
    // --- handling methods
    // --------------------

    /**
     * Mapping the request method to appropriate class methods.
     *
     * @return view name
     */
    @Override
    public String handle(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> model) throws ServletException, IOException {
        String method = req.getMethod();
        return switch (method) {
            case "GET" -> doGet(req, resp, model);
            case "POST" -> doPost(req, resp, model);
            case "UPDATE" -> doUpdate(req, resp, model);
            case "DELETE" -> doDelete(req, resp, model);
            default ->
                    throw new ServletException("Unsupported HTTP method: " + method + ". Add it into HttpHandlerBase class");
        };
    }

    protected String doGet(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> model) throws ServletException, IOException {
        throw new ServletException("GET not implemented yet");
    }

    protected String doPost(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> model) throws ServletException, IOException {
        throw new ServletException("POST not implemented yet");
    }

    protected String doUpdate(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> model) throws ServletException, IOException {
        throw new ServletException("UPDATE not implemented yet");
    }

    protected String doDelete(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> model) throws ServletException, IOException {
        throw new ServletException("UPDATE not implemented yet");
    }

    // TODO: maybe more methods


}
