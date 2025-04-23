package org.yarek.fasttestapp.routing.handlers.impl.account;

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
        String roleStr = req.getParameter("role");

        if (username == null || password == null || confirmPassword == null) {
            return "wrong_req";
        }

        User.Role role = mapRole(roleStr);

        if (isUsernameValid(username) && isPasswordValid(password, confirmPassword) && role != null) {

            User newUser = new User(username, password, role);

            try {
                userDAO.registerUser(newUser);
            } catch (UsernameAlreadyExistsException e) {
                model.put("userExistsError", true);
                return "signup";
            }

            return "index@redirect";
        } else {
            return "wrong_req";
        }
    }

    private boolean isUsernameValid(String username) {
        return username.length() >= 5 && username.length() <= 20;
    }

    private boolean isPasswordValid(String password, String confirmPassword) {
        if (confirmPassword == null || password.length() < 6 || password.length() > 20) {
            return false;
        }
        return password.equals(confirmPassword);
    }

    private User.Role mapRole(String roleStr) {
        return switch (roleStr) {
            case "user" -> User.Role.USER;
            case "teacher" -> User.Role.TEACHER;
            default -> null;
        };
    }
}
