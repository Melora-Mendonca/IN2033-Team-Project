package IPOS.SA;

import IPOS.SA.ACC.LoginForm;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginForm::new);
    }
}
