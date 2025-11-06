package at.holly.easybankbackend.model;

/**
 * Enum representing fine-grained authorities (permissions) in the system.
 * Authorities use the pattern RESOURCE:ACTION for clarity.
 *
 * Spring Security Best Practices:
 * - Roles are prefixed with "ROLE_" (e.g., ROLE_ADMIN)
 * - Authorities have no prefix (e.g., ACCOUNT:READ)
 * - Use @PreAuthorize("hasAuthority('ACCOUNT:READ')") in controllers
 * - Combine multiple: @PreAuthorize("hasAnyAuthority('ACCOUNT:READ', 'ACCOUNT:WRITE')")
 */
public enum Authority {
  // Account authorities
  ACCOUNT_READ("ACCOUNT:READ"),
  ACCOUNT_WRITE("ACCOUNT:WRITE"),
  ACCOUNT_DELETE("ACCOUNT:DELETE"),

  // Transaction authorities
  TRANSACTION_READ("TRANSACTION:READ"),
  TRANSACTION_WRITE("TRANSACTION:WRITE"),
  TRANSACTION_APPROVE("TRANSACTION:APPROVE"),

  // Card authorities
  CARD_READ("CARD:READ"),
  CARD_WRITE("CARD:WRITE"),
  CARD_ACTIVATE("CARD:ACTIVATE"),
  CARD_BLOCK("CARD:BLOCK"),

  // Loan authorities
  LOAN_READ("LOAN:READ"),
  LOAN_WRITE("LOAN:WRITE"),
  LOAN_APPROVE("LOAN:APPROVE"),

  // User management authorities
  USER_READ("USER:READ"),
  USER_WRITE("USER:WRITE"),
  USER_DELETE("USER:DELETE"),

  // Contact/Support authorities
  CONTACT_READ("CONTACT:READ"),
  CONTACT_WRITE("CONTACT:WRITE"),

  // Notice authorities
  NOTICE_READ("NOTICE:READ"),
  NOTICE_WRITE("NOTICE:WRITE"),

  // Report authorities
  REPORT_GENERATE("REPORT:GENERATE"),
  REPORT_EXPORT("REPORT:EXPORT");

  private final String permission;

  Authority(String permission) {
    this.permission = permission;
  }

  /**
   * Returns the authority name (without ROLE_ prefix)
   */
  public String getAuthority() {
    return this.permission;
  }

  @Override
  public String toString() {
    return this.permission;
  }
}
