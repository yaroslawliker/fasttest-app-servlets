package org.yarek.fasttestapp.routing.handlers.impl.quizpassing;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.yarek.fasttestapp.model.database.dao.QuizDAO;
import org.yarek.fasttestapp.model.database.dao.UserDAO;
import org.yarek.fasttestapp.model.entities.User;
import org.yarek.fasttestapp.model.entities.quiz.Question;
import org.yarek.fasttestapp.model.entities.quiz.Quiz;
import org.yarek.fasttestapp.model.entities.quiz.QuizBlank;
import org.yarek.fasttestapp.routing.handlers.HttpHandlerBase;

import java.io.IOException;
import java.time.LocalDateTime;
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

    @Override
    protected String doPost(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> model) throws ServletException, IOException {
        Quiz quiz = (Quiz) req.getAttribute("quiz");
        QuizBlank blank = new QuizBlank(quiz);

        for (int i = 0; i < quiz.getQuestions().size(); i++) {
            Question question = quiz.getQuestions().get(i);
            for (int j = 0; j < question.getAnswers().size(); j++) {
                if (req.getParameter("questions["+i+"].answers["+j+"].isChecked") != null) {
                    // questions["+i+"].answers["+j+"].isChecked
                    blank.registerAnswer(i,j);
                }
            }
        }

        float score = blank.getScore();
        float maxScore = blank.getMaxScore();

        User user = (User) req.getSession().getAttribute("user");

        quizDAO.finishQuizPassing(user.getId(), quiz.getId(), LocalDateTime.now(), score);

        String ownerUsername = userDAO.getUsernameByID(quiz.getOwnerID());
        req.setAttribute("username", ownerUsername);
        req.setAttribute("score", score);
        req.setAttribute("maxScore", maxScore);
        return "quiz_result";
    }
}
