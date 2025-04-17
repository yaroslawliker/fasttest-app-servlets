package org.yarek.fasttestapp.routing.handlers.impl;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.yarek.fasttestapp.routing.handlers.HttpHandlerBase;

import java.io.IOException;
import java.util.Map;

public class HomeHandler extends HttpHandlerBase {

    public HomeHandler() {
        addPath("/home");
        addPath("/");
    }

    @Override
    protected String doGet(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> model) throws ServletException, IOException {
        return "index";
    }
}
