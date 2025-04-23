package org.yarek.fasttestapp.routing.handlers.impl.quizpassing;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.yarek.fasttestapp.model.database.dao.QuizDAO;
import org.yarek.fasttestapp.model.entities.User;
import org.yarek.fasttestapp.model.entities.quiz.Quiz;
import org.yarek.fasttestapp.routing.handlers.HttpHandlerBase;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class QuizRedirectionHandler extends HttpHandlerBase {

    QuizDAO quizDAO;


    public QuizRedirectionHandler(QuizDAO quizDAO) {
        this.quizDAO = quizDAO;
        addPath("^/tests/[^/]+$");
    }

    @Override
    protected String doGet(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> model) throws ServletException, IOException {

        // Ensured by QuizIdFilter
        Quiz quiz = (Quiz) req.getAttribute("quiz");

        // Ensured by AuthorizationFilter
        User user = (User) req.getSession().getAttribute("user");

        String uri = req.getRequestURI().substring(1);

        if (user.getRole() == User.Role.TEACHER) {
            if (Objects.equals(quiz.getOwnerID(), user.getId())) {
                return uri + "/info@redirect";
            } else {
                resp.getWriter().write("<script>alert('You are not a student or owner of the quiz " + quiz.getId() + "')</script>");
                return "wrong_req";
            }


        } else if (user.getRole() == User.Role.USER) {

            if (quizDAO.isUserPassingQuiz(user.getId(), quiz.getId())) {
                return uri + "/started@redirect";
            } else {
                return uri + "/preview@redirect";
            }

        } else {
            return uri + "/preview@redirect";
        }
    }
}
