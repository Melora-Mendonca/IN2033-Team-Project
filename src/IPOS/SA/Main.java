package IPOS.SA.ACC;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AccountService accountService = new AccountService();

            AccountCreationFrame creationFrame = new AccountCreationFrame(accountService);
            creationFrame.setVisible(true);

            AccountManagementFrame managementFrame = new AccountManagementFrame(accountService);
            managementFrame.setVisible(true);
        });
    }
}