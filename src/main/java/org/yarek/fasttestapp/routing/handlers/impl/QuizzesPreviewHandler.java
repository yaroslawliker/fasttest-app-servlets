package org.yarek.fasttestapp.routing.handlers.impl;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.yarek.fasttestapp.model.database.dao.QuizDAO;
import org.yarek.fasttestapp.model.database.dao.UserDAO;
import org.yarek.fasttestapp.model.database.entities.QuizPreviewData;
import org.yarek.fasttestapp.routing.handlers.HttpHandlerBase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuizzesPreviewHandler extends HttpHandlerBase {

    UserDAO userDAO;
    QuizDAO quizDAO;

    public QuizzesPreviewHandler(UserDAO userDAO, QuizDAO quizDAO) {
        this.userDAO = userDAO;
        this.quizDAO = quizDAO;
        addPath("/tests");
    }

    @Override
    protected String doGet(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> model) throws ServletException, IOException {
        List<QuizPreviewData> quizzes = quizDAO.getQuizPreviews(10);

        List<String> usernames = new ArrayList<String>();
        for (QuizPreviewData quizPreview : quizzes) {
            String userID = quizPreview.getOwnerID();
            String username = userDAO.getUsernameByID(userID);
            usernames.add(username);
        }

        model.put("quizzes", quizzes);
        model.put("usernames", usernames);
        return "quizzes";
    }
}
