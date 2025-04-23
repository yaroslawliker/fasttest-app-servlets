package org.yarek.fasttestapp.routing.handlers.impl;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.yarek.fasttestapp.model.database.dao.QuizDAO;
import org.yarek.fasttestapp.model.database.dao.UserDAO;
import org.yarek.fasttestapp.model.entities.User;
import org.yarek.fasttestapp.model.entities.quiz.Quiz;
import org.yarek.fasttestapp.routing.handlers.HttpHandlerBase;

import java.io.IOException;
import java.util.Map;

public class QuizStartedHandler extends HttpHandlerBase {

    UserDAO userDAO;
    QuizDAO quizDAO;

    public QuizStartedHandler(UserDAO userDAO, QuizDAO quizDAO) {
        this.userDAO = userDAO;
        this.quizDAO = quizDAO;
        addPath("^/tests/[^/]+/started$");
    }

    @Override
    protected String doGet(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> model) throws ServletException, IOException {
        Quiz quiz = (Quiz) req.getAttribute("quiz");
        User user = (User) req.getSession().getAttribute("user");

        if (!quizDAO.isUserPassingQuiz(user.getId(), quiz.getId())){
            return "tests/" + quiz.getId() + "/preview@redirect";
        }

        String authorUsername = userDAO.getUsernameByID(quiz.getOwnerID());
        req.setAttribute("username", authorUsername);
        return "quiz_started";
    }
}
