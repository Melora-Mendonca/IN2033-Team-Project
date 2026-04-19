package IPOS.SA.ACC.Model;

/**
 * Represents a staff member account in the IPOS-SA system.
 * Stores personal details, login credentials and role information
 * for all InfoPharma staff who use the system.
 */
public class Staff {
    private String staffId; // the unique Staff ID
    private String username; // the username for the staff
    private String firstName; // the first name of the staff
    private String surName; // the surname of the staff
    private String email; // the email of the staff
    private String phone; // the phone number of the staff
    private String address; // the current address of the staff
    private String role; // the role fo the staff
    private String password; // the password created for the staff
    private boolean isActive; // whether the staff account is currently active or not

    /**
     * Default constructor — creates an empty staff object with active status.
     */
    public Staff() {
        this.isActive = true;
    }

    /**
     * Full constructor — creates a staff account with all required details.
     * Account is set to active by default.
     *
     * @param staffId   unique staff identifier
     * @param username  login username
     * @param firstName first name
     * @param surName   surname
     * @param email     contact email
     * @param phone     contact phone number
     * @param address   home address
     * @param role      staff role
     */
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

    // Getter methods to retrieve data
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

    // Setter methods to assign data and store it
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

    /**
     * Returns a string representation of the staff member
     * showing their full name and staff ID.
     *
     * @return formatted string with the full name and ID of the staff
     */
    @Override
    public String toString() {
        return getFullName() + " (" + staffId + ")";
    }
}
