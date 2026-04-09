package IPOS.SA.Comms.Interfaces;

import IPOS.SA.Comms.PUClient.EmailService;
import IPOS.SA.DB.DBConnection;

/**
 * Default implementation of {@link ICommercialMembershipService}.
 *
 * This class handles commercial membership requests within the subsystem.
 * It depends on {@link EmailService} to produce notifications related
 * to membership submissions or confirmations.
 */

public class ICommercialMembershipImpl implements ICommercialMembershipService {
    /**
     * Email service used to send membership-related notifications.
     */
    private final EmailService emailService;
    private final DBConnection db;

    public ICommercialMembershipImpl() {
        this.emailService = new EmailService();
        this.db = new DBConnection();
    }

    /**
     * Submits a commercial membership request for processing.
     *
     * @param companyName A string containing company name of the candidate applying for membership
     * @param registrationNumber Details on the registration number of the company
     * @param directors Details on company directors.
     * @param businessType Details on the type of Business the candidate is part of
     * @param address Details on the address of the candidate's business
     * @param email Email address of the candidate's business
     * @param fax Details for Fax to company
     * @param preferPhysicalMail True if commercial customer prefers to receive the confirmation by regular mail
     * @return true if the membership request was accepted for processing, false otherwise
     */

    public boolean requestMembership(String companyName, String registrationNumber,
                                     String directors, String businessType,
                                     String address, String email, String fax,
                                     boolean preferPhysicalMail) {
        try {

            int rows = db.update(
                    "INSERT INTO commercial_applications " +
                            "(company_name, registration_number, directors, " +
                            "business_type, address, email, fax, prefer_physical_mail, status, application_date) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'pending', CURRENT_DATE())",
                    companyName, registrationNumber, directors,
                    businessType, address, email, fax, preferPhysicalMail ? 1 : 0
            );

            if (rows > 0) {
                System.out.println("Membership application submitted for: " + companyName);
                return true;
            }
            return false;

        } catch (Exception e) {
            System.err.println("Error submitting membership application: " + e.getMessage());
            return false;
        }
    }
}
