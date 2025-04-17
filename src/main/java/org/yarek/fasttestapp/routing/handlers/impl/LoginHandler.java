package org.yarek.fasttestapp.routing.handlers.impl;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.yarek.fasttestapp.routing.handlers.HttpHandlerBase;

import java.util.Map;

public class LoginHandler extends HttpHandlerBase {

    public LoginHandler() {
        addPath("/login");
    }

    @Override
    protected String doGet(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> model) {
        return "login";
    }
}
