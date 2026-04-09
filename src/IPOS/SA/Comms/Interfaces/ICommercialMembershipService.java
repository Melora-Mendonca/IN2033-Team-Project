package IPOS.SA.Comms.Interfaces;

public interface ICommercialMembershipService {

    /**
     * Allows PU to pass an application for a commercial member to SA, string is a JSON containing details including company registration number, details on the Company Director(s), type of business, address, email address, etc.
     * @param companyName A string containing company name of the candidate applying for membership
     * @param registrationNumber Details on the registration number of the company
     * @param directors Details on company directors.
     * @param businessType Details on the type of Business the candidate is part of
     * @param address Details on the address of the candidate's business
     * @param email Email address of the candidate's business
     * @param fax Deails for Fax to company
     * String stores an array-like structure: [Name, Role, Email; Name, Role, Email] to facilitate InfoPharma staff diligence checks.
     * @param preferPhysicalMail True if commercial customer prefers to receive the confirmation by regular mail
     */
    public boolean requestMembership(String companyName, String registrationNumber, String directors, String businessType, String address, String email, String fax, boolean preferPhysicalMail);
}
