package sm.order_project.config;

import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestRedisConfig {
    // Redis configuration is now handled by application-test.yml
    // No additional beans needed - Spring Boot auto-configuration will use
    // spring.data.redis.host and spring.data.redis.port from properties
}
