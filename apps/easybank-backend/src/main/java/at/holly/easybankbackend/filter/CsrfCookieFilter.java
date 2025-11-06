package at.holly.easybankbackend.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class CsrfCookieFilter extends OncePerRequestFilter {

    /**
     * This filter ensures that a CSRF token is generated for each request
     * and (optionally) sent to the client as a cookie.
     * It retrieves the CsrfToken from the request attributes, triggering
     * token creation if it does not yet exist. In a complete implementation,
     * the token can also be added to the response as a cookie (e.g., XSRF-TOKEN)
     * so that frontend applications like Angular can read and include it
     * in subsequent requests for CSRF protection.
     */

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        csrfToken.getToken();
        filterChain.doFilter(request, response);
    }
}
