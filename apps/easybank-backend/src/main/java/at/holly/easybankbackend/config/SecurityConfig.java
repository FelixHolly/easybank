package at.holly.easybankbackend.config;

import at.holly.easybankbackend.exceptionhandling.CustomAccessDeniedHandler;
import at.holly.easybankbackend.exceptionhandling.CustomBasicAuthenticationEntryPoint;
import at.holly.easybankbackend.filter.CsrfCookieFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import static org.springframework.security.config.Customizer.withDefaults;

@Profile("!prod")
@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

    http
      //.csrf(AbstractHttpConfigurer::disable)
      .csrf(csrf -> csrf
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
        .ignoringRequestMatchers("/contact", "/register")
      )
      .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
      .securityContext(securityContext -> securityContext.requireExplicitSave(false))
      .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
      .authorizeHttpRequests(
        (requests) -> requests
          .requestMatchers("/myAccount", "/myLoans", "/myCards", "/myBalance", "/user", "/logout").authenticated()
          .requestMatchers("/register", "/contact", "/notices", "/error").permitAll()
      )
      //global error config
      .exceptionHandling(
        (exceptions) -> exceptions
          .authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint())
          .accessDeniedHandler(new CustomAccessDeniedHandler())
      )
      .formLogin(withDefaults())
      .httpBasic(hbc -> hbc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint()));

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

}
