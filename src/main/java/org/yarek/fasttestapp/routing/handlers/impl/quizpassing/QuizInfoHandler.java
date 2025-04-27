package org.yarek.fasttestapp.routing.handlers.impl.quizpassing;

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
import java.util.Objects;

public class QuizInfoHandler extends HttpHandlerBase {

    UserDAO userDAO;
    QuizDAO quizDAO;

    private final String PASSING_TEXT = "still passing";

    public QuizInfoHandler(UserDAO userDAO, QuizDAO quizDAO) {
        this.userDAO = userDAO;
        this.quizDAO = quizDAO;

        addPath("^/tests/[^/]+/info$");
    }

    @Override
    protected String doGet(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> model) throws ServletException, IOException {

        Quiz quiz = (Quiz) req.getAttribute("quiz");
        User user = (User) req.getSession().getAttribute("user");
        String authorId = quiz.getOwnerID();

        // Verifying only author can see quiz info
        if (!Objects.equals(authorId, user.getId())) {
            resp.getWriter().println("<script>alert('You are not the owner of the quiz')</script>");
            return "wrong_req";
        }

        // Preparing quiz results
        List<QuizResultData> quizResults = quizDAO.getQuizResultsByQuizId(quiz.getId());


        float maxScore = (new QuizBlank(quiz)).getMaxScore();
        List<String> students = new ArrayList<>();
        List<Float> scores = new ArrayList<>();
        List<String> startTimes = new ArrayList<>();
        List<String> finishTimes = new ArrayList<>();

        for (QuizResultData quizResultData : quizResults) {
            // Getting students
            String studentUsername = userDAO.getUsernameByID(quizResultData.getUserId());
            students.add(studentUsername);

            // Getting scores
            scores.add(quizResultData.getScore());

            // Format times
            String[] times = DataTimePresenter.presentDateTimes(
                    quizResultData.getStartTime(),
                    quizResultData.getFinishTime(),
                    PASSING_TEXT);
            startTimes.add(times[0]);
            finishTimes.add(times[1]);
        }

        model.put("quiz", quiz);
        model.put("resultAmount", quizResults.size());
        model.put("students", students);
        model.put("maxScore", maxScore);
        model.put("scores", scores);

        model.put("startTimes", startTimes);
        model.put("finishTimes", finishTimes);

        return "quiz_info";
    }
}
