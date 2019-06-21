package dev.mvvasilev.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mvvasilev.dto.ErrorDTO;
import dev.mvvasilev.exception.ValidationException;
import dev.mvvasilev.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private AuthenticationService tokenProvider;

    private ObjectMapper objectMapper;

    public JwtTokenFilter(AuthenticationService tokenProvider, ObjectMapper objectMapper) {
        this.tokenProvider = tokenProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            Authentication authentication = tokenProvider.authenticate(request);

            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ValidationException | AuthenticationException ex) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(objectMapper.writeValueAsString(ErrorDTO.of(HttpStatus.UNAUTHORIZED, ex.getMessage())));
            return;
        }

        filterChain.doFilter(request, response);
    }
}
