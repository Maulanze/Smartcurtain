package allers;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;


@Configuration
@SpringBootApplication
public class Starter {

    private String version = "1.0.0";

    @Service
    class ApplicationStartup implements InitializingBean {
        @Override
        public void afterPropertiesSet() {
            System.out.println("Version: " + version);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(Starter.class, args);
    }

}
