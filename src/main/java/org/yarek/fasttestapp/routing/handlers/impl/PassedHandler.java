package org.yarek.fasttestapp.routing.handlers.impl;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.yarek.fasttestapp.model.database.dao.QuizDAO;
import org.yarek.fasttestapp.model.database.dao.UserDAO;
import org.yarek.fasttestapp.model.database.entities.QuizResultData;
import org.yarek.fasttestapp.model.entities.User;
import org.yarek.fasttestapp.model.entities.quiz.Quiz;
import org.yarek.fasttestapp.model.entities.quiz.QuizBlank;
import org.yarek.fasttestapp.routing.handlers.HttpHandlerBase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PassedHandler extends HttpHandlerBase {

    QuizDAO quizDAO;
    UserDAO userDAO;

    public PassedHandler(UserDAO userDAO, QuizDAO quizDAO) {
        this.userDAO = userDAO;
        this.quizDAO = quizDAO;
        addPath("/passed-tests");
    }

    @Override
    protected String doGet(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> model) throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("user");

        List<QuizResultData> quizResults = quizDAO.getQuizResultsOfUser(user.getId());
        List<Quiz> quizzes = new ArrayList<>();
        List<String> authors = new ArrayList<>();
        List<Float> maxScores = new ArrayList<>();

        for (QuizResultData quizResultData : quizResults) {
            // Getting quiz
            Quiz quiz = quizDAO.getQuizById( quizResultData.getQuizId());
            quizzes.add( quiz );

            // Getting authors
            String username = userDAO.getUsernameByID(quiz.getOwnerID());
            authors.add(username);

            // Getting max scores
            QuizBlank blank = new QuizBlank(quiz);
            maxScores.add(blank.getMaxScore());
        }

        model.put("quizResults", quizResults);
        model.put("quizzes", quizzes);
        model.put("authors", authors);
        model.put("maxScores", maxScores);

        return "passed_quizzes";
    }
}
