package org.yarek.fasttestapp.routing.handlers.impl;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.yarek.fasttestapp.model.database.dao.QuizDAO;
import org.yarek.fasttestapp.model.database.dao.UserDAO;
import org.yarek.fasttestapp.model.entities.User;
import org.yarek.fasttestapp.model.entities.quiz.Answer;
import org.yarek.fasttestapp.model.entities.quiz.Question;
import org.yarek.fasttestapp.model.entities.quiz.Quiz;
import org.yarek.fasttestapp.routing.handlers.HttpHandlerBase;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

public class CreateQuizHandler extends HttpHandlerBase {
    private final QuizDAO quizDAO;

    public CreateQuizHandler(QuizDAO quizDAO) {
        this.quizDAO = quizDAO;
        addPath("/create-test");
    }

    @Override
    protected String doGet(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> model) throws ServletException, IOException {
        return "create_quiz";
    }

    @Override
    protected String doPost(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> model) throws ServletException, IOException {
        String name = req.getParameter("name");
        String description = req.getParameter("description");
        int questions = Integer.parseInt(req.getParameter("number-of-questions"));

        User user = (User)req.getSession().getAttribute("user");

        if (name == null || description == null || user == null || questions <= 0) {
            return "wrong_req";
        }

        if (user.getRole() != User.Role.TEACHER) {
            return "wrong_req";
        }

        Date creationDate = new Date();
        creationDate.setTime(System.currentTimeMillis());

        Quiz quiz = new Quiz();
        quiz.setName(name);
        quiz.setDescription(description);
        quiz.setCreationDate(creationDate);
        quiz.setOwnerID(user.getId());

        // Getting answers
        for (int i = 1; i < questions+1; i++) {
            String questionText = req.getParameter("questions["+i+"].text");

            if (questionText != null) { // May be null if question was deleted, front-end issues :)

                // Getting score
                String scoreString = req.getParameter("questions["+i+"].score");
                float score = (scoreString != null && !scoreString.isEmpty()) ? Float.parseFloat(req.getParameter("questions[" + i + "].score")) : 1f;

                Question question = new Question(questionText, score);
                // TODO: score
                int j=0;
                String answerText = req.getParameter("questions["+i+"].answers["+1+"].text");
                String isCoorectText = req.getParameter("questions["+i+"].answers["+1+"].isCorrect");
                boolean isCorrect = req.getParameter("questions["+i+"].answers["+1+"].isCorrect") != null;
                // questions["+i+"].answers["+j+"].isCorrect
                if (answerText == null) {
                    return "wrong_req";
                }
                for (j = 2; answerText != null; j++) {

                    Answer answer = new Answer(answerText, isCorrect);
                    question.addAnswer(answer);
                    answerText = req.getParameter("questions["+i+"].answers["+j+"].text");
                    isCorrect = req.getParameter("questions["+i+"].answers["+j+"].isCorrect") != null;
                }
                quiz.addQuestion(question);
            }
        }

        String quizID = quizDAO.saveNewQuiz(quiz);

        return "tests/" + quizID + "/info@redirect";
    }
}
