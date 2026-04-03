package IPOS.SA.RPT.Model;

import java.time.LocalDate;

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

    public CommercialApplication() {}

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

    public int getApplicationId() {
        return applicationId;
    }
    public String getCompanyName() {
        return companyName;
    }
    public String getRegistrationNo() {
        return registrationNo;
    }
    public String getBusinessType() {
        return businessType;
    }
    public String getDirectorName() {
        return directorName;
    }
    public String getEmail() {
        return email;
    }
    public String getPhone() {
        return phone;
    }
    public String getFax() {
        return fax;
    }
    public String getAddress() {
        return address;
    }
    public LocalDate getApplicationDate() {
        return applicationDate;
    }
    public String getStatus() {
        return status;
    }
    public Integer getReviewedBy() {
        return reviewedBy;
    }
    public LocalDate getReviewDate() {
        return reviewDate;
    }
    public String getReviewNotes() {
        return reviewNotes;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }
    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }
    public void setDirectorName(String directorName) {
        this.directorName = directorName;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setFax(String fax) {
        this.fax = fax;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setApplicationDate(LocalDate date) {
        this.applicationDate = date;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setReviewedBy(Integer reviewedBy) {
        this.reviewedBy = reviewedBy;
    }
    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }
    public void setReviewNotes(String reviewNotes) {
        this.reviewNotes = reviewNotes;
    }
}
