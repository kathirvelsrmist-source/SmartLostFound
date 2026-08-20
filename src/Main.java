import javax.swing.SwingUtilities;
/**
* Main.java
*
* Entry point of the Smart Lost & Found Asset Tracking
System.
*
* Startup sequence:
* 1. Make sure the "users" / "items" tables exist and the
fixed
* "kathir" administrator account is present
(DatabaseConnection).
* 2. Show LoginFrame on the Swing Event Dispatch Thread.
*/
public class Main {
public static void main(String[] args) {
DatabaseConnection.initializeDatabase();
SwingUtilities.invokeLater(() -> {
LoginFrame loginFrame = new LoginFrame();
loginFrame.setVisible(true);
});
}
}