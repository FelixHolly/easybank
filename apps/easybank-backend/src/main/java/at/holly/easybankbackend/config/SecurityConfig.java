package at.holly.easybankbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

  @Bean
  SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
      (requests) -> requests
        .requestMatchers("/myAccount", "/myLoans", "/myCards", "/myBalance").authenticated()
        .requestMatchers("/contact", "/notices", "/error").permitAll()
    );
    http.formLogin(withDefaults());
    http.httpBasic(withDefaults());
    return http.build();
  }

}
