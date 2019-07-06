package dev.mvvasilev;

import dev.mvvasilev.common.config.RabbitMQProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Miroslav Vasilev
 */
@SpringBootApplication
@ComponentScan(basePackages = "dev.mvvasilev")
@EnableConfigurationProperties(RabbitMQProperties.class)
public class UserManager {

    public static void main(String[] args) {
        SpringApplication.run(UserManager.class, args);
    }

}
