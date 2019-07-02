package dev.mvvasilev;

import dev.mvvasilev.configuration.RabbitMQProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author Miroslav Vasilev
 */
@SpringBootApplication
@EnableConfigurationProperties(RabbitMQProperties.class)
public class UserManager {

    public static void main(String[] args) {
        SpringApplication.run(UserManager.class, args);
    }

}
