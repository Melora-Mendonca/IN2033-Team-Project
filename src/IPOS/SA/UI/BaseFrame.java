package IPOS.SA.UI;

import IPOS.SA.ORD.UI.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// Claude AI was used in this form in the role based navigation between forms as well as in the expandable nav panel functionality//

// Shared layout: header, role-based sidebar nav, and a CenterPanel for each screen's content
public abstract class BaseFrame extends JPanel {

    protected String fullname;
    protected String role;
    protected String username;
    protected ScreenRouter router;

    protected JPanel MainPanel;
    protected JPanel ContentPanel;
    protected JPanel NavPanel;
    protected JPanel HeaderPanel;
    protected JPanel CenterPanel;
    protected JLabel headerLabel;
    protected JLabel headerSubTitle;
    protected JLabel navIcon;
    protected JButton logoutBtn;
    protected JSeparator divider;

    public BaseFrame(String fullname, String role, String title, ScreenRouter router) {
        this(fullname, role, null, title, router);
    }

    public BaseFrame(String fullname, String role, String username, String title, ScreenRouter router) {
        this.fullname = fullname;
        this.role = role;
        this.username = username;
        this.router = router;

        setLayout(new BorderLayout());

        MainPanel = new JPanel(new BorderLayout());
        add(MainPanel, BorderLayout.CENTER);

        createHeaderPanel();
        createNavPanel();
        createContentContainer();
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

    // Nav items and sub-menus vary by role
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

        if (role.equals("Administrator")) {
            NavPanel.add(buildNavButton("Overview", false));
            NavPanel.add(Box.createVerticalStrut(4));
            NavPanel.add(buildNavButton("Catalogue", false));
            NavPanel.add(Box.createVerticalStrut(4));
            addExpandableNavItem("Merchants", new String[]{"View Merchant Orders", "View Merchant Invoices"});
            addExpandableNavItem("Accounts", new String[]{"View All Merchants", "Create Merchant Account", "Manage Merchant Accounts", "Commercial Applications"});
            addExpandableNavItem("Staff", new String[]{"View All Staff", "Create Staff Account", "Manage Staff Account"});
            addExpandableNavItem("Orders", new String[]{"View All Orders", "Manage Orders"});
            NavPanel.add(buildNavButton("Reports", false));
            NavPanel.add(Box.createVerticalStrut(4));
            NavPanel.add(buildNavButton("Settings", false));
            NavPanel.add(Box.createVerticalStrut(4));
        } else if (role.equals("Director of Operations")) {
            NavPanel.add(buildNavButton("Overview", false));
            NavPanel.add(Box.createVerticalStrut(4));
            NavPanel.add(buildNavButton("Catalogue", false));
            NavPanel.add(Box.createVerticalStrut(4));
            addExpandableNavItem("Merchants", new String[]{"View Merchant Orders", "View Merchant Invoices"});
            addExpandableNavItem("Accounts", new String[]{"View All Merchants", "Manage Merchant Accounts"});
            addExpandableNavItem("Staff", new String[]{"View All Staff"});
            addExpandableNavItem("Invoices", new String[]{"View All Invoices"});
            NavPanel.add(buildNavButton("Reports", false));
            NavPanel.add(Box.createVerticalStrut(4));
            NavPanel.add(buildNavButton("Settings", false));
            NavPanel.add(Box.createVerticalStrut(4));
        } else if (role.equals("Warehouse Employee") || role.equals("Delivery Employee")) {
            NavPanel.add(buildNavButton("Overview", false));
            NavPanel.add(Box.createVerticalStrut(4));
            addExpandableNavItem("Orders", new String[]{"View All Orders", "Manage Orders"});
            NavPanel.add(buildNavButton("Settings", false));
            NavPanel.add(Box.createVerticalStrut(4));
        } else if (role.equals("Senior Accountant")) {
            NavPanel.add(buildNavButton("Overview", false));
            NavPanel.add(Box.createVerticalStrut(4));
            addExpandableNavItem("Invoices", new String[]{"View All Invoices"});
            addExpandableNavItem("Payments", new String[]{"Record Payments", "View Debtors List", "View Payment History"});
            NavPanel.add(buildNavButton("Settings", false));
            NavPanel.add(Box.createVerticalStrut(4));
        } else if (role.equals("Accountant")) {
            NavPanel.add(buildNavButton("Overview", false));
            NavPanel.add(Box.createVerticalStrut(4));
            addExpandableNavItem("Invoices", new String[]{"View All Invoices"});
            addExpandableNavItem("Payments", new String[]{"Record Payments"});
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

            subBtn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    subBtn.setForeground(Color.WHITE);
                    subBtn.setBackground(new Color(20, 50, 65));
                }
                public void mouseExited(MouseEvent e) {
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

    // Routes sub-nav clicks to the correct screen constant
    protected void handleSubNavClick(String label) {
        switch (label) {
            case "View All Merchants":       router.goTo(AppFrame.SCREEN_MERCHANT_LIST); break;
            case "Create Merchant Account":  router.goTo(AppFrame.SCREEN_ACCOUNT_MANAGEMENT_CREATE); break;
            case "Manage Merchant Accounts": router.goTo(AppFrame.SCREEN_ACCOUNT_MANAGEMENT_MANAGE); break;
            case "View All Staff":           router.goTo(AppFrame.SCREEN_STAFF_LIST); break;
            case "Create Staff Account":     router.goTo(AppFrame.SCREEN_STAFF_ACCOUNT_CREATE); break;
            case "Manage Staff Account":     router.goTo(AppFrame.SCREEN_STAFF_ACCOUNT_MANAGE); break;
            case "Commercial Applications":  router.goTo(AppFrame.SCREEN_COMMERCIAL_APP); break;
            case "View All Orders":          router.goTo(AppFrame.SCREEN_ORDER_MANAGEMENT); break;
            case "Manage Orders":            router.goTo(AppFrame.SCREEN_ORDER_PROCESSING); break;
            case "View All Invoices":        router.goTo(AppFrame.SCREEN_INVOICE_LIST); break;
            case "View Merchant Orders":     router.goTo(AppFrame.SCREEN_MERCHANT_ORDERS); break;
            case "View Merchant Invoices":   router.goTo(AppFrame.SCREEN_MERCHANT_INVOICES); break;
            case "Record Payments":          router.goTo(AppFrame.SCREEN_PAYMENT_RECORDING); break;
            case "View Debtors List":
                new PaymentRecording(fullname, role, router).showDebtorsDialog();
                break;
            case "View Payment History":
                new PaymentRecording(fullname, role, router).showPaymentHistoryDialog();
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
                case "Catalogue": router.goTo(AppFrame.SCREEN_CATALOGUE); break;
                case "Overview":
                    if (role.equals("Administrator")) router.goTo(AppFrame.SCREEN_ADMIN_DASHBOARD);
                    else if (role.equals("Director of Operations")) router.goTo(AppFrame.SCREEN_MANAGER_DASHBOARD);
                    else router.goTo(AppFrame.SCREEN_STAFF_DASHBOARD);
                    break;
                case "Reports":  router.goTo(AppFrame.SCREEN_REPORT); break;
                case "Settings": router.goTo(AppFrame.SCREEN_SETTINGS); break;
                default:
                    JOptionPane.showMessageDialog(this, label + " — coming soon.");
                    break;
            }
        });

        return btn;
    }

    protected void handleLogout() {
        router.goTo(AppFrame.SCREEN_LOGIN);
    }

    protected abstract String getHeaderTitle();
}