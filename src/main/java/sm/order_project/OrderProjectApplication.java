package sm.order_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableJpaAuditing
public class OrderProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderProjectApplication.class, args);
    }

}
