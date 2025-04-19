package org.yarek.fasttestapp.routing.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebFilter(
        urlPatterns = "/*"
)
public class AuthentificationFilter extends HttpFilter {

    List<String> allowedPaths = new ArrayList<>();

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException, ServletException {
        String uri = req.getPathInfo();
        if (allowedPaths.contains(uri)) {
            chain.doFilter(req, resp);
        } else {
            HttpSession session = req.getSession();
            if (session.getAttribute("user") != null) {
                chain.doFilter(req, resp);
            } else {
                resp.sendRedirect(req.getContextPath() + "/login");
            }
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        allowedPaths.add("/");
        allowedPaths.add("/home");
        allowedPaths.add("/login");
        allowedPaths.add("/signup");
        allowedPaths.add("/register");
    }

}
