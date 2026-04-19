package IPOS.SA.RPT.Model;

import java.time.LocalDate;
/**
 * Represents a commercial membership application submitted by a prospective merchant.
 * Applications are submitted via IPOS-CA or the REST API and reviewed by
 * InfoPharma administrators. On approval, a merchant account is automatically created.
 *
 * Status values: pending, approved, rejected.
 */
public class CommercialApplication {

    private int applicationId;
    private String companyName;
    private String registrationNo;
    private String businessType;
    private String directorName;
    private String email;
    private String phone;
    private String fax;
    private String address;
    private LocalDate applicationDate;
    private String status;
    private Integer reviewedBy;
    private LocalDate reviewDate;
    private String reviewNotes;
    /**
     * Represents a commercial membership application submitted by a prospective merchant.
     * Applications are submitted via IPOS-CA or the REST API and reviewed by
     * InfoPharma administrators. On approval, a merchant account is automatically created.
     *
     * Status values: pending, approved, rejected.
     */
    public CommercialApplication() {}
    /**
     * Constructor — creates a new application with all required details.
     * Status is set to pending and application date is set to today.
     *
     * @param companyName    the name of the applying company
     * @param registrationNo the company registration number
     * @param businessType   the type of business
     * @param directorName   the name of the company director
     * @param email          the contact email address
     * @param phone          the contact phone number
     * @param fax            the company fax number
     * @param address        the company registered address
     */
    public CommercialApplication(String companyName, String registrationNo, String businessType, String directorName, String email, String phone, String fax, String address) {
        this.companyName = companyName;
        this.registrationNo = registrationNo;
        this.businessType = businessType;
        this.directorName = directorName;
        this.email = email;
        this.phone = phone;
        this.fax = fax;
        this.address = address;
        this.status = "pending";
        this.applicationDate = LocalDate.now();
    }
    /**
     * Returns the unique application identifier.
     *
     * @return the application ID
     */
    public int getApplicationId() {
        return applicationId;
    }
    /**
     * Returns the name of the applying company.
     *
     * @return the company name
     */
    public String getCompanyName() {
        return companyName;
    }
    /**
     * Returns the company registration number.
     *
     * @return the registration number
     */
    public String getRegistrationNo() {
        return registrationNo;
    }
    /**
     * Returns the type of business.
     *
     * @return the business type
     */
    public String getBusinessType() {
        return businessType;
    }
    /**
     * Returns the name of the company director.
     *
     * @return the director name
     */
    public String getDirectorName() {
        return directorName;
    }
    /**
     * Returns the contact email address.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }
    /**
     * Returns the contact email address.
     *
     * @return the email address
     */
    public String getPhone() {
        return phone;
    }
    /**
     * Returns the contact phone number.
     *
     * @return the phone number
     */
    public String getFax() {
        return fax;
    }
    /**
     * Returns the company fax number.
     *
     * @return the fax number
     */
    public String getAddress() {
        return address;
    }
    /**
     * Returns the company registered address.
     *
     * @return the address
     */
    public LocalDate getApplicationDate() {
        return applicationDate;
    }
    /**
     * Returns the current status of the application.
     *
     * @return the status string — pending, approved or rejected
     */
    public String getStatus() {
        return status;
    }
    /**
     * Returns the user ID of the staff member who reviewed the application.
     *
     * @return the reviewer's user ID, or null if not yet reviewed
     */
    public Integer getReviewedBy() {
        return reviewedBy;
    }
    /**
     * Returns the date the application was reviewed.
     *
     * @return the review date, or null if not yet reviewed
     */
    public LocalDate getReviewDate() {
        return reviewDate;
    }
    /**
     * Returns the notes added by the reviewer.
     *
     * @return the review notes, or null if not yet reviewed
     */
    public String getReviewNotes() {
        return reviewNotes;
    }
    /**
     * Sets the unique application identifier.
     *
     * @param applicationId the application ID
     */

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }
    /**
     * Sets the company name.
     *
     * @param companyName the company name
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    /**
     * Sets the company registration number.
     *
     * @param registrationNo the registration number
     */
    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }
    /**
     * Sets the type of business.
     *
     * @param businessType the business type
     */
    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }
    /**
     * Sets the name of the company director.
     *
     * @param directorName the director name
     */
    public void setDirectorName(String directorName) {
        this.directorName = directorName;
    }
    /**
     * Sets the contact email address.
     *
     * @param email the email address
     */
    public void setEmail(String email) {
        this.email = email;
    }
    /**
     * Sets the contact phone number.
     *
     * @param phone the phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }
    /**
     * Sets the company fax number.
     *
     * @param fax the fax number
     */
    public void setFax(String fax) {
        this.fax = fax;
    }
    /**
     * Sets the company registered address.
     *
     * @param address the address
     */
    public void setAddress(String address) {
        this.address = address;
    }
    /**
     * Sets the application submission date.
     *
     * @param date the application date
     */
    public void setApplicationDate(LocalDate date) {
        this.applicationDate = date;
    }
    /**
     * Sets the application status.
     *
     * @param status the status string — pending, approved or rejected
     */
    public void setStatus(String status) {
        this.status = status;
    }
    /**
     * Sets the user ID of the staff member who reviewed the application.
     *
     * @param reviewedBy the reviewer's user ID
     */
    public void setReviewedBy(Integer reviewedBy) {
        this.reviewedBy = reviewedBy;
    }
    /**
     * Sets the date the application was reviewed.
     *
     * @param reviewDate the review date
     */
    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }
    /**
     * Sets the notes added by the reviewer.
     *
     * @param reviewNotes the review notes
     */
    public void setReviewNotes(String reviewNotes) {
        this.reviewNotes = reviewNotes;
    }
}
