package dev.mvvasilev.filter;

import dev.mvvasilev.exception.AuthenticationException;
import dev.mvvasilev.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class UserTokenFilter extends HttpFilter {

    public static final String TOKEN_HEADER = "auth-token";

    private UserService userService;

    @Autowired
    public UserTokenFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String authToken = request.getHeader(TOKEN_HEADER);

        if (authToken != null && userService.validateUserToken(authToken)) {
            super.doFilter(request, response, chain);
        } else {
            throw new AuthenticationException("Authentication token not provided or invalid");
        }
    }
}
