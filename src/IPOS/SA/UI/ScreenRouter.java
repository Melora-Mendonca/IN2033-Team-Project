package IPOS.SA.UI;

import javax.swing.*;
import java.awt.*;

public class ScreenRouter {
    private final CardLayout cards;
    private final JPanel root;

    public ScreenRouter(CardLayout cards, JPanel root) {
        this.cards = cards;
        this.root = root;
    }

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