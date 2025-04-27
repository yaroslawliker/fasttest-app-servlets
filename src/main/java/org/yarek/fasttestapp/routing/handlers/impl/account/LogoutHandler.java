package org.yarek.fasttestapp.routing.handlers.impl.account;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yarek.fasttestapp.model.entities.User;
import org.yarek.fasttestapp.routing.handlers.HttpHandlerBase;

import java.io.IOException;
import java.util.Map;

public class LogoutHandler extends HttpHandlerBase {
    Logger logger = LoggerFactory.getLogger(LogoutHandler.class);

    public LogoutHandler() {
        addPath("/logout");
    }


    @Override
    protected String doGet(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> model) throws ServletException, IOException {
        logger.info("Logged out '{}'", ((User) req.getSession().getAttribute("user")).getUsername());

        req.getSession().invalidate();
        return "home@redirect";
    }
}
