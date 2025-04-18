package org.yarek.fasttestapp.routing.handlers.impl;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.yarek.fasttestapp.model.database.dao.UserDAO;
import org.yarek.fasttestapp.model.entities.User;
import org.yarek.fasttestapp.routing.handlers.HttpHandlerBase;

import java.util.Map;
import java.util.Objects;

public class LoginHandler extends HttpHandlerBase {

    UserDAO userDAO;

    public LoginHandler(UserDAO userDAO) {
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
            return "wrong_req";
        }

        User user = userDAO.getUser(username);
        if (user == null || !Objects.equals(password, user.getPassword())) {
            req.setAttribute("notAuthenticatedError", true);
            return "login";
        }
            HttpSession session = req.getSession();
            session.setAttribute("user", user);
            return "home@redirect";
    }
}
