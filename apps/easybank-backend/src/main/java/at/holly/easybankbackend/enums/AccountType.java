package at.holly.easybankbackend.enums;

/**
 * Account types available in the banking system
 */
public enum AccountType {
    SAVINGS("Savings Account"),
    CHECKING("Checking Account"),
    CREDIT("Credit Account");

    private final String displayName;

    AccountType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
