package org.yarek.fasttestapp.routing.filters;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.yarek.fasttestapp.model.database.dao.QuizDAO;
import org.yarek.fasttestapp.model.entities.quiz.Quiz;

import java.io.IOException;

/**
 * Filter checks all requests within path pattern /tests/<quizId>...
 * It looks for given id, checks if it's correct and parses it, gets from DAO and sets "quiz" request parameter.
 */
@WebFilter(urlPatterns = "/tests/*")
public class QuizIdFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        QuizDAO quizDAO = (QuizDAO) getServletContext().getAttribute("quizDAO");

        String path = req.getRequestURI();
        if (!path.matches("/tests/.+")) {
            chain.doFilter(req, res);
            return;
        }

        String uri = req.getRequestURI();
        String quizId = extractQuizId(uri);

        Quiz quiz = quizDAO.getQuizById(quizId);
        if (quiz == null) {
            res.getWriter().write("<script>alert('No such quiz with id" + quizId + "')</script>");
            req.getRequestDispatcher("wrong_req").forward(req, res);
            return;
        }

        req.setAttribute("quiz", quiz);
        chain.doFilter(req, res);
    }

    /**
     * Extracts id from uri of a pattern /tests/<id>/...
     */
    public static String extractQuizId(String uri) {
        int idStart = uri.indexOf("tests/") + 6;
        int idEnd = uri.indexOf("/", idStart+1);
        // Case path is just /tests/id
        if (idEnd == -1) {
            idEnd = uri.length();
        }
        return uri.substring(idStart, idEnd);
    }
}
