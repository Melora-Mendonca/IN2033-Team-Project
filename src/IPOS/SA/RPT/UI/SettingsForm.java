package IPOS.SA.RPT.UI;

import javax.swing.*;
import java.awt.*;
import IPOS.SA.UI.BaseFrame;
import IPOS.SA.DB.DBConnection;

//view and update their account details
public class SettingsForm extends BaseFrame {

    //input fields for user data
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea addressArea;

    //variables that store user information
    private String email;
    private String phone;
    private String address;

    //settings constructor
    public SettingsForm(String fullname, String role,
                        String email, String phone, String address) {
        //BaseFrame constructor
        super(fullname, role, "Settings");

        //stores user data locally
        this.email = email;
        this.phone = phone;
        this.address = address;

        //builds the UI components
        buildContent();
    }

    //secondary constructor
    public SettingsForm(String fullname, String role) {
        this(fullname, role, "", "", "");
    }

    //sets the title for the header
    @Override
    protected String getHeaderTitle() {
        return "Settings";
    }

    //builds the UI for the settings page
    private void buildContent() {

        CenterPanel.setLayout(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        //name field
        formPanel.add(new JLabel("Full Name:"));
        nameField = new JTextField(fullname); // pre-filled with existing name
        formPanel.add(nameField);

        //email field
        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField(email); // pre-filled
        formPanel.add(emailField);

        //phone field
        formPanel.add(new JLabel("Phone:"));
        phoneField = new JTextField(phone); // pre-filled
        formPanel.add(phoneField);

        //address field
        formPanel.add(new JLabel("Address:"));
        addressArea = new JTextArea(address, 2, 20); // multi-line text
        formPanel.add(new JScrollPane(addressArea));

        //save button
        JButton saveBtn = new JButton("Save Changes");

        //calls handleSave function when button is pressed
        saveBtn.addActionListener(e -> handleSave());

        //adds form panel and save button to main panel
        CenterPanel.add(formPanel, BorderLayout.CENTER);
        CenterPanel.add(saveBtn, BorderLayout.SOUTH);
    }

    //handles saving updated settings to database
    private void handleSave() {

        //gets updated values from input fields
        String newName = nameField.getText();
        String newEmail = emailField.getText();
        String newPhone = phoneField.getText();
        String newAddress = addressArea.getText();

        //checks fields
        if (newName.isEmpty() || newEmail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Email are required.");
            return;
        }

        //email validation
        if (!newEmail.contains("@")) {
            JOptionPane.showMessageDialog(this, "Invalid email.");
            return;
        }

        try {
            //connects to database
            DBConnection db = new DBConnection();

            //updates full name
            db.update(
                    "UPDATE User_Login SET fullname=? WHERE fullname=?",
                    newName, this.fullname
            );

            //updates email, phone and address
            db.update(
                    "UPDATE Merchant_Details SET email=?, phone=?, address=? WHERE email=?",
                    newEmail, newPhone, newAddress, this.email
            );

            //shows success message
            JOptionPane.showMessageDialog(this, "Settings updated successfully!");

            //update local variables with new values
            this.fullname = newName;
            this.email = newEmail;
            this.phone = newPhone;
            this.address = newAddress;

            db.closeConnection();

        } catch (Exception e) {
            e.printStackTrace();

            //shows error message to user
            JOptionPane.showMessageDialog(this, "Database error.");
        }
    }
}