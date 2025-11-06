package at.holly.easybankbackend.service;

import at.holly.easybankbackend.model.Authority;
import at.holly.easybankbackend.model.Role;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Service to manage role-based default authorities.
 * This implements the principle that roles imply certain authorities.
 *
 * Best Practice Pattern:
 * - Users are assigned ROLES (high-level access)
 * - Each ROLE implies a set of AUTHORITIES (specific permissions)
 * - Additional authorities can be granted beyond role defaults
 */
@Service
public class AuthorityService {

  /**
   * Returns the default authorities for a given role.
   * These are the minimum permissions a role should have.
   */
  public Set<Authority> getDefaultAuthoritiesForRole(Role role) {
    return switch (role) {
      case USER -> getUserAuthorities();
      case MANAGER -> getManagerAuthorities();
      case SUPPORT -> getSupportAuthorities();
      case ADMIN -> getAdminAuthorities();
    };
  }

  /**
   * Returns all default authorities for a set of roles.
   */
  public Set<Authority> getDefaultAuthoritiesForRoles(Set<Role> roles) {
    Set<Authority> authorities = new HashSet<>();
    for (Role role : roles) {
      authorities.addAll(getDefaultAuthoritiesForRole(role));
    }
    return authorities;
  }

  /**
   * USER role authorities - Basic read access to own data
   */
  private Set<Authority> getUserAuthorities() {
    return Set.of(
        Authority.ACCOUNT_READ,
        Authority.TRANSACTION_READ,
        Authority.CARD_READ,
        Authority.LOAN_READ,
        Authority.CONTACT_WRITE,
        Authority.NOTICE_READ
    );
  }

  /**
   * MANAGER role authorities - Read/Write access to most resources
   */
  private Set<Authority> getManagerAuthorities() {
    Set<Authority> authorities = new HashSet<>(getUserAuthorities());
    authorities.addAll(Set.of(
        Authority.ACCOUNT_WRITE,
        Authority.TRANSACTION_WRITE,
        Authority.TRANSACTION_APPROVE,
        Authority.CARD_WRITE,
        Authority.CARD_ACTIVATE,
        Authority.CARD_BLOCK,
        Authority.LOAN_WRITE,
        Authority.LOAN_APPROVE,
        Authority.USER_READ,
        Authority.CONTACT_READ,
        Authority.NOTICE_WRITE,
        Authority.REPORT_GENERATE
    ));
    return authorities;
  }

  /**
   * SUPPORT role authorities - Customer service operations
   */
  private Set<Authority> getSupportAuthorities() {
    Set<Authority> authorities = new HashSet<>(getUserAuthorities());
    authorities.addAll(Set.of(
        Authority.CARD_ACTIVATE,
        Authority.CARD_BLOCK,
        Authority.CONTACT_READ,
        Authority.CONTACT_WRITE,
        Authority.USER_READ
    ));
    return authorities;
  }

  /**
   * ADMIN role authorities - Full access
   */
  private Set<Authority> getAdminAuthorities() {
    // Admin gets all authorities
    return Set.of(Authority.values());
  }
}
