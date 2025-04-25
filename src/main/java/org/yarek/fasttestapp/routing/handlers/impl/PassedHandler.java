package org.yarek.fasttestapp.routing.handlers.impl;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.yarek.fasttestapp.model.database.dao.QuizDAO;
import org.yarek.fasttestapp.model.database.dao.UserDAO;
import org.yarek.fasttestapp.model.database.entities.QuizResultData;
import org.yarek.fasttestapp.model.entities.User;
import org.yarek.fasttestapp.model.entities.quiz.Quiz;
import org.yarek.fasttestapp.model.entities.quiz.QuizBlank;
import org.yarek.fasttestapp.routing.handlers.HttpHandlerBase;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
            String[] times = presentDateTimes(quizResultData.getStartTime(), quizResultData.getFinishTime());
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

    String[] presentDateTimes(LocalDateTime startTime, LocalDateTime finishTime) {
        if (finishTime == null) {
            return new String[] { presentTime(startTime) + " " + presentDate(startTime), NOT_FINISHED_TEXT };
        } else {
            return formatDateTimes(startTime, finishTime);
        }
    }

    String[] formatDateTimes(@NotNull LocalDateTime startTime,@NotNull LocalDateTime finishTime) {
        String[] result = new String[2];
        String startTimeResult;
        String finishTimeResult;

        startTimeResult = presentTime(startTime);
        finishTimeResult = presentTime(finishTime);

        if (startTime.getDayOfMonth() != finishTime.getDayOfMonth()
                || startTime.getMonth() != finishTime.getMonth()
                || startTime.getYear() != finishTime.getYear()) {
            startTimeResult = startTimeResult + " " +  presentDate(startTime);
            finishTimeResult = finishTimeResult + " " + presentDate(finishTime);
        }

        result[0] = startTimeResult;
        result[1] = finishTimeResult;
        return result;
    }

    String presentTime(LocalDateTime datetime) {
        return datetime.getHour() + ":" + datetime.getMinute() + ":" + formatSeconds(datetime.getSecond());
    }
    String presentDate(LocalDateTime datetime) {
        return  datetime.getDayOfMonth() + "." + datetime.getMonthValue() + "." + datetime.getYear();
    }
    String formatSeconds(int seconds) {
        if (seconds < 10) {
            return "0" + seconds;
        } else {
            return String.valueOf(seconds);
        }
    }
}
