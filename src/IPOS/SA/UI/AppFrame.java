package IPOS.SA.UI;

import IPOS.SA.ACC.UI.*;
import IPOS.SA.CAT.UI.Catalogue;
import IPOS.SA.CAT.UI.ManageItem;
import IPOS.SA.ORD.UI.*;
import IPOS.SA.RPT.UI.*;

import javax.swing.*;
import java.awt.*;
/**
 * The main application frame for IPOS-SA.
 * Implemented as a singleton — only one instance exists throughout the application.
 *
 * Uses a CardLayout to manage all screens in a single JFrame window.
 * Navigation between screens is handled by the ScreenRouter.
 * All screens are registered as named cards using the SCREEN_ constants.
 *
 * On startup only the login screen is loaded. After successful authentication
 * all screens for the logged-in user are loaded via loadUserScreens().
 *
 * Also acts as a shared state holder for the currently selected merchant ID,
 * used to pass context between the merchant list and order/invoice screens.
 */
public class AppFrame extends JFrame {

    private final CardLayout cards = new CardLayout();
    /** CardLayout used to switch between registered screens. */
    private final JPanel root = new JPanel(cards);
    /** Root panel containing all screen cards. */
    private final ScreenRouter router;
    /** Router used by all screens to navigate to other screens. */
    private String selectedMerchantId;

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
    public static final String SCREEN_MANAGE_ITEM_ADD = "manageItemAdd";
    public static final String SCREEN_MANAGE_ITEM_EDIT = "manageItemEdit";
    public static final String SCREEN_MANAGE_ITEM_DELETE = "manageItemDelete";
    public static final String SCREEN_MANAGE_ITEM_DELIVERY = "manageItemDelivery";

    private String fullname;
    private String role;
    private String username;

    // Singleton instance
    private static AppFrame instance;
    /**
     * Returns the singleton instance of AppFrame.
     * Creates it if it does not yet exist.
     *
     * @return the single AppFrame instance
     */
    public static AppFrame getInstance() {
        if (instance == null) {
            instance = new AppFrame();
        }
        return instance;
    }
    /**
     * Private constructor — creates the main application window.
     * Sets up the CardLayout, creates the ScreenRouter and
     * loads the login screen as the initial view.
     */
    private AppFrame() {
        super("IPOS-SA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
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
    /**
     * Registers all application screens for the logged-in user.
     * Called by LoginForm after successful authentication.
     * All screens are added as named cards to the root panel
     * so the ScreenRouter can navigate between them.
     *
     * Multiple variants of some screens are registered for context-specific
     * navigation — for example MerchantList is registered three times
     * (default, orders context and invoices context).
     *
     * @param fullname the full name of the logged-in user
     * @param role     the role of the logged-in user
     * @param username the username of the logged-in user
     */
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
        root.add(new ManageItem(fullname, role, "ADD", router), SCREEN_MANAGE_ITEM_ADD);
        root.add(new ManageItem(fullname, role, "EDIT", router), SCREEN_MANAGE_ITEM_EDIT);
        root.add(new ManageItem(fullname, role, "DELETE", router), SCREEN_MANAGE_ITEM_DELETE);
        root.add(new ManageItem(fullname, role, "DELIVERY", router), SCREEN_MANAGE_ITEM_DELIVERY);

    }
    /**
     * Returns the ScreenRouter used for navigation between screens.
     *
     * @return the screen router
     */
    public ScreenRouter getRouter() {
        return router;
    }
    /**
     * Stores the ID of the merchant selected in MerchantList.
     * Read by OrderManagement and InvoiceListFrame to filter their data.
     *
     * @param merchantId the selected merchant ID
     */
    public void setSelectedMerchant(String merchantId) {
        this.selectedMerchantId = merchantId;
    }
    /**
     * Returns the ID of the currently selected merchant.
     * Used by OrderManagement and InvoiceListFrame to filter by merchant.
     *
     * @return the selected merchant ID, or null if none selected
     */
    public String getSelectedMerchant() {
        return selectedMerchantId;
    }
}