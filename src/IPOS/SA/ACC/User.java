package IPOS.SA.ACC;

public class User {
    private String username;
    private String fullName;
    private String role;

    public User(String username, String role, String fullName) {
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getRole()     { return role; }
    public String getFullName() { return fullName; }
}
