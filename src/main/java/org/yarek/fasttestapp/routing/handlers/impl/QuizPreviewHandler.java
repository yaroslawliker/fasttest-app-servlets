package org.yarek.fasttestapp.routing.handlers.impl;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.yarek.fasttestapp.model.database.dao.QuizDAO;
import org.yarek.fasttestapp.model.database.dao.UserDAO;
import org.yarek.fasttestapp.model.entities.quiz.Quiz;
import org.yarek.fasttestapp.routing.handlers.HttpHandlerBase;

import java.io.IOException;
import java.util.Map;

public class QuizPreviewHandler extends HttpHandlerBase {

    private UserDAO userDAO;
    private QuizDAO quizDAO;

    public QuizPreviewHandler(UserDAO userDAO, QuizDAO quizDAO) {
        this.userDAO = userDAO;
        this.quizDAO = quizDAO;
        addPath("^/tests/[^/]+/preview$");
    }

    @Override
    protected String doGet(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> model) throws ServletException, IOException {
        // set quiz
        String uri = req.getRequestURI();
        String quizId = uri.substring(uri.lastIndexOf("tests/") + 6, uri.lastIndexOf("/preview"));

        Quiz quiz = quizDAO.getQuizById(quizId);
        if (quiz == null) {
            return "wrong_req";
        }
        String username = userDAO.getUsernameByID(quiz.getOwnerID());

        model.put("quiz", quiz);
        model.put("username", username);
        model.put("questionAmount", quiz.getQuestions().size());

        return "quiz_preview";
    }
}
