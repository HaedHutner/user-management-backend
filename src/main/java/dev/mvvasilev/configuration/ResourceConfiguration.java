package dev.mvvasilev.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@Configuration
@EnableResourceServer
public class ResourceConfiguration extends ResourceServerConfigurerAdapter {

    public static final String RESOURCE_ID = "user-manager-api";

    public static final String SECURE_GET_SCOPE = "#oauth2.hasScope('get')";

    public static final String SECURE_POST_SCOPE = "#oauth2.hasScope('post')";

    public static final String SECURE_PUT_SCOPE = "#oauth2.hasScope('put')";

    public static final String SECURE_DELETE_SCOPE = "#oauth2.hasScope('delete')";

    public static final String SECURE_ENDPOINT_PATTERN = "/api/**";

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId(RESOURCE_ID);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.requestMatchers()
                .antMatchers(SECURE_ENDPOINT_PATTERN).and().authorizeRequests()
                .antMatchers(HttpMethod.POST, SECURE_ENDPOINT_PATTERN).access(SECURE_POST_SCOPE)
                .antMatchers(HttpMethod.PUT, SECURE_ENDPOINT_PATTERN).access(SECURE_PUT_SCOPE)
                .antMatchers(HttpMethod.DELETE, SECURE_ENDPOINT_PATTERN).access(SECURE_DELETE_SCOPE)
                .anyRequest().access(SECURE_GET_SCOPE);
    }
}
