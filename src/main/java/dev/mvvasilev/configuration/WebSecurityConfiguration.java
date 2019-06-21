package dev.mvvasilev.configuration;

import dev.mvvasilev.security.JwtTokenConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private JwtTokenConfigurer tokenConfigurer;

    @Autowired
    public WebSecurityConfiguration(JwtTokenConfigurer tokenConfigurer) {
        this.tokenConfigurer = tokenConfigurer;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Disable CSRF
        http.csrf().disable();

        // Stateless
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Configure endpoints
        http.authorizeRequests()
                .antMatchers("/api/users/create").permitAll()
                .antMatchers("/api/users/authenticate").permitAll()
                .anyRequest().authenticated().and().apply(tokenConfigurer);
    }
}
