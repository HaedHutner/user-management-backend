package dev.mvvasilev.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mvvasilev.configuration.SecurityProperties;
import dev.mvvasilev.dto.AuthenticateUserDTO;
import dev.mvvasilev.entity.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;

@Component
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    private ObjectMapper objectMapper;

    private SecurityProperties securityProperties;

    public AuthenticationFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper, SecurityProperties securityProperties) {
        this.authenticationManager = authenticationManager;
        this.objectMapper = objectMapper;
        this.securityProperties = securityProperties;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            AuthenticateUserDTO credentials = objectMapper.readValue(request.getInputStream(), AuthenticateUserDTO.class);

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getRawPassword(), new ArrayList<>());

            return authenticationManager.authenticate(token);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String token = JWT.create()
                .withSubject(((User) authResult.getPrincipal()).getEmail())
                .withExpiresAt(new Date(LocalDateTime.now().plusHours(securityProperties.getTokenExpirationHours()).toEpochSecond(ZoneOffset.UTC)))
                .sign(Algorithm.HMAC512(securityProperties.getTokenSecret()));

        response.addHeader(SecurityProperties.TOKEN_HEADER_KEY, securityProperties.getTokenPrefix() + token);
    }
}
