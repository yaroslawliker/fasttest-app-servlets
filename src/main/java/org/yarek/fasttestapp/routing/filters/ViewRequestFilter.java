package org.yarek.fasttestapp.routing.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


/**
 * Filters requests for views.
 * Views requests are one, which URI ends up with .jsp, or .html.
 */
@WebFilter(
        urlPatterns = "/*",
        dispatcherTypes = DispatcherType.FORWARD,
        servletNames = "Controller"
)
public class ViewRequestFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException, ServletException {

        // Checking if jsp has already passed through this filter
        Boolean isChecked = (Boolean) req.getAttribute("isViewRequest");
        if (isChecked != null) {
            if (isChecked) {
                chain.doFilter(req, resp);
                return;
            }
        }

        // Getting the actual (perhaps forward) uri
        String uri = req.getPathInfo();

        if (uri.endsWith(".jsp")) {
            RequestDispatcher jspDispatcher = req.getServletContext().getNamedDispatcher("jsp");
            if (jspDispatcher == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            // Setting this attribute to avoid cycle checking by this filter
            req.setAttribute("isViewRequest", true);
            jspDispatcher.forward(req, resp);
        } else {
            chain.doFilter(req, resp);
        }
    }
}
