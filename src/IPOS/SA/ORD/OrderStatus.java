package IPOS.SA.ORD;

public enum OrderStatus {
    ACCEPTED("accepted"),
    PENDING("pending"),
    PROCESSING("processing"),
    DISPATCHED("dispatched"),
    DELIVERED("delivered");

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
