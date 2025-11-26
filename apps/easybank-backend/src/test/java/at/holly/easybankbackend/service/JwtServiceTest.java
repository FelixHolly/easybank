package at.holly.easybankbackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for JwtService.
 * Tests JWT token claim extraction functionality.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JwtService Tests")
class JwtServiceTest {

    private JwtService jwtService;

    @Mock
    private Authentication authentication;

  private JwtAuthenticationToken jwtAuthenticationToken;

    private static final String TEST_EMAIL = "john.doe@example.com";
    private static final String TEST_USERNAME = "johndoe";
    private static final String TEST_SUBJECT = "123e4567-e89b-12d3-a456-426614174000";
    private static final String TEST_GIVEN_NAME = "John";
    private static final String TEST_FAMILY_NAME = "Doe";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        // Create JWT with test claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", TEST_EMAIL);
        claims.put("preferred_username", TEST_USERNAME);
        claims.put("given_name", TEST_GIVEN_NAME);
        claims.put("family_name", TEST_FAMILY_NAME);
        claims.put("sub", TEST_SUBJECT);

      Jwt jwt = new Jwt(
        "test-token",
        Instant.now(),
        Instant.now().plusSeconds(3600),
        Map.of("alg", "RS256"),
        claims
      );

        jwtAuthenticationToken = new JwtAuthenticationToken(jwt);
    }

    @Test
    @DisplayName("Should extract email from JWT")
    void shouldExtractEmail() {
        // When
        String email = jwtService.extractEmail(jwtAuthenticationToken);

        // Then
        assertThat(email).isEqualTo(TEST_EMAIL);
    }

    @Test
    @DisplayName("Should extract username from JWT")
    void shouldExtractUsername() {
        // When
        String username = jwtService.extractUsername(jwtAuthenticationToken);

        // Then
        assertThat(username).isEqualTo(TEST_USERNAME);
    }

    @Test
    @DisplayName("Should extract subject from JWT")
    void shouldExtractSubject() {
        // When
        String subject = jwtService.extractSubject(jwtAuthenticationToken);

        // Then
        assertThat(subject).isEqualTo(TEST_SUBJECT);
    }

    @Test
    @DisplayName("Should extract given name from JWT")
    void shouldExtractGivenName() {
        // When
        String givenName = jwtService.extractGivenName(jwtAuthenticationToken);

        // Then
        assertThat(givenName).isEqualTo(TEST_GIVEN_NAME);
    }

    @Test
    @DisplayName("Should extract family name from JWT")
    void shouldExtractFamilyName() {
        // When
        String familyName = jwtService.extractFamilyName(jwtAuthenticationToken);

        // Then
        assertThat(familyName).isEqualTo(TEST_FAMILY_NAME);
    }

    @Test
    @DisplayName("Should extract full name from JWT")
    void shouldExtractFullName() {
        // When
        String fullName = jwtService.extractFullName(jwtAuthenticationToken);

        // Then
        assertThat(fullName).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should extract full name with first name only")
    void shouldExtractFullNameWithFirstNameOnly() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("given_name", TEST_GIVEN_NAME);
        claims.put("email", TEST_EMAIL);

        Jwt jwtWithFirstNameOnly = new Jwt(
                "test-token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "RS256"),
                claims
        );

        JwtAuthenticationToken token = new JwtAuthenticationToken(jwtWithFirstNameOnly);

        // When
        String fullName = jwtService.extractFullName(token);

        // Then
        assertThat(fullName).isEqualTo(TEST_GIVEN_NAME);
    }

    @Test
    @DisplayName("Should return null when no name claims present")
    void shouldReturnNullWhenNoNames() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", TEST_EMAIL);

        Jwt jwtWithoutNames = new Jwt(
                "test-token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "RS256"),
                claims
        );

        JwtAuthenticationToken token = new JwtAuthenticationToken(jwtWithoutNames);

        // When
        String fullName = jwtService.extractFullName(token);

        // Then
        assertThat(fullName).isNull();
    }

    @Test
    @DisplayName("Should extract custom claim")
    void shouldExtractCustomClaim() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("custom_claim", "custom_value");

        Jwt jwtWithCustomClaim = new Jwt(
                "test-token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "RS256"),
                claims
        );

        JwtAuthenticationToken token = new JwtAuthenticationToken(jwtWithCustomClaim);

        // When
        String customClaim = jwtService.extractClaim(token, "custom_claim");

        // Then
        assertThat(customClaim).isEqualTo("custom_value");
    }

    @Test
    @DisplayName("Should return JWT object")
    void shouldReturnJwtObject() {
        // When
        Jwt result = jwtService.getJwt(jwtAuthenticationToken);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTokenValue()).isEqualTo("test-token");
        assertThat(result.getClaimAsString("email")).isEqualTo(TEST_EMAIL);
    }

    @Test
    @DisplayName("Should throw exception when authentication is not JwtAuthenticationToken")
    void shouldThrowExceptionWhenNotJwtAuthentication() {
        // When & Then
        assertThatThrownBy(() -> jwtService.extractEmail(authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid authentication type");
    }

    @Test
    @DisplayName("Should throw exception when email claim is missing")
    void shouldThrowExceptionWhenEmailClaimMissing() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", TEST_SUBJECT);

        Jwt jwtWithoutEmail = new Jwt(
                "test-token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "RS256"),
                claims
        );

        JwtAuthenticationToken token = new JwtAuthenticationToken(jwtWithoutEmail);

        // When & Then
        assertThatThrownBy(() -> jwtService.extractEmail(token))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email claim not found in JWT token");
    }

    @Test
    @DisplayName("Should return null when optional claim is missing")
    void shouldReturnNullWhenOptionalClaimMissing() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", TEST_EMAIL);

        Jwt jwtWithoutOptionalClaim = new Jwt(
                "test-token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "RS256"),
                claims
        );

        JwtAuthenticationToken token = new JwtAuthenticationToken(jwtWithoutOptionalClaim);

        // When
        String givenName = jwtService.extractGivenName(token);

        // Then
        assertThat(givenName).isNull();
    }
}
