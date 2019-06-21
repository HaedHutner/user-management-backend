package dev.mvvasilev.security;

import dev.mvvasilev.exception.TokenAuthenticationException;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class JwtTokenProvider {

    /**
     * THIS IS NOT A SECURE PRACTICE! For simplicity, we are storing a static key here. Ideally, in a
     * microservices environment, this key would be kept on a config-server.
     */
    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.expire-length}")
    private long validityInSeconds = 3600;

    private UserDetailsService userDetailsService;

    @Autowired
    public JwtTokenProvider(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String username, Set<Permission> permissions) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("perms", permissions);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime validUntil = now.plusSeconds(validityInSeconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant(ZoneOffset.UTC)))
                .setExpiration(Date.from(validUntil.toInstant(ZoneOffset.UTC)))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Authentication authenticate(HttpServletRequest request) {
        String token;
        Jws<Claims> parsedToken;

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || authorizationHeader.isEmpty() || !authorizationHeader.startsWith("Bearer")) {
            return null;
        }

        token = authorizationHeader.substring(7);

        try {
            parsedToken = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        } catch (JwtException | IllegalArgumentException e) {
            throw new TokenAuthenticationException("Invalid token");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(parsedToken.getBody().getSubject());

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}
