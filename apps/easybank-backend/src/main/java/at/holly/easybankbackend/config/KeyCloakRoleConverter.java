package at.holly.easybankbackend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class KeyCloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

  private static final String REALM_ACCESS_CLAIM = "realm_access";
  private static final String ROLES_CLAIM = "roles";
  private static final String ROLE_PREFIX = "ROLE_";

  /**
   * Converts Keycloak JWT realm roles to Spring Security GrantedAuthority objects.
   * Extracts roles from the 'realm_access.roles' claim and prefixes them with 'ROLE_'.
   *
   * @param jwt the JWT token containing Keycloak claims (never {@code null})
   * @return a collection of GrantedAuthority objects, or empty collection if no roles found
   */
  @Override
  public Collection<GrantedAuthority> convert(@NonNull Jwt jwt) {
    log.info("Converting Keycloak JWT roles to Spring Security authorities");
    log.debug("JWT Subject: {}, Issuer: {}", jwt.getSubject(), jwt.getIssuer());

    Map<String, Object> realmAccess = extractRealmAccess(jwt);

    if (realmAccess == null || realmAccess.isEmpty()) {
      log.warn("No realm_access claim found in JWT for subject: {}", jwt.getSubject());
      return Collections.emptySet();
    }

    Collection<String> roles = extractRoles(realmAccess);

    if (roles == null || roles.isEmpty()) {
      log.warn("No roles found in realm_access claim for subject: {}", jwt.getSubject());
      return Collections.emptySet();
    }

    Set<GrantedAuthority> authorities = roles.stream()
      .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role))
      .collect(Collectors.toSet());

    log.info("Converted {} Keycloak roles to authorities: {}", authorities.size(),
      authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
    return authorities;
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> extractRealmAccess(Jwt jwt) {
    try {
      Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get(REALM_ACCESS_CLAIM);
      log.debug("Extracted realm_access: {}", realmAccess);
      return realmAccess;
    } catch (ClassCastException e) {
      log.error("realm_access claim has unexpected type. Available claims: {}", jwt.getClaims().keySet(), e);
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  private Collection<String> extractRoles(Map<String, Object> realmAccess) {
    try {
      Collection<String> roles = (Collection<String>) realmAccess.get(ROLES_CLAIM);
      log.debug("Extracted roles from realm_access: {}", roles);
      return roles;
    } catch (ClassCastException e) {
      log.error("roles claim has unexpected type in realm_access", e);
      return null;
    }
  }
}
