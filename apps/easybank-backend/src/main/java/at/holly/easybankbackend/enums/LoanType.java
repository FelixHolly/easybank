package at.holly.easybankbackend.enums;

/**
 * Loan types offered by the banking system
 */
public enum LoanType {
    HOME("Home Loan"),
    AUTO("Auto Loan"),
    EDUCATION("Education Loan"),
    PERSONAL("Personal Loan");

    private final String displayName;

    LoanType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
