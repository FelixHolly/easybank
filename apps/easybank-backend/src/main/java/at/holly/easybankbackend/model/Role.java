package at.holly.easybankbackend.model;

/**
 * Enum representing user roles in the system.
 * Using enum provides type safety and prevents invalid role values.
 */
public enum Role {
  /**
   * Standard user role with basic access
   */
  USER,

  /**
   * Administrator role with full system access
   */
  ADMIN,

  /**
   * Manager role with elevated privileges
   */
  MANAGER,

  /**
   * Support role for customer service operations
   */
  SUPPORT;

  /**
   * Returns the role name with ROLE_ prefix as required by Spring Security
   */
  public String getAuthority() {
    return "ROLE_" + this.name();
  }
}
