package IPOS.SA.ACC.Model;

public class Staff {
    private String staffId;
    private String username;
    private String firstName;
    private String surName;
    private String email;
    private String phone;
    private String address;
    private String role;
    private String password;
    private boolean isActive;

    public Staff() {
        this.isActive = true;
    }

    public Staff(String staffId, String username, String firstName, String surName,
                 String email, String phone, String address, String role) {
        this.staffId = staffId;
        this.username = username;
        this.firstName = firstName;
        this.surName = surName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.role = role;
        this.isActive = true;
    }

    // Getters
    public String getStaffId() { return staffId; }
    public String getUsername() { return username; }
    public String getFirstName() { return firstName; }
    public String getSurName() { return surName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getRole() { return role; }
    public String getPassword() { return password; }
    public boolean isActive() { return isActive; }
    public String getFullName() { return firstName + " " + surName; }

    // Setters
    public void setStaffId(String staffId) { this.staffId = staffId; }
    public void setUsername(String username) { this.username = username; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setSurName(String surName) { this.surName = surName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    public void setRole(String role) { this.role = role; }
    public void setPassword(String password) { this.password = password; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public String toString() {
        return getFullName() + " (" + staffId + ")";
    }
}
