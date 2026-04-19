package IPOS.SA.ACC;

public enum AccountStatus {
    NORMAL,      // account in good standing
    SUSPENDED,   // temporarily blocked from placing orders
    IN_DEFAULT   // overdue beyond 30 days
}
