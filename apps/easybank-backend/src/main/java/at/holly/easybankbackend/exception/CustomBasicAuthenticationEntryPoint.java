package at.holly.easybankbackend.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.util.Date;

/**
 * Custom Authentication Entry Point
 * Handles authentication failures (401 Unauthorized)
 */
public class CustomBasicAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

    Date date = new Date();
    String errorMessage = (authException != null && authException.getMessage() != null) ? authException.getMessage() : "Authentication failed";
    String path = request.getRequestURI();

    response.setHeader("easybank-error-reason", "Authentication failed");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json;charset=utf-8");
    response.getWriter().write("""
        {
        "status":"%s",
        "path":"%s",
        "timestamp": "%s",
        "error": "%s"
        }
      """.formatted(HttpServletResponse.SC_UNAUTHORIZED, path, date, errorMessage)
    );
  }
}
