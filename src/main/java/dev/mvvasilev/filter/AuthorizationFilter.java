package dev.mvvasilev.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import dev.mvvasilev.configuration.SecurityProperties;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class AuthorizationFilter extends BasicAuthenticationFilter {

    SecurityProperties securityProperties;

    public AuthorizationFilter(AuthenticationManager authenticationManager, SecurityProperties securityProperties) {
        super(authenticationManager);
        this.securityProperties = securityProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String tokenHeader = request.getHeader(SecurityProperties.TOKEN_HEADER_KEY);

        if (ObjectUtils.isEmpty(tokenHeader) || !tokenHeader.startsWith(securityProperties.getTokenPrefix())) {
            chain.doFilter(request, response);
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(getAuthenticationToken(tokenHeader));

        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthenticationToken(String tokenHeader) {

        if (ObjectUtils.isEmpty(tokenHeader)) {
            return null;
        }

        String user = JWT.require(Algorithm.HMAC512(securityProperties.getTokenSecret().getBytes()))
                .build()
                .verify(tokenHeader.replace(securityProperties.getTokenPrefix(), ""))
                .getSubject();

        if (ObjectUtils.isEmpty(user)) {
            return null;
        }

        return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
    }


}
