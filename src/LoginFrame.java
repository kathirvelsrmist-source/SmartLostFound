import javax.swing.*;
import java.awt.*;
/**
* LoginFrame.java
*
* The first screen shown when the application starts. Uses a
* CardLayout to flip between a Login card and a Register card
* inside
* the SAME JFrame, instead of opening a second window.
*
* Responsibilities:
* - authenticate an existing user through
* AssetManager.login(...)
* - register a brand-new normal user through
* AssetManager.register(...)
* - decide, based on User.isAdmin(), whether to open
* AdminFrame or
* DashboardFrame next
*
* This class never talks to java.sql directly — all
* persistence goes
* through the shared AssetManager instance.
*/
public class LoginFrame extends JFrame {
private final AssetManager assetManager = new
AssetManager();
private final CardLayout cardLayout = new CardLayout();
private final JPanel cardPanel = new JPanel(cardLayout);
// Login card fields
private JTextField loginUsernameField;
private JPasswordField loginPasswordField;
// Register card fields
private JTextField regUsernameField;

private JPasswordField regPasswordField;
private JTextField regNameField;
private JTextField regPhoneField;
public LoginFrame() {
setTitle("Smart Lost & Found Asset Tracking System -Login");
setSize(440, 480);
setLocationRelativeTo(null);
setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
setResizable(false);
cardPanel.add(buildLoginPanel(), "LOGIN");
cardPanel.add(buildRegisterPanel(), "REGISTER");
add(cardPanel);
cardLayout.show(cardPanel, "LOGIN");
}
// -------------------------------------------------------
// LOGIN CARD
// -------------------------------------------------------
private JPanel buildLoginPanel() {
JPanel panel = new JPanel(new GridBagLayout());
panel.setBorder(BorderFactory.createEmptyBorder(30,
40, 30, 40));
GridBagConstraints gbc = new GridBagConstraints();
gbc.insets = new Insets(8, 4, 8, 4);
gbc.fill = GridBagConstraints.HORIZONTAL;
JLabel titleLabel = new JLabel("Smart Lost & Found");
titleLabel.setFont(new Font("SansSerif", Font.BOLD,
20));
JLabel subtitleLabel = new JLabel("Asset TrackingSystem");
subtitleLabel.setFont(new Font("SansSerif",
Font.PLAIN, 13));
loginUsernameField = new JTextField(18);
loginPasswordField = new JPasswordField(18);

JButton loginButton = new JButton("Login");
JButton goToRegisterButton = new JButton("New user?Register here");
goToRegisterButton.setBorderPainted(false);
goToRegisterButton.setContentAreaFilled(false);
goToRegisterButton.setForeground(new Color(30, 90,
200));
int row = 0;
gbc.gridx = 0;
gbc.gridy = row;
gbc.gridwidth = 2;
gbc.anchor = GridBagConstraints.CENTER;
panel.add(titleLabel, gbc);
row++;
gbc.gridy = row;
panel.add(subtitleLabel, gbc);
row++;
gbc.gridy = row;
panel.add(Box.createVerticalStrut(10), gbc);
gbc.gridwidth = 1;
gbc.anchor = GridBagConstraints.LINE_END;
row++;
gbc.gridx = 0;
gbc.gridy = row;
panel.add(new JLabel("Username:"), gbc);
gbc.gridx = 1;
gbc.anchor = GridBagConstraints.LINE_START;
panel.add(loginUsernameField, gbc);
row++;
gbc.gridx = 0;
gbc.gridy = row;
gbc.anchor = GridBagConstraints.LINE_END;
panel.add(new JLabel("Password:"), gbc);
gbc.gridx = 1;
gbc.anchor = GridBagConstraints.LINE_START;
panel.add(loginPasswordField, gbc);

row++;
gbc.gridx = 0;
gbc.gridy = row;
gbc.gridwidth = 2;
gbc.anchor = GridBagConstraints.CENTER;
gbc.insets = new Insets(18, 4, 4, 4);
panel.add(loginButton, gbc);
row++;
gbc.gridy = row;
gbc.insets = new Insets(4, 4, 4, 4);
panel.add(goToRegisterButton, gbc);
loginButton.addActionListener(e -> handleLogin());
loginPasswordField.addActionListener(e ->
handleLogin());
goToRegisterButton.addActionListener(e ->
cardLayout.show(cardPanel, "REGISTER"));
return panel;
}
private void handleLogin() {
String username = loginUsernameField.getText().trim();
String password = new
String(loginPasswordField.getPassword());
if (username.isEmpty() || password.isEmpty()) {
JOptionPane.showMessageDialog(this,
"Please enter both username andpassword.",
"Missing Information",
JOptionPane.WARNING_MESSAGE);
return;
}
User user = assetManager.login(username, password);
if (user == null) {
JOptionPane.showMessageDialog(this,
"Invalid username or password.",
"Login Failed",
JOptionPane.ERROR_MESSAGE);
return;

}
if (user.isAdmin()) {
new AdminFrame(user).setVisible(true);
} else {
new DashboardFrame(user).setVisible(true);
}
dispose();
}
// -------------------------------------------------------
// REGISTER CARD
// -------------------------------------------------------
private JPanel buildRegisterPanel() {
JPanel panel = new JPanel(new GridBagLayout());
panel.setBorder(BorderFactory.createEmptyBorder(30,
40, 30, 40));
GridBagConstraints gbc = new GridBagConstraints();
gbc.insets = new Insets(8, 4, 8, 4);
gbc.fill = GridBagConstraints.HORIZONTAL;
JLabel titleLabel = new JLabel("Create a NewAccount");
titleLabel.setFont(new Font("SansSerif", Font.BOLD,
18));
regUsernameField = new JTextField(18);
regPasswordField = new JPasswordField(18);
regNameField = new JTextField(18);
regPhoneField = new JTextField(18);
JButton registerButton = new JButton("Register");
JButton backToLoginButton = new JButton("Already havean account? Login");
backToLoginButton.setBorderPainted(false);
backToLoginButton.setContentAreaFilled(false);
backToLoginButton.setForeground(new Color(30, 90,
200));
int row = 0;

gbc.gridx = 0;
gbc.gridy = row;
gbc.gridwidth = 2;
gbc.anchor = GridBagConstraints.CENTER;
panel.add(titleLabel, gbc);
gbc.gridwidth = 1;
row++;
gbc.gridx = 0;
gbc.gridy = row;
gbc.anchor = GridBagConstraints.LINE_END;
panel.add(new JLabel("Username:"), gbc);
gbc.gridx = 1;
gbc.anchor = GridBagConstraints.LINE_START;
panel.add(regUsernameField, gbc);
row++;
gbc.gridx = 0;
gbc.gridy = row;
gbc.anchor = GridBagConstraints.LINE_END;
panel.add(new JLabel("Password:"), gbc);
gbc.gridx = 1;
gbc.anchor = GridBagConstraints.LINE_START;
panel.add(regPasswordField, gbc);
row++;
gbc.gridx = 0;
gbc.gridy = row;
gbc.anchor = GridBagConstraints.LINE_END;
panel.add(new JLabel("Full Name:"), gbc);
gbc.gridx = 1;
gbc.anchor = GridBagConstraints.LINE_START;
panel.add(regNameField, gbc);
row++;
gbc.gridx = 0;
gbc.gridy = row;
gbc.anchor = GridBagConstraints.LINE_END;
panel.add(new JLabel("Phone:"), gbc);
gbc.gridx = 1;
gbc.anchor = GridBagConstraints.LINE_START;
panel.add(regPhoneField, gbc);

row++;
gbc.gridx = 0;
gbc.gridy = row;
gbc.gridwidth = 2;
gbc.anchor = GridBagConstraints.CENTER;
gbc.insets = new Insets(18, 4, 4, 4);
panel.add(registerButton, gbc);
row++;
gbc.gridy = row;
gbc.insets = new Insets(4, 4, 4, 4);
panel.add(backToLoginButton, gbc);
registerButton.addActionListener(e ->
handleRegister());
backToLoginButton.addActionListener(e -> {
clearRegisterFields();
cardLayout.show(cardPanel, "LOGIN");
});
return panel;
}
private void handleRegister() {
String username = regUsernameField.getText().trim();
String password = new
String(regPasswordField.getPassword());
String name = regNameField.getText().trim();
String phone = regPhoneField.getText().trim();
if (username.isEmpty() || password.isEmpty() ||
name.isEmpty()) {
JOptionPane.showMessageDialog(this,
"Username, password and full name arerequired.",
"Missing Information",
JOptionPane.WARNING_MESSAGE);
return;
}
if ("kathir".equalsIgnoreCase(username)) {
JOptionPane.showMessageDialog(this,
"This username is reserved. Please chooseanother one.",

"Registration Failed",
JOptionPane.ERROR_MESSAGE);
return;
}
boolean success = assetManager.register(username,
password, name, phone);
if (success) {
JOptionPane.showMessageDialog(this,
"Registration successful! You can now login.",
"Welcome",
JOptionPane.INFORMATION_MESSAGE);
clearRegisterFields();
cardLayout.show(cardPanel, "LOGIN");
} else {
JOptionPane.showMessageDialog(this,
"That username is already taken. Pleasechoose another one.",
"Registration Failed",
JOptionPane.ERROR_MESSAGE);
}
}
private void clearRegisterFields() {
regUsernameField.setText("");
regPasswordField.setText("");
regNameField.setText("");
regPhoneField.setText("");
}
}

