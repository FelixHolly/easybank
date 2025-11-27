package at.holly.easybankbackend.config;

import at.holly.easybankbackend.exception.CustomBasicAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Profile("prod")
@Configuration
public class ProdSecurityConfig {

  @Bean
  public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeyCloakRoleConverter());

    http
      .csrf(AbstractHttpConfigurer::disable)
      .redirectToHttps(withDefaults()) //only https
      .authorizeHttpRequests(
        (requests) -> requests
          .requestMatchers("/api/v1/myAccount", "/api/v1/myLoans", "/api/v1/myCards", "/api/v1/myBalance", "/api/v1/user").authenticated()
          .requestMatchers("/api/v1/register", "/api/v1/contact", "/api/v1/notices", "/error").permitAll()
          .requestMatchers("/actuator/health/**", "/actuator/info").permitAll()
      )
      .exceptionHandling(
        (exceptions) -> exceptions
          .authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint())
      )
      .oauth2ResourceServer(rsc -> rsc.jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter)));

    return http.build();
  }

}
