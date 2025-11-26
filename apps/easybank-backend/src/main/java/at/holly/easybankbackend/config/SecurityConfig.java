package at.holly.easybankbackend.config;

import at.holly.easybankbackend.exception.CustomAccessDeniedHandler;
import at.holly.easybankbackend.exception.CustomBasicAuthenticationEntryPoint;
import at.holly.easybankbackend.filter.CsrfCookieFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Profile("!prod")
@Configuration
@EnableMethodSecurity  // Enables @PreAuthorize, @PostAuthorize, @Secured annotations
@EnableWebSecurity(debug = true)
public class SecurityConfig {

  @Bean
  public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeyCloakRoleConverter());

    http
      .csrf(csrf -> csrf
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
        .ignoringRequestMatchers("/contact", "/register")
      )
      .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
      .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(
        (requests) -> requests
          .requestMatchers("/myAccount", "/myLoans", "/myCards", "/myBalance", "/user", "/logout").hasRole("USER")
          .requestMatchers("/register", "/contact", "/notices", "/error").permitAll()
      )
      //global error config
      .exceptionHandling(
        (exceptions) -> exceptions
          .authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint())
          .accessDeniedHandler(new CustomAccessDeniedHandler())
      )
      .oauth2ResourceServer(rsc -> rsc.jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter)));

    return http.build();
  }

}
