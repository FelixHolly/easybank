package at.holly.easybankbackend.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.util.Date;

/**
 * Custom Access Denied Handler
 * Handles authorization failures (403 Forbidden)
 */
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
    Date date = new Date();
    String errorMessage = (accessDeniedException != null && accessDeniedException.getMessage() != null) ? accessDeniedException.getMessage() : "Access denied";
    String path = request.getRequestURI();

    response.setHeader("easybank-error-reason", "Authorization denied");
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType("application/json;charset=utf-8");
    response.getWriter().write("""
        {
        "status":"%s",
        "path":"%s",
        "timestamp": "%s",
        "error": "%s"
        }
      """.formatted(HttpServletResponse.SC_FORBIDDEN, path, date, errorMessage)
    );
  }
}
