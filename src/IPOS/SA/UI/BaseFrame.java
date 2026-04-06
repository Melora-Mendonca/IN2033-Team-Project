package IPOS.SA.UI;

import IPOS.SA.ACC.UI.*;
import IPOS.SA.CAT.UI.Catalogue;
import IPOS.SA.ORD.UI.OrderProcessingFrame;
import IPOS.SA.ORD.UI.OrderTrackingFrame;
import IPOS.SA.ORD.UI.PaymentRecording;
import IPOS.SA.RPT.UI.CommercialAppForm;
import IPOS.SA.RPT.UI.ReportForm;
import IPOS.SA.ACC.UI.SettingsForm;
import IPOS.SA.ORD.UI.InvoiceListFrame;
import IPOS.SA.ORD.UI.OrderManagement;

import javax.swing.*;
import java.awt.*;

public abstract class BaseFrame extends JFrame {

    protected String fullname;
    protected String role;
    protected String username;

    // Common UI Components
    protected JPanel MainPanel;
    protected JPanel ContentPanel;
    protected JPanel NavPanel;
    protected JPanel HeaderPanel;
    private JPanel FooterPanel;
    protected JPanel CenterPanel;
    protected JLabel headerLabel;
    protected JLabel headerSubTitle;
    protected JLabel navIcon;
    protected JButton logoutBtn;
    protected JSeparator divider;


    // Old constructor — still works for all existing classes
    public BaseFrame(String fullname, String role, String title) {
        this(fullname, role, null, title);
    }

    // New constructor — used when username is needed
    public BaseFrame(String fullname, String role, String username, String title) {
        this.fullname = fullname;
        this.role     = role;
        this.username = username;

        setTitle(title);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        // Initialize main panel
        MainPanel = new JPanel(new BorderLayout());
        setContentPane(MainPanel);

        // Create common components
        createHeaderPanel();
        createNavPanel();
        createContentContainer();

        setVisible(true);
    }

    private void createHeaderPanel() {
        HeaderPanel = new JPanel();
        HeaderPanel.setLayout(new BoxLayout(HeaderPanel, BoxLayout.X_AXIS));
        HeaderPanel.setPreferredSize(new Dimension(1000, 54));
        HeaderPanel.setBackground(new Color(240, 252, 255));
        HeaderPanel.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        headerLabel = new JLabel(getHeaderTitle());
        headerLabel.setForeground(Color.BLACK);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        headerSubTitle = new JLabel(role);
        headerSubTitle.setForeground(new Color(107, 114, 128));
        headerSubTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        textPanel.add(headerLabel);
        textPanel.add(headerSubTitle);
        HeaderPanel.add(textPanel);
    }

