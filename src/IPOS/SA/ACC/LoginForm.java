package IPOS.SA.ACC;

import javax.swing.*;
import java.awt.*;

public class LoginForm extends JFrame{
    private JPanel MainPanel;
    private JPanel HeaderPanel;
    private JPanel ContentPanel;
    private JPanel LogoPanel;
    private JPanel FooterPanel;
    private JPanel LoginPanel;

    public LoginForm() {
        // Create the Frame
        setTitle("Login Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(MainPanel);
        pack();
        // Set the frame location to the center of the screen
        setLocationRelativeTo(null);

        // HEADER:
        // Sets a Layout for the Header panel, to correctly position the Labels
        HeaderPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 60, 15));
        HeaderPanel.setBackground(new Color(14,37,48)); // dark colour

        // Create and style the label
        //JLabel headerIcon = new JLabel();
        JLabel headerLabel = new JLabel("InfoPharma Ordering System");
        headerLabel.setForeground(Color.WHITE); // text colour, not background
        headerLabel.setFont(new Font("Arial", Font.BOLD, 15));

        // Add to panel
        //HeaderPanel.add(headerIcon);
        HeaderPanel.add(headerLabel);

        //LOGO PANEL


























//        ContentPanel.setBackground(new Color(255,255,255,65));
//        ContentPanel.setBorder(BorderFactory.createLineBorder(Color.white));
//
//        // Text fields
//        UsernameField1.setBackground(new Color(255,255,255,75));
//        passwordField1.setBackground(new Color(255,255,255,75));
//
//// Buttons
//        JButton[] buttons = {loginButton, merchantButton, managerButton, adminButton};
//        for (JButton btn : buttons) {
//            btn.setBackground(new Color(255,255,255,75));
//        }
//
//        // Set fonts and font styles for the subtitle and login credential Labels
//        SubtitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
//        userNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
//        passWordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
//        UsernameField1.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        passwordField1.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//
//
//        UsernameField1.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(new Color(200,200,200)),
//                BorderFactory.createEmptyBorder(5,10,5,10)));
//
//        passwordField1.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(new Color(200,200,200)),
//                BorderFactory.createEmptyBorder(5,10,5,10)));
//
//        loginButton.setFocusPainted(false);
//        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
//
//        MainPanel.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(new Color(220,220,220),1,true),
//                BorderFactory.createEmptyBorder(25,25,25,25)
//        ));
//
//
//        SubtitleLabel.setForeground(new Color(40,40,40));
//
//
//        // Adds functionality to the form buttons
//        merchantButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JOptionPane.showMessageDialog(null, "Welcome to Merchant Login Form");
//            }
//        });
//        managerButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JOptionPane.showMessageDialog(null, "Welcome to Manager Login Form");
//            }
//        });
//        adminButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JOptionPane.showMessageDialog(null, "Welcome to Admin Login Form");
//            }
//        });
//        loginButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JOptionPane.showMessageDialog(null, "Welcome to IPOS");
//            }
//        });

        // Set the frame visible
        setVisible(true);

    }
}
