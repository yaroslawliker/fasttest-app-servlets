package org.yarek.fasttestapp.routing.handlers.impl;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.yarek.fasttestapp.model.database.dao.QuizDAO;
import org.yarek.fasttestapp.model.entities.User;
import org.yarek.fasttestapp.model.entities.quiz.Quiz;
import org.yarek.fasttestapp.routing.handlers.HttpHandlerBase;

import java.io.IOException;
import java.util.Map;

public class QuizRedirectionHandler extends HttpHandlerBase {

    QuizDAO quizDAO;


    public QuizRedirectionHandler(QuizDAO quizDAO) {
        this.quizDAO = quizDAO;
        addPath("^/tests/[^/]+$");
    }

    @Override
    protected String doGet(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> model) throws ServletException, IOException {

        String uri = req.getRequestURI();
        String quizId = uri.substring(uri.lastIndexOf("/") + 1);

        Quiz quiz = quizDAO.getQuizById(quizId);
        if (quiz == null) {
            resp.getWriter().write("<script>alert('No such quiz with id" + quizId + "')</script>");
            return "wrong_req";
        }

        User user = (User) model.get("user");

        if (user.getRole() == User.Role.TEACHER) {
            return uri + "/info@redirect";

        } else if (user.getRole() == User.Role.USER) {

            if (quizDAO.isUserPassingQuiz(user.getId(), quizId)) {
                return uri + "/started@redirect";
            } else {
                return uri + "/preview@redirect";
            }

        } else {
            return uri + "/preview@redirect";
        }
    }
}
