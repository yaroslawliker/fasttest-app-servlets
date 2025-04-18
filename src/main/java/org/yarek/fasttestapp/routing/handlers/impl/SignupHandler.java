package org.yarek.fasttestapp.routing.handlers.impl;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.yarek.fasttestapp.model.database.dao.UserDAO;
import org.yarek.fasttestapp.model.entities.User;
import org.yarek.fasttestapp.model.exceptions.UsernameAlreadyExistsException;
import org.yarek.fasttestapp.routing.handlers.HttpHandlerBase;

import java.io.IOException;
import java.util.Map;

public class SignupHandler extends HttpHandlerBase {

    private final UserDAO userDAO;

    public SignupHandler(@NotNull UserDAO userDAO) {
        this.userDAO = userDAO;
        addPath("/signup");
        addPath("/register");
    }

    @Override
    protected String doGet(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> model) throws ServletException, IOException {
        return "signup";
    }

    @Override
    protected String doPost(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> model) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");

        if (isUsernameValid(username) && isPasswordValid(password, confirmPassword)) {

            User newUser = new User(username, password, User.Role.USER);

            try {
                userDAO.registerUser(newUser);
            } catch (UsernameAlreadyExistsException e) {
                model.put("userExistsError", true);
                return "signup";
            }

            return "index";
        } else {
            return "signup";
        }
    }

    private boolean isUsernameValid(String username) {
        return !(username == null || username.length() < 5 || username.length() > 20);
    }

    private boolean isPasswordValid(String password, String confirmPassword) {
        if (password == null || confirmPassword == null || password.length() < 6 || password.length() > 20) {
            return false;
        }
        return password.equals(confirmPassword);
    }
}
