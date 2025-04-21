package org.yarek.fasttestapp.routing.handlers.impl;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.yarek.fasttestapp.routing.handlers.HttpHandler;
import org.yarek.fasttestapp.routing.handlers.HttpHandlerBase;

import java.io.IOException;
import java.util.Map;

public class LogoutHandler extends HttpHandlerBase {

    public LogoutHandler() {
        addPath("/logout");
    }


    @Override
    protected String doGet(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> model) throws ServletException, IOException {
        req.getSession().invalidate();
        return "home@redirect";
    }
}
