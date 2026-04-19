package IPOS.SA.UI;

import javax.swing.*;
import java.awt.*;
/**
 * Handles navigation between screens in IPOS-SA using a CardLayout.
 * Each screen is registered as a card with a unique name constant
 * defined in AppFrame. Calling goTo() switches the visible card
 * and notifies the new screen via the Refreshable interface so it
 * can reload its data before being displayed.
 */
public class ScreenRouter {
    /** The CardLayout used to switch between screens. */
    private final CardLayout cards;
    /** The root panel containing all screen cards. */
    private final JPanel root;
    /**
     * Constructor — creates a router with the given CardLayout and root panel.
     *
     * @param cards the CardLayout managing screen transitions
     * @param root  the root panel containing all registered screen cards
     */
    public ScreenRouter(CardLayout cards, JPanel root) {
        this.cards = cards;
        this.root = root;
    }
    /**
     * Navigates to the screen registered under the given name.
     * After switching the visible card, iterates through all components
     * in the root panel to find the newly visible one and calls
     * onShow() if it implements Refreshable, ensuring the screen
     * reloads its data before being displayed to the user.
     *
     * @param screen the name of the screen to navigate to,
     *               as defined by the SCREEN_ constants in AppFrame
     */
    public void goTo(String screen) {
        cards.show(root, screen);
        // Notify the panel it's being shown
        for (Component comp : root.getComponents()) {
            if (comp.isVisible() && comp instanceof Refreshable) {
                ((Refreshable) comp).onShow();
            }
        }
    }
}