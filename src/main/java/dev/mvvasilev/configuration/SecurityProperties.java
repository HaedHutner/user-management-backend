package dev.mvvasilev.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:security.properties")
public class SecurityProperties {

    public static final String TOKEN_HEADER_KEY = "auth-token";

    @Value("{token.expiration}")
    private long tokenExpirationHours;

    @Value("{token.secret}")
    private String tokenSecret;

    @Value("{token.prefix}")
    private String tokenPrefix;

    public long getTokenExpirationHours() {
        return tokenExpirationHours;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public String getTokenPrefix() {
        return tokenPrefix;
    }
}
