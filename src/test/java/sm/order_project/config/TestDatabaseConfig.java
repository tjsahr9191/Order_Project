package sm.order_project.config;

import jakarta.annotation.PreDestroy;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import javax.sql.DataSource;

@TestConfiguration
public class TestDatabaseConfig {
    
//    @Container
//    private static final MySQLContainer<?> mysqlContainer;
//
//    static {
//        mysqlContainer = new MySQLContainer<>("mysql:8.0.33")
//                .withDatabaseName("testdb")
//                .withUsername("test")
//                .withPassword("test");
//        mysqlContainer.start();
//
//        // A) 기본 설정 + rewriteBatchedStatements 옵션
////        String originalJdbcUrl = mysqlContainer.getJdbcUrl() + "?rewriteBatchedStatements=true";
////
////        // TODO: BulkInsert 모니터링 - 아래 주석을 해제해야함
//////        originalJdbcUrl = originalJdbcUrl + "&profileSQL=true&logger=Slf4JLogger&maxQuerySizeToLog=2147483647";
////
////        System.setProperty("spring.datasource.url", originalJdbcUrl);
////        System.setProperty("spring.datasource.username", mysqlContainer.getUsername());
////        System.setProperty("spring.datasource.password", mysqlContainer.getPassword());
//    }
//
//    @Bean
//    public DataSource dataSource() {
//        return DataSourceBuilder.create()
//                .url(mysqlContainer.getJdbcUrl()+ "?rewriteBatchedStatements=true")
//                .username(mysqlContainer.getUsername())
//                .password(mysqlContainer.getPassword())
//                .driverClassName(mysqlContainer.getDriverClassName())
//                .build();
//    }
//
////    @Bean
////    public MySQLContainer<?> mySQLContainer() {
////        return mysqlContainer;
////    }
//
//    @PreDestroy
//    public void stop() {
//        if (mysqlContainer != null && mysqlContainer.isRunning()) {
//            mysqlContainer.stop();
//        }
//    }
} 