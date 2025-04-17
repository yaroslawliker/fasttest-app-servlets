package org.yarek.fasttestapp.routing;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.yarek.fasttestapp.routing.handlers.HttpHandler;
import org.yarek.fasttestapp.routing.handlers.impl.HomeHandler;
import org.yarek.fasttestapp.routing.handlers.impl.LoginHandler;
import org.yarek.fasttestapp.routing.handlers.impl.SignupHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/*")
public class Controller extends HttpServlet {

    List<HttpHandler> handlers;

    @Override
    public void init() throws ServletException {
        registerHandlers();
    }

    protected void registerHandlers() {
        handlers = new ArrayList<>();

        // Home handler
        handlers.add(new HomeHandler());

        // Login handler
        handlers.add(new LoginHandler());

        // Sighup handler
        handlers.add(new SignupHandler());
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String path = req.getPathInfo();

        Map<String, Object> model = new HashMap<>();

        for (HttpHandler handler : handlers) {
            if (handler.isProcessingPath(path)) {
                String view = handler.handle(req, resp, model);
                mapModelToRequest(model, req);
                req.getRequestDispatcher(String.format("WEB-INF/views/%s.jsp", view)).forward(req, resp);
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
