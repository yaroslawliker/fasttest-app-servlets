package org.yarek.fasttestapp.routing.handlers.impl;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.yarek.fasttestapp.model.database.dao.UserDAO;
import org.yarek.fasttestapp.model.entities.quiz.Quiz;
import org.yarek.fasttestapp.routing.handlers.HttpHandlerBase;

import java.io.IOException;
import java.util.Map;

public class QuizStartedHandler extends HttpHandlerBase {

    UserDAO userDAO;

    public QuizStartedHandler(UserDAO userDAO) {
        this.userDAO = userDAO;
        addPath("^/tests/[^/]+/started$");
    }

    @Override
    protected String doGet(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> model) throws ServletException, IOException {
        String uri = req.getRequestURI();
        Quiz quiz = (Quiz) req.getAttribute("quiz");
        String authorUsername = userDAO.getUsernameByID(quiz.getOwnerID());
        req.setAttribute("username", authorUsername);
        return "quiz_started";
    }
}
