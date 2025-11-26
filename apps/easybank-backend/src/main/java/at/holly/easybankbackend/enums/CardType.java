package at.holly.easybankbackend.enums;

/**
 * Credit/Debit card types supported by the banking system
 */
public enum CardType {
    VISA("Visa"),
    MASTERCARD("Mastercard"),
    DISCOVER("Discover"),
    JCB("JCB");

    private final String displayName;

    CardType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
