package org.yarek.fasttestapp.routing.handlers.impl;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.yarek.fasttestapp.model.database.dao.QuizDAO;
import org.yarek.fasttestapp.model.database.entities.QuizPreviewData;
import org.yarek.fasttestapp.model.entities.User;
import org.yarek.fasttestapp.routing.handlers.HttpHandlerBase;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MyQuizzesHandler extends HttpHandlerBase {

    private final QuizDAO quizDAO;

    public MyQuizzesHandler(QuizDAO quizDAO) {
        this.quizDAO = quizDAO;

        addPath("/my-tests");
    }

    @Override
    protected String doGet(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> model) throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("user");

        List<QuizPreviewData> quizzes = quizDAO.getQuizPreviewsOfAuthor(user.getId(), 10);
        model.put("quizzes", quizzes);
        model.put("username", user.getUsername());

        return "my_quizzes";
    }
}
