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
import org.yarek.fasttestapp.routing.handlers.DataTimePresenter;
import org.yarek.fasttestapp.routing.handlers.HttpHandlerBase;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PassedHandler extends HttpHandlerBase {

    private QuizDAO quizDAO;
    private UserDAO userDAO;

    final String NOT_FINISHED_TEXT = "not finished yet";

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
        List<String> startTimes = new ArrayList<>();
        List<String> finishTimes = new ArrayList<>();

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

            // Format times
            String[] times = DataTimePresenter.presentDateTimes(
                    quizResultData.getStartTime(),
                    quizResultData.getFinishTime(),
                    NOT_FINISHED_TEXT);
            startTimes.add(times[0]);
            finishTimes.add(times[1]);
        }

        model.put("quizResults", quizResults);
        model.put("quizzes", quizzes);
        model.put("authors", authors);
        model.put("maxScores", maxScores);
        model.put("startTimes", startTimes);
        model.put("finishTimes", finishTimes);

        return "passed_quizzes";
    }


}
