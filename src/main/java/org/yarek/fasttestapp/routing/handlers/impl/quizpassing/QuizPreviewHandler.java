package org.yarek.fasttestapp.routing.handlers.impl.quizpassing;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yarek.fasttestapp.model.database.dao.QuizDAO;
import org.yarek.fasttestapp.model.database.dao.UserDAO;
import org.yarek.fasttestapp.model.entities.User;
import org.yarek.fasttestapp.model.entities.quiz.Quiz;
import org.yarek.fasttestapp.routing.handlers.HttpHandlerBase;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

public class QuizPreviewHandler extends HttpHandlerBase {
    private final Logger logger = LoggerFactory.getLogger(QuizPreviewHandler.class);

    private UserDAO userDAO;
    private QuizDAO quizDAO;

    public QuizPreviewHandler(UserDAO userDAO, QuizDAO quizDAO) {
        this.userDAO = userDAO;
        this.quizDAO = quizDAO;
        addPath("^/tests/[^/]+/preview$");
    }

    @Override
    protected String doGet(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> model) throws ServletException, IOException {

        // Ensured by QuizIdFilter
        Quiz quiz = (Quiz) req.getAttribute("quiz");
        String quizId = quiz.getId();

        String username = userDAO.getUsernameByID(quiz.getOwnerID());

        model.put("quiz", quiz);
        model.put("username", username);
        model.put("questionAmount", quiz.getQuestions().size());

        return "quiz_preview";
    }

    @Override
    protected String doPost(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> model) throws ServletException, IOException {
        String quizId = req.getParameter("quizId");

        User user = (User) req.getSession().getAttribute("user");
        if (user.getRole() != User.Role.USER) {
            logger.warn("Wrong request: user '{}' is not a USER role", user.getUsername());
            return "wrong_req";
        }

        LocalDateTime now = LocalDateTime.now();
        quizDAO.startQuizPassing(user.getId(), quizId, now);
        logger.info("Quiz started: user={}, quizId={}", user.getUsername(), quizId);
        return "tests/" + quizId + "@redirect";
    }
}
