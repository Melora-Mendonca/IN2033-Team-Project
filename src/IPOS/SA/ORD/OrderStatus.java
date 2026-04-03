package IPOS.SA.ORD;

public enum OrderStatus {
    ACCEPTED("Accepted"),
    PROCESSING("Processing"),
    DISPATCHED("Dispatched"),
    DELIVERED("Delivered");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
