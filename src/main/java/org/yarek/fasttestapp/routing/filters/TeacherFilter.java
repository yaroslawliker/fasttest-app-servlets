package org.yarek.fasttestapp.routing.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.yarek.fasttestapp.model.entities.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebFilter("/*")
public class TeacherFilter extends HttpFilter {

    private List<String> teacherOnlyPathsRegEx = new ArrayList<>();

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        String uri = req.getRequestURI();
        User user = (User) req.getSession().getAttribute("user");

        for (String path : teacherOnlyPathsRegEx) {
            Pattern pattern = Pattern.compile(path);
            Matcher matcher = pattern.matcher(uri);
            if (matcher.find()) {
                if (user.getRole() != User.Role.TEACHER) {
                    res.getWriter().println("<script>alert('You are not teacher')</script>");
                } else {
                    break;
                }
            }
        }

        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        teacherOnlyPathsRegEx.add("/create-test");
        teacherOnlyPathsRegEx.add("/tests/\\d+/info");
    }
}
