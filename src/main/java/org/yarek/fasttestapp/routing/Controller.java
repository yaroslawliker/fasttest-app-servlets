package org.yarek.fasttestapp.routing;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.yarek.fasttestapp.model.Constants;
import org.yarek.fasttestapp.model.database.dao.QuizDAO;
import org.yarek.fasttestapp.model.database.dao.QuizDAOPostgres;
import org.yarek.fasttestapp.model.database.dao.UserDAO;
import org.yarek.fasttestapp.model.database.dao.UserDAOPostgres;
import org.yarek.fasttestapp.routing.handlers.HttpHandler;
import org.yarek.fasttestapp.routing.handlers.impl.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(value = "/*", loadOnStartup = 1)
public class Controller extends HttpServlet {

    private HikariDataSource dataSource;
    private UserDAO userDAO;
    private QuizDAO quizDAO;

    List<HttpHandler> handlers;

    @Override
    public void init() throws ServletException {
        setUpHikariDataSource();
        initDAOs();
        registerHandlers();
    }

    private void setUpHikariDataSource() {
        dataSource = new HikariDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setJdbcUrl(Constants.DATABASE_URL);
        dataSource.setUsername(Constants.DATABASE_USER);
        dataSource.setPassword(Constants.DATABASE_PASSWORD);
        dataSource.setSchema("public");
        dataSource.setMaximumPoolSize(20);
    }

    private void initDAOs() {
        userDAO = new UserDAOPostgres(dataSource);
        quizDAO = new QuizDAOPostgres(dataSource);

        getServletContext().setAttribute("quizDAO", quizDAO); // For filters to use
    }

    protected void registerHandlers() {
        handlers = new ArrayList<>();

        // Home handler
        handlers.add(new HomeHandler());

        // Login and logout handlers
        handlers.add(new LoginHandler(userDAO));
        handlers.add(new LogoutHandler());

        // Sighup handler
        handlers.add(new SignupHandler(userDAO));

        // Create quiz handler
        handlers.add(new CreateQuizHandler(quizDAO));

        // QuizzesPreviewHandler
        handlers.add(new QuizzesPreviewHandler(userDAO, quizDAO));

        // QuizRedirector
        handlers.add(new QuizRedirectionHandler(quizDAO));
        handlers.add(new QuizPreviewHandler(userDAO, quizDAO));
        handlers.add((new QuizStartedHandler(userDAO)));
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String path = req.getPathInfo();

        Map<String, Object> model = new HashMap<>();

        for (HttpHandler handler : handlers) {
            if (handler.isProcessingPath(path)) {
                // Delegate work to the handler
                String view = handler.handle(req, resp, model);
                // Put model objects to as attributes
                mapModelToRequest(model, req);

                // Check if the view is as redirect
                if (view.endsWith("@redirect")) {
                    view = view.substring(0, view.lastIndexOf("@redirect"));
                    resp.sendRedirect("/" + view);
                } else {
                    req.getRequestDispatcher(String.format("/WEB-INF/views/%s.jsp", view)).forward(req, resp);
                }
                break;
            }
        }
    }

    private void mapModelToRequest(Map<String, Object> model, HttpServletRequest request) {
        for (Map.Entry<String, Object> entry : model.entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }
    }

}
