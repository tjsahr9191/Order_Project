package sm.order_project.config;

import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration
//@EnableWebSecurity
public class TestSecurityConfig {

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf(AbstractHttpConfigurer::disable)
//            .sessionManagement(session -> session
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//            .authorizeHttpRequests(auth -> auth
//                    .requestMatchers(HttpMethod.GET, "/api/chapter2/boards/**").permitAll()
//                .requestMatchers("/api/chapter2/members/signup", "/api/chapter2/members/login").permitAll()
//                .anyRequest().authenticated()
//            );
//
//        return http.build();
//    }
} 