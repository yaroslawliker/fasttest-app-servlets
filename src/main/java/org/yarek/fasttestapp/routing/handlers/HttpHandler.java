package org.yarek.fasttestapp.routing.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Class gives interface for making handlers, processing HTTP requests.
 */
public interface HttpHandler {

    public abstract boolean isProcessingPath(String path);
    public abstract void handle(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;
}
