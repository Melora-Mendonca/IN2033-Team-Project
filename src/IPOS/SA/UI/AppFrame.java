package IPOS.SA.UI;

import IPOS.SA.ACC.UI.*;
import IPOS.SA.CAT.UI.Catalogue;
import IPOS.SA.ORD.UI.*;
import IPOS.SA.RPT.UI.*;

import javax.swing.*;
import java.awt.*;

public class AppFrame extends JFrame {

    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    private final ScreenRouter router;

    public static final String SCREEN_LOGIN = "login";
    public static final String SCREEN_ADMIN_DASHBOARD = "adminDashboard";
    public static final String SCREEN_MANAGER_DASHBOARD = "managerDashboard";
    public static final String SCREEN_STAFF_DASHBOARD = "staffDashboard";
    public static final String SCREEN_CATALOGUE = "catalogue";
    public static final String SCREEN_MERCHANT_LIST = "merchantList";
    public static final String SCREEN_ACCOUNT_MANAGEMENT_CREATE = "accountManagementCreate";
    public static final String SCREEN_ACCOUNT_MANAGEMENT_MANAGE = "accountManagementManage";
    public static final String SCREEN_STAFF_LIST = "staffList";
    public static final String SCREEN_STAFF_ACCOUNT_CREATE = "staffAccountCreate";
    public static final String SCREEN_STAFF_ACCOUNT_MANAGE = "staffAccountManage";
    public static final String SCREEN_ORDER_MANAGEMENT = "orderManagement";
    public static final String SCREEN_ORDER_PROCESSING = "orderProcessing";
    public static final String SCREEN_ORDER_TRACKING = "orderTracking";
    public static final String SCREEN_INVOICE_LIST = "invoiceList";
    public static final String SCREEN_PAYMENT_RECORDING = "paymentRecording";
    public static final String SCREEN_REPORT = "report";
    public static final String SCREEN_COMMERCIAL_APP = "commercialApp";
    public static final String SCREEN_SETTINGS = "settings";
    public static final String SCREEN_MERCHANT_ORDERS = "merchantOrders";
    public static final String SCREEN_MERCHANT_INVOICES = "merchantInvoices";

    private String fullname;
    private String role;
    private String username;

    // Singleton instance
    private static AppFrame instance;

    public static AppFrame getInstance() {
        if (instance == null) {
            instance = new AppFrame();
        }
        return instance;
    }

    private AppFrame() {
        super("IPOS-SA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);

        router = new ScreenRouter(cards, root);
        setContentPane(root);

        // Start with login screen
        root.add(new LoginForm(router), SCREEN_LOGIN);
        router.goTo(SCREEN_LOGIN);

        setVisible(true);
        toFront();
        repaint();
        revalidate();
        SwingUtilities.invokeLater(() -> {
            System.out.println("AppFrame size: " + getSize());
            System.out.println("AppFrame visible: " + isVisible());
            System.out.println("Root components: " + root.getComponentCount());
        });
    }

    public void loadUserScreens(String fullname, String role, String username) {
        this.fullname = fullname;
        this.role = role;
        this.username = username;

        // Remove old screens except login
        // Add all screens for this user
        root.add(new AdminDashboard(fullname, role, username, router), SCREEN_ADMIN_DASHBOARD);
        root.add(new ManagerDashboard(fullname, role, username, router), SCREEN_MANAGER_DASHBOARD);
        root.add(new StaffDashboard(fullname, role, username, router), SCREEN_STAFF_DASHBOARD);
        root.add(new Catalogue(fullname, role, router), SCREEN_CATALOGUE);
        root.add(new MerchantList(fullname, role, router), SCREEN_MERCHANT_LIST);
        root.add(new MerchantList(fullname, role, "ORDERS", router), SCREEN_MERCHANT_ORDERS);
        root.add(new MerchantList(fullname, role, "INVOICES", router), SCREEN_MERCHANT_INVOICES);
        root.add(new AccountManagement(fullname, role, "CREATE", router), SCREEN_ACCOUNT_MANAGEMENT_CREATE);
        root.add(new AccountManagement(fullname, role, "MANAGE", router), SCREEN_ACCOUNT_MANAGEMENT_MANAGE);
        root.add(new StaffList(fullname, role, router), SCREEN_STAFF_LIST);
        root.add(new StaffAccountManagement(fullname, role, "CREATE", router), SCREEN_STAFF_ACCOUNT_CREATE);
        root.add(new StaffAccountManagement(fullname, role, "MANAGE", router), SCREEN_STAFF_ACCOUNT_MANAGE);
        root.add(new OrderManagement(fullname, role, router), SCREEN_ORDER_MANAGEMENT);
        root.add(new OrderProcessingFrame(fullname, role, router), SCREEN_ORDER_PROCESSING);
        root.add(new OrderTrackingFrame(fullname, role, router), SCREEN_ORDER_TRACKING);
        root.add(new InvoiceListFrame(fullname, role, router), SCREEN_INVOICE_LIST);
        root.add(new PaymentRecording(fullname, role, router), SCREEN_PAYMENT_RECORDING);
        root.add(new ReportForm(fullname, role, router), SCREEN_REPORT);
        root.add(new CommercialAppForm(fullname, role, router), SCREEN_COMMERCIAL_APP);
        root.add(new SettingsForm(fullname, role, username, router), SCREEN_SETTINGS);
    }

    public ScreenRouter getRouter() {
        return router;
    }
}