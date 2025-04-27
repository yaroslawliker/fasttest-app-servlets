package org.yarek.fasttestapp.routing.handlers.impl.account;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yarek.fasttestapp.model.database.dao.UserDAO;
import org.yarek.fasttestapp.model.entities.User;
import org.yarek.fasttestapp.routing.handlers.HttpHandlerBase;

import java.util.Map;
import java.util.Objects;

public class LoginHandler extends HttpHandlerBase {

    UserDAO userDAO;
    Logger logger;


    public LoginHandler(UserDAO userDAO) {
        logger = LoggerFactory.getLogger(LoginHandler.class);
        this.userDAO = userDAO;
        addPath("/login");
    }

    @Override
    protected String doGet(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> model) {
        return "login";
    }

    @Override
    protected String doPost(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> model) {


        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (username == null || password == null) {
            logger.warn("Wrong post request: no username or password params provided.");
            return "wrong_req";
        }

        User user = userDAO.getUser(username);
        if (user == null || !Objects.equals(password, user.getPassword())) {
            logger.info("Attempting login '{}': failed.", username);
            req.setAttribute("notAuthenticatedError", true);
            return "login";
        }
            HttpSession session = req.getSession();
            session.setAttribute("user", user);
            logger.info("Attempting login '{}': success.", username);
            return "home@redirect";
    }
}
