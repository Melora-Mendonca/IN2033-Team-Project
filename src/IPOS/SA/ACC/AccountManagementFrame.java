package IPOS.SA.ACC;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AccountManagementFrame extends JFrame {

    private final AccountService accountService;

    private JTextField merchantIdField;
    private JTextField businessNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;
    private JTextField creditLimitField;
    private JTextField discountValueField;

    private JLabel balanceLabel;
    private JLabel statusLabel;
    private JLabel messageLabel;

    public AccountManagementFrame(AccountService accountService) {
        this.accountService = accountService;

        setTitle("IPOS-SA Account Management");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(root);

        JLabel title = new JLabel("Merchant Account Management");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(10, 2, 10, 10));
        root.add(form, BorderLayout.CENTER);

        merchantIdField = new JTextField();
        businessNameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();
        addressField = new JTextField();
        creditLimitField = new JTextField();
        discountValueField = new JTextField();

        balanceLabel = new JLabel("0.00");
        statusLabel = new JLabel("NORMAL");
        messageLabel = new JLabel("");

        form.add(new JLabel("Merchant ID:"));
        form.add(merchantIdField);

        form.add(new JLabel("Business Name:"));
        form.add(businessNameField);

        form.add(new JLabel("Email:"));
        form.add(emailField);

        form.add(new JLabel("Phone:"));
        form.add(phoneField);

        form.add(new JLabel("Address:"));
        form.add(addressField);

        form.add(new JLabel("Credit Limit:"));
        form.add(creditLimitField);

        form.add(new JLabel("Discount %:"));
        form.add(discountValueField);

        form.add(new JLabel("Outstanding Balance:"));
        form.add(balanceLabel);

        form.add(new JLabel("Account Status:"));
        form.add(statusLabel);

        form.add(messageLabel);

        JPanel buttons = new JPanel(new FlowLayout());
        root.add(buttons, BorderLayout.SOUTH);

        JButton createBtn = new JButton("Create");
        JButton loadBtn = new JButton("Load");
        JButton updateBtn = new JButton("Update");
        JButton clearBtn = new JButton("Clear");

        buttons.add(createBtn);
        buttons.add(loadBtn);
        buttons.add(updateBtn);
        buttons.add(clearBtn);

        createBtn.addActionListener(e -> createAccount());
        loadBtn.addActionListener(e -> loadAccount());
        updateBtn.addActionListener(e -> updateAccount());
        clearBtn.addActionListener(e -> clearForm());
    }

    private void createAccount() {
        try {
            String id = merchantIdField.getText().trim();
            String name = businessNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();
            double credit = Double.parseDouble(creditLimitField.getText().trim());
            double discount = Double.parseDouble(discountValueField.getText().trim());

            if (id.isEmpty()) {
                messageLabel.setText("Merchant ID is required.");
                return;
            }
            if (name.isEmpty()) {
                messageLabel.setText("Business name is required.");
                return;
            }
            if (credit < 0) {
                messageLabel.setText("Credit limit cannot be negative.");
                return;
            }
            if (discount < 0) {
                messageLabel.setText("Discount cannot be negative.");
                return;
            }
            if (accountService.accountExists(id)) {
                messageLabel.setText("Account already exists.");
                return;
            }

            DiscountPlan plan = new FixedDiscountPlan("Fixed Plan", discount);

            MerchantAccount account = new MerchantAccount(
                    id, name, email, phone, address, credit, plan
            );

            accountService.addAccount(account);

            messageLabel.setText("Account created.");
            populate(account);

        } catch (Exception ex) {
            messageLabel.setText("Error: " + ex.getMessage());
        }
    }

    private void loadAccount() {
        MerchantAccount acc = accountService.getAccount(merchantIdField.getText().trim());

        if (acc == null) {
            messageLabel.setText("Account not found.");
            return;
        }

        populate(acc);
        messageLabel.setText("Loaded.");
    }

    private void updateAccount() {
        try {
            MerchantAccount acc = accountService.getAccount(merchantIdField.getText().trim());

            if (acc == null) {
                messageLabel.setText("Load account first.");
                return;
            }

            acc.setBusinessName(businessNameField.getText());
            acc.setEmail(emailField.getText());
            acc.setPhone(phoneField.getText());
            acc.setAddress(addressField.getText());
            acc.setCreditLimit(Double.parseDouble(creditLimitField.getText()));

            double discount = Double.parseDouble(discountValueField.getText());
            acc.setDiscountPlan(new FixedDiscountPlan("Fixed Plan", discount));

            populate(acc);
            messageLabel.setText("Updated.");

        } catch (Exception ex) {
            messageLabel.setText("Error: " + ex.getMessage());
        }
    }

    private void populate(MerchantAccount acc) {
        merchantIdField.setText(acc.getMerchantId());
        businessNameField.setText(acc.getBusinessName());
        emailField.setText(acc.getEmail());
        phoneField.setText(acc.getPhone());
        addressField.setText(acc.getAddress());
        creditLimitField.setText(String.valueOf(acc.getCreditLimit()));
        if (acc.getDiscountPlan() instanceof FixedDiscountPlan) {
            discountValueField.setText(String.valueOf(((FixedDiscountPlan) acc.getDiscountPlan()).getPercentage()));
        }
        balanceLabel.setText(String.valueOf(acc.getOutstandingBalance()));
        statusLabel.setText(acc.getStatus().name());
    }

    private void clearForm() {
        merchantIdField.setText("");
        businessNameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressField.setText("");
        creditLimitField.setText("");
        discountValueField.setText("");
        balanceLabel.setText("0.00");
        statusLabel.setText("NORMAL");
        messageLabel.setText("");
    }
}