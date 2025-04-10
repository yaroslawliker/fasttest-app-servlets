package org.yarek.fasttestapp.routing;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.yarek.fasttestapp.routing.handlers.HttpHandler;
import org.yarek.fasttestapp.routing.handlers.impl.HomeHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/*")
public class Controller extends HttpServlet {

    List<HttpHandler> handlers;

    @Override
    public void init() throws ServletException {
        registerHandlers();
    }

    protected void registerHandlers() {
        handlers = new ArrayList<>();
        HttpHandler httpHandler;

        // Home handler
        httpHandler = new HomeHandler();
        handlers.add(httpHandler);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String path = req.getPathInfo();
        for (HttpHandler handler : handlers) {
            if (handler.isProcessingPath(path)) {
                handler.handle(req, resp);
                break;
            }
        }
    }

}
