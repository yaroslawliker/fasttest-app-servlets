package org.yarek.fasttestapp.routing.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class HttpHandlerBase implements HttpHandler {
    List<String> pathList;

    public HttpHandlerBase() {
        pathList = new ArrayList<>();
    }

    public HttpHandlerBase(List<String> pathList) {
        setProcessingPaths(pathList);
    }

    @Override
    public boolean isProcessingPath(String path) {
        return pathList.contains(path);
    }

    public void setProcessingPaths(List<String> pathList) {
        this.pathList = pathList;
    }

    public void addPath(String path) {
        pathList.add(path);
    }

    // --------------------
    // --- handling methods
    // --------------------

    /**
     * Mapping request method to appropriate class methods.
     *
     * @return view name
     */
    @Override
    public String handle(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> model) throws ServletException, IOException {
        String method = req.getMethod();
        switch (method) {
            case "GET":
                return doGet(req, resp, model);
            case "POST":
                return doPost(req, resp, model);
            case "UPDATE":
                return doUpdate(req, resp, model);
            case "DELETE":
                return doDelete(req, resp, model);

            default:
                throw new ServletException("Unsupported HTTP method: " + method + ". Add it into HttpHandlerBase class");
        }
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
