package IPOS.SA.ORD;

public enum OrderStatus {
    ACCEPTED("accepted"),    // order received and confirmed
    PENDING("pending"),      // awaiting processing
    PROCESSING("processing"),// being packed/prepared
    DISPATCHED("dispatched"),// handed to courier
    DELIVERED("delivered");  // confirmed arrival

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