    private void createNavPanel() {
        NavPanel = new JPanel();
        NavPanel.setLayout(new BoxLayout(NavPanel, BoxLayout.Y_AXIS));
        NavPanel.setBackground(new Color(14, 37, 48));
        NavPanel.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));
        NavPanel.setPreferredSize(new Dimension(220, 0));

        ImageIcon Icon = new ImageIcon(new ImageIcon("data/Logo.png")
                .getImage().getScaledInstance(80, 60, Image.SCALE_SMOOTH));
        navIcon = new JLabel(Icon);

        NavPanel.add(navIcon);
        NavPanel.add(Box.createVerticalStrut(16));

        // ROLE-SPECIFIC NAVIGATION

        // ADMINISTRATOR - Full access
        if (role.equals("Administrator")) {

            NavPanel.add(buildNavButton("Overview", false));
            NavPanel.add(Box.createVerticalStrut(4));
            NavPanel.add(buildNavButton("Catalogue", false));
            NavPanel.add(Box.createVerticalStrut(4));

            addExpandableNavItem("Merchants", new String[]{
                    "View Merchant Orders",
                    "View Merchant Invoices"
            });
            addExpandableNavItem("Accounts", new String[]{
                    "View All Merchants",
                    "Create Merchant Account",
                    "Manage Merchant Accounts",
                    "Commercial Applications"
            });
            addExpandableNavItem("Staff", new String[]{
                    "View All Staff",
                    "Create Staff Account",
                    "Manage Staff Account",
            });
            addExpandableNavItem("Orders", new String[]{
                    "View All Orders",
                    "Manage Orders"
            });
            NavPanel.add(buildNavButton("Reports", false));
            NavPanel.add(Box.createVerticalStrut(4));
            NavPanel.add(buildNavButton("Settings", false));
            NavPanel.add(Box.createVerticalStrut(4));
        }

        // MANAGER (Director of Operations) - Limited access
        else if (role.equals("Director of Operations")) {
            NavPanel.add(buildNavButton("Overview", false));
            NavPanel.add(Box.createVerticalStrut(4));
            NavPanel.add(buildNavButton("Catalogue", false));
            NavPanel.add(Box.createVerticalStrut(4));

            addExpandableNavItem("Merchants", new String[]{
                    "View Merchant Orders",
                    "View Merchant Invoices"
            });
            addExpandableNavItem("Accounts", new String[]{
                    "View All Merchants",
                    "Manage Merchant Accounts"
            });
            addExpandableNavItem("Staff", new String[]{
                    "View All Staff"
            });
            addExpandableNavItem("Invoices", new String[]{
                    "View All Invoices"
            });

            NavPanel.add(buildNavButton("Reports", false));
            NavPanel.add(Box.createVerticalStrut(4));
            NavPanel.add(buildNavButton("Settings", false));
            NavPanel.add(Box.createVerticalStrut(4));

        }

        // WAREHOUSE STAFF - Order processing only
        else if (role.equals("Warehouse Employee")) {
            NavPanel.add(buildNavButton("Overview", false));
            NavPanel.add(Box.createVerticalStrut(4));

            addExpandableNavItem("Orders", new String[]{
                    "View All Orders",
                    "Manage Orders"
            });

            NavPanel.add(buildNavButton("Settings", false));
            NavPanel.add(Box.createVerticalStrut(4));

        }

        // WAREHOUSE STAFF - Order processing only
        else if (role.equals("Delivery Employee")) {
            NavPanel.add(buildNavButton("Overview", false));
            NavPanel.add(Box.createVerticalStrut(4));

            addExpandableNavItem("Orders", new String[]{
                    "View All Orders",
                    "Manage Orders"
            });

            NavPanel.add(buildNavButton("Settings", false));
            NavPanel.add(Box.createVerticalStrut(4));

        }

        // ACCOUNTANTS - Invoice and payment only
        else if (role.equals("Senior Accountant")) {
            NavPanel.add(buildNavButton("Overview", false));
            NavPanel.add(Box.createVerticalStrut(4));
            addExpandableNavItem("Invoices", new String[]{
                    "View All Invoices"
            });
            addExpandableNavItem("Payments", new String[]{
                    "Record Payments",
                    "View Debtors List",
                    "View Payment History"

            });
            NavPanel.add(buildNavButton("Settings", false));
            NavPanel.add(Box.createVerticalStrut(4));
        }

        else if (role.equals("Accountant")) {
            NavPanel.add(buildNavButton("Overview", false));
            NavPanel.add(Box.createVerticalStrut(4));
            addExpandableNavItem("Invoices", new String[]{
                    "View All Invoices"
            });
            addExpandableNavItem("Payments", new String[]{
                    "Record Payments"
            });
            NavPanel.add(buildNavButton("Settings", false));
            NavPanel.add(Box.createVerticalStrut(4));
        }

        divider = new JSeparator();
        divider.setForeground(Color.WHITE);
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        NavPanel.add(divider);
        NavPanel.add(Box.createVerticalGlue());

        logoutBtn = new JButton("→  Log out");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logoutBtn.setForeground(new Color(200, 80, 80));
        logoutBtn.setBackground(new Color(14, 37, 48));
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        logoutBtn.setHorizontalAlignment(SwingConstants.LEFT);
        logoutBtn.addActionListener(e -> handleLogout());
        NavPanel.add(logoutBtn);
    }

    private void createContentContainer() {
        ContentPanel = new JPanel(new BorderLayout());
        ContentPanel.setBackground(new Color(245, 247, 250));

        CenterPanel = new JPanel(new BorderLayout());
        CenterPanel.setBackground(new Color(245, 247, 250));
        CenterPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        ContentPanel.add(CenterPanel, BorderLayout.CENTER);

        // Assemble MainPanel
        MainPanel.add(HeaderPanel, BorderLayout.NORTH);
        MainPanel.add(NavPanel, BorderLayout.WEST);
        MainPanel.add(ContentPanel, BorderLayout.CENTER);
    }

    protected void addExpandableNavItem(String label, String[] subItems) {
        JButton mainBtn = new JButton(label);
        mainBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mainBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        mainBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainBtn.setHorizontalAlignment(SwingConstants.LEFT);
        mainBtn.setFocusPainted(false);
        mainBtn.setBorderPainted(false);
        mainBtn.setBackground(new Color(14, 37, 48));
        mainBtn.setForeground(new Color(160, 190, 210));

        JPanel subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));
        subPanel.setBackground(new Color(10, 28, 38));
        subPanel.setVisible(false);

        for (String sub : subItems) {
            JButton subBtn = new JButton("    › " + sub);
            subBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            subBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            subBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
            subBtn.setHorizontalAlignment(SwingConstants.LEFT);
            subBtn.setFocusPainted(false);
            subBtn.setBorderPainted(false);
            subBtn.setBackground(new Color(10, 28, 38));
            subBtn.setForeground(new Color(120, 160, 185));

            subBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    subBtn.setForeground(Color.WHITE);
                    subBtn.setBackground(new Color(20, 50, 65));
                }
                public void mouseExited(java.awt.event.MouseEvent e) {
                    subBtn.setForeground(new Color(120, 160, 185));
                    subBtn.setBackground(new Color(10, 28, 38));
                }
            });

            subBtn.addActionListener(e -> handleSubNavClick(sub));
            subPanel.add(subBtn);
            subPanel.add(Box.createVerticalStrut(2));
        }

        mainBtn.addActionListener(e -> {
            boolean showing = subPanel.isVisible();
            subPanel.setVisible(!showing);
            mainBtn.setForeground(showing ? new Color(160, 190, 210) : Color.WHITE);
            mainBtn.setBackground(showing ? new Color(14, 37, 48) : new Color(20, 45, 60));
            NavPanel.revalidate();
            NavPanel.repaint();
        });

        NavPanel.add(mainBtn);
        NavPanel.add(subPanel);
        NavPanel.add(Box.createVerticalStrut(4));
    }

    protected void handleSubNavClick(String label) {
        switch (label) {
            case "View All Merchants":
                dispose();
                new MerchantList(fullname, role);
                break;
            case "Create Merchant Account":
                dispose();
                new AccountManagement(fullname, role, "CREATE");
                break;
            case "Manage Merchant Accounts":
                dispose();
                new AccountManagement(fullname, role, "MANAGE");
                break;
            case "View All Staff":
                dispose();
                new StaffList(fullname, role);
                break;
            case "Create Staff Account":
                dispose();
                new StaffAccountManagement(fullname, role, "CREATE");
                break;
            case "Manage Staff Account":
                dispose();
                new StaffAccountManagement(fullname, role, "MANAGE");
                break;
            case "Commercial Applications":
                dispose();
                new CommercialAppForm(fullname, role);
                break;
            case "View All Orders":
                dispose();
                new OrderManagement(fullname, role);
                break;
            case "Manage Orders":
                dispose();
                new OrderProcessingFrame(fullname, role);
                break;
            case "Reports":
                dispose();
                new ReportForm(fullname, role);
                break;
            case "Settings":
                dispose();
                new SettingsForm(fullname, role, username);
                break;
            case "View All Invoices":
                dispose();
                new InvoiceListFrame(fullname, role);
                break;
            case "View Merchant Orders":
                dispose();
                new MerchantList(fullname, role, "ORDERS");
                break;
            case "View Merchant Invoices":
                dispose();
                new MerchantList(fullname, role, "INVOICES");
                break;
            case "Record Payments":
                dispose();
                new PaymentRecording(fullname, role);
                break;
            case "View Debtors List":
                dispose();
                break;
            case "View Payment History":
                dispose();
                break;
            default:
                JOptionPane.showMessageDialog(this, label + " — coming soon.");
                break;
        }
    }

    protected JButton buildNavButton(String label, boolean active) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 13));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBackground(active ? new Color(30, 70, 90) : new Color(14, 37, 48));
        btn.setForeground(active ? Color.WHITE : new Color(160, 190, 210));

        btn.addActionListener(e -> {
            switch (label) {
                case "Catalogue":
                    dispose();
                    new Catalogue(fullname, role);
                    break;
                case "Overview":
                    // Open appropriate dashboard based on role
                    if (role.equals("Administrator")) {
                        dispose();
                        new AdminDashboard(fullname, role, username);
                        break;
                    } else if (role.equals("Director of Operations")) {
                        dispose();
                        new ManagerDashboard(fullname, role, username);
                        break;
                    } else {
                        dispose();
                        new StaffDashboard(fullname, role, username);
                        break;
                    }
                case "Process Orders":
                    dispose();
                    new OrderProcessingFrame(fullname, role);
                    break;
                case "Track Orders":
                    dispose();
                    new OrderTrackingFrame(fullname, role);
                    break;
                case "Payments":
                    dispose();
                    new PaymentRecording(fullname, role);
                    break;
                case "Reports":
                    dispose();
                    new ReportForm(fullname, role);
                    break;
                case "Settings":
                    dispose();
                    new SettingsForm(fullname, role, username);
                    break;
                default:
                    JOptionPane.showMessageDialog(this, label + " — coming soon.");
                    break;
            }
        });

        return btn;
    }

    protected void handleLogout() {
        dispose();
        new LoginForm();
    }

    // Abstract methods for subclasses
    protected abstract String getHeaderTitle();

}