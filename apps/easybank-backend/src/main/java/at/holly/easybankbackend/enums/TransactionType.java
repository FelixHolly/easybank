package at.holly.easybankbackend.enums;

/**
 * Transaction types for account transactions
 */
public enum TransactionType {
    DEBIT("Debit Transaction"),
    CREDIT("Credit Transaction");

    private final String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
