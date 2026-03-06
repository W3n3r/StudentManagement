package com.prj301.filter;

import com.prj301.entity.UserAccount;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {"/departments"})
public class AuthorizationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            UserAccount user = (UserAccount) session.getAttribute("user");
            if (user != null && user.getRole() == 1) {
                chain.doFilter(request, response);
                return;
            }
        }
        httpResponse.sendRedirect(httpRequest.getContextPath() + "/students");
    }
}
