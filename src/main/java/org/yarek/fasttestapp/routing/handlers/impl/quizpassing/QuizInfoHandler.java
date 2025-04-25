package org.yarek.fasttestapp.routing.handlers.impl.quizpassing;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.yarek.fasttestapp.model.database.dao.QuizDAO;
import org.yarek.fasttestapp.model.database.dao.UserDAO;
import org.yarek.fasttestapp.routing.handlers.HttpHandlerBase;

import java.io.IOException;
import java.util.Map;

public class QuizInfoHandler extends HttpHandlerBase {

    UserDAO userDAO;
    QuizDAO quizDAO;

    public QuizInfoHandler(UserDAO userDAO, QuizDAO quizDAO) {
        this.userDAO = userDAO;
        this.quizDAO = quizDAO;

        addPath("^/tests/[^/]+/info$");
    }

    @Override
    protected String doGet(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> model) throws ServletException, IOException {

        return "quiz_info";
    }
}
