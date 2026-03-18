package IPOS.SA.ACC;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AccountCreationFrame extends JFrame {

    private final AccountService accountService;

    private JTextField merchantIdField;
    private JTextField businessNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;
    private JTextField creditLimitField;
    private JTextField discountValueField;

    private JLabel messageLabel;

    public AccountCreationFrame(AccountService accountService) {
        this.accountService = accountService;

        setTitle("Create Merchant Account");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(root);

        JLabel title = new JLabel("Create New Merchant Account");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(8, 2, 10, 10));
        root.add(form, BorderLayout.CENTER);

        merchantIdField = new JTextField();
        businessNameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();
        addressField = new JTextField();
        creditLimitField = new JTextField();
        discountValueField = new JTextField();

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

        messageLabel = new JLabel("");
        form.add(messageLabel);

        JPanel buttons = new JPanel(new FlowLayout());
        root.add(buttons, BorderLayout.SOUTH);

        JButton createBtn = new JButton("Create");
        JButton clearBtn = new JButton("Clear");

        buttons.add(createBtn);
        buttons.add(clearBtn);

        createBtn.addActionListener(e -> createAccount());
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

            if (id.isEmpty() || name.isEmpty()) {
                messageLabel.setText("ID and Name required.");
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
                    id,
                    name,
                    email,
                    phone,
                    address,
                    credit,
                    plan
            );

            accountService.addAccount(account);

            messageLabel.setText("Account created successfully.");

        } catch (NumberFormatException ex) {
            messageLabel.setText("Invalid number input.");
        } catch (Exception ex) {
            messageLabel.setText("Error: " + ex.getMessage());
        }
    }

    private void clearForm() {
        merchantIdField.setText("");
        businessNameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressField.setText("");
        creditLimitField.setText("");
        discountValueField.setText("");
        messageLabel.setText("");
    }
}