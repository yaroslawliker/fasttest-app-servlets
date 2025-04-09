package org.yarek.fasttestapp.routing.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class HttpHandlerBase implements HttpHandler {
    List<String> pathList;

    HttpHandlerBase() {
        pathList = new ArrayList<>();
    }

    HttpHandlerBase(List<String> pathList) {
        setProcessingPaths(pathList);
    }

    @Override
    public boolean isProcessingPath(String path) {
        return pathList.contains(path);
    }

    public void setProcessingPaths(List<String> pathList) {
        this.pathList = pathList;
    }

    // --------------------
    // --- handling methods
    // --------------------

    /**
     * Mapping request method to appropriate class methods.
     */
    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        switch (method) {
            case "GET":
                doGet(req, resp);
                break;
            case "POST":
                doPost(req, resp);
                break;
            case "UPDATE":
                doUpdate(req, resp);
                break;
            case "DELETE":
                doDelete(req, resp);
                break;

            default:
                throw new ServletException("Unsupported HTTP method: " + method + ". Add it into HttpHandlerBase class");
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        throw new ServletException("GET not implemented yet");
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        throw new ServletException("POST not implemented yet");
    }

    protected void doUpdate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        throw new ServletException("UPDATE not implemented yet");
    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        throw new ServletException("UPDATE not implemented yet");
    }

    // TODO: maybe more methods


}
