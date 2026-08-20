import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.LocalDate;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
/**
* AdminFrame.java
*
* The screen shown only to the fixed application
* administrator
* ("kathir"). Gives full control over the system through four
* tabs:
*
* 1. Admin Dashboard - live totals pulled from MySQL
* 2. User Management - view every user, delete normal
* users
* ("kathir" itself can never be
* deleted)
* 3. Lost Items - view/add/delete/update status of
* every
* LOST item in the system
* 4. Found Items - view/add/delete/update status of
* every
* FOUND item in the system
*
* Like every other screen, this class never touches java.sql
* directly — everything goes through the shared AssetManager.
*/
public class AdminFrame extends JFrame {
private static final String[] CATEGORIES =
{"Electronics", "Documents", "Bags & Wallets",
"Accessories", "Clothing", "Keys", "Others"};
private static final String[] STATUS_OPTIONS = {"ACTIVE",
"CLAIMED", "CLOSED"};
private final User currentUser;
private final AssetManager assetManager = new
AssetManager();
// Admin Dashboard tab
private JLabel totalUsersValueLabel;
private JLabel totalItemsValueLabel;
private JLabel totalLostValueLabel;

private JLabel totalFoundValueLabel;
// User Management tab
private DefaultTableModel usersTableModel;
private JTable usersTable;
private List<User> usersList = new ArrayList<>();
// Lost Items / Found Items tabs (built after assetManager exists)
private final ItemManagementTab lostItemsTab = new
ItemManagementTab("LOST");
private final ItemManagementTab foundItemsTab = new
ItemManagementTab("FOUND");
public AdminFrame(User user) {
this.currentUser = user;
setTitle("Smart Lost & Found Asset Tracking System -Admin Panel");
setSize(1020, 660);
setLocationRelativeTo(null);
setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
setLayout(new BorderLayout());
add(buildTopBar(), BorderLayout.NORTH);
JTabbedPane tabbedPane = new JTabbedPane();
tabbedPane.addTab("Admin Dashboard",
buildDashboardTab());
tabbedPane.addTab("User Management",
buildUserManagementTab());
tabbedPane.addTab("Lost Items", lostItemsTab.panel);
tabbedPane.addTab("Found Items", foundItemsTab.panel);
add(tabbedPane, BorderLayout.CENTER);
refreshDashboardStats();
refreshUsersTable();
lostItemsTab.refresh();
foundItemsTab.refresh();
}
private JPanel buildTopBar() {
JPanel topBar = new JPanel(new BorderLayout());
topBar.setBorder(BorderFactory.createEmptyBorder(8,

12, 8, 12));
JLabel titleLabel = new JLabel(
"Administrator: " + currentUser.getName() + "(" + currentUser.getUsername() + ")");
titleLabel.setFont(new Font("SansSerif", Font.BOLD,
14));
JButton logoutButton = new JButton("Logout");
logoutButton.addActionListener(e -> logout());
JPanel logoutPanel = new JPanel(new
FlowLayout(FlowLayout.RIGHT));
logoutPanel.add(logoutButton);
topBar.add(titleLabel, BorderLayout.WEST);
topBar.add(logoutPanel, BorderLayout.EAST);
return topBar;
}
private void logout() {
new LoginFrame().setVisible(true);
dispose();
}
// -------------------------------------------------------
// ADMIN DASHBOARD TAB
// -------------------------------------------------------
private JPanel buildDashboardTab() {
JPanel panel = new JPanel(new BorderLayout());
panel.setBorder(BorderFactory.createEmptyBorder(30,
30, 30, 30));
JPanel statsGrid = new JPanel(new GridLayout(2, 2, 20,
20));
totalUsersValueLabel = new JLabel("0",
SwingConstants.CENTER);
totalItemsValueLabel = new JLabel("0",
SwingConstants.CENTER);
totalLostValueLabel = new JLabel("0",
SwingConstants.CENTER);

totalFoundValueLabel = new JLabel("0",
SwingConstants.CENTER);
statsGrid.add(buildStatCard("Total Users",
totalUsersValueLabel));
statsGrid.add(buildStatCard("Total Items",
totalItemsValueLabel));
statsGrid.add(buildStatCard("Total Lost Items",
totalLostValueLabel));
statsGrid.add(buildStatCard("Total Found Items",
totalFoundValueLabel));
JButton refreshButton = new JButton("RefreshDashboard");
refreshButton.addActionListener(e ->
refreshDashboardStats());
JPanel refreshPanel = new JPanel(new
FlowLayout(FlowLayout.CENTER));
refreshPanel.add(refreshButton);
panel.add(statsGrid, BorderLayout.CENTER);
panel.add(refreshPanel, BorderLayout.SOUTH);
return panel;
}
private JPanel buildStatCard(String title, JLabel
valueLabel) {
JPanel card = new JPanel(new BorderLayout());
card.setBorder(BorderFactory.createLineBorder(new
Color(190, 190, 190), 1, true));
card.setPreferredSize(new Dimension(200, 120));
JLabel titleLabel = new JLabel(title,
SwingConstants.CENTER);
titleLabel.setFont(new Font("SansSerif", Font.PLAIN,
14));
titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0,
0));
valueLabel.setFont(new Font("SansSerif", Font.BOLD,
34));

card.add(titleLabel, BorderLayout.NORTH);
card.add(valueLabel, BorderLayout.CENTER);
return card;
}
private void refreshDashboardStats() {
AssetManager.Stats stats =
assetManager.getDashboardStats();
totalUsersValueLabel.setText(String.valueOf(stats.totalUsers))
;
totalItemsValueLabel.setText(String.valueOf(stats.totalItems))
;
totalLostValueLabel.setText(String.valueOf(stats.totalLostItems));
totalFoundValueLabel.setText(String.valueOf(stats.totalFoundItems));
}
// -------------------------------------------------------
// USER MANAGEMENT TAB
// -------------------------------------------------------
private JPanel buildUserManagementTab() {
JPanel panel = new JPanel(new BorderLayout(8, 8));
panel.setBorder(BorderFactory.createEmptyBorder(10,
10, 10, 10));
String[] columns = {"Username", "Name", "Phone",
"Role"};
usersTableModel = new DefaultTableModel(columns, 0) {
@Override
public boolean isCellEditable(int row, int column)
{
return false;
}
};
usersTable = new JTable(usersTableModel);
usersTable.setRowHeight(22);

JScrollPane tableScroll = new JScrollPane(usersTable);
JPanel buttonRow = new JPanel(new
FlowLayout(FlowLayout.LEFT));
JButton refreshButton = new JButton("Refresh");
JButton deleteButton = new JButton("Delete SelectedUser");
buttonRow.add(refreshButton);
buttonRow.add(deleteButton);
panel.add(tableScroll, BorderLayout.CENTER);
panel.add(buttonRow, BorderLayout.SOUTH);
refreshButton.addActionListener(e ->
refreshUsersTable());
deleteButton.addActionListener(e ->
deleteSelectedUser());
return panel;
}
private void refreshUsersTable() {
usersList = assetManager.getAllUsers();
usersTableModel.setRowCount(0);
for (User u : usersList) {
usersTableModel.addRow(new Object[]
{u.getUsername(), u.getName(), u.getPhone(), u.getRole()});
}
}
private void deleteSelectedUser() {
int row = usersTable.getSelectedRow();
if (row < 0) {
JOptionPane.showMessageDialog(this, "Please selecta user first.",
"No Selection",
JOptionPane.WARNING_MESSAGE);
return;
}
User user = usersList.get(row);
if ("kathir".equalsIgnoreCase(user.getUsername())) {
JOptionPane.showMessageDialog(this,

"The administrator account \"kathir\"cannot be deleted.",
"Action Not Allowed",
JOptionPane.WARNING_MESSAGE);
return;
}
int confirm = JOptionPane.showConfirmDialog(this,
"Delete user \"" + user.getUsername() + "\"?This also removes their " +
"reported items and cannot beundone.",
"Confirm Delete", JOptionPane.YES_NO_OPTION,
JOptionPane.WARNING_MESSAGE);
if (confirm == JOptionPane.YES_OPTION) {
boolean success =
assetManager.deleteUser(user.getUsername());
if (success) {
refreshUsersTable();
lostItemsTab.refresh();
foundItemsTab.refresh();
refreshDashboardStats();
} else {
JOptionPane.showMessageDialog(this, "Could notdelete this user.",
"Error", JOptionPane.ERROR_MESSAGE);
}
}
}
// -------------------------------------------------------
// SHARED TABLE + IMAGE HELPERS (used by both Lost and Found tabs)
// -------------------------------------------------------
private DefaultTableModel createItemTableModel() {
String[] columns = {"ID", "Type", "Name", "Category",
"Description", "Colour",
"Location", "Date", "Reporter", "Contact",
"Status", "Image"};
return new DefaultTableModel(columns, 0) {

@Override
public boolean isCellEditable(int row, int column)
{
return false;
}
};
}
private void fillItemTable(DefaultTableModel model,
List<Item> items) {
model.setRowCount(0);
for (Item item : items) {
model.addRow(new Object[]{
item.getId(),
item.getType(),
item.getName(),
item.getCategory(),
item.getDescription(),
item.getColour(),
item.getLocation(),
item.getDate() != null ?
item.getDate().toString() : "",
item.getReporterUsername(),
item.getContact(),
item.getStatus(),
item.hasImage() ? "View Image" : "NoImage"
});
}
}
private void viewSelectedImage(JTable table, List<Item>
items) {
int row = table.getSelectedRow();
if (row < 0) {
JOptionPane.showMessageDialog(this, "Please selectan item first.",
"No Selection",
JOptionPane.WARNING_MESSAGE);
return;
}
showImagePreview(items.get(row).getImagePath());
}

private void showImagePreview(String imagePath) {
if (imagePath == null || imagePath.isBlank()) {
JOptionPane.showMessageDialog(this, "No imageavailable for this item.",
"No Image",
JOptionPane.INFORMATION_MESSAGE);
return;
}
File imageFile = new File(imagePath);
if (!imageFile.exists()) {
JOptionPane.showMessageDialog(this,
"Image file not found on this computer:\n"
+ imagePath,
"Image Missing",
JOptionPane.WARNING_MESSAGE);
return;
}
ImageIcon icon = new ImageIcon(imagePath);
Image scaledImage =
icon.getImage().getScaledInstance(320, 240,
Image.SCALE_SMOOTH);
JOptionPane.showMessageDialog(this, new JLabel(new
ImageIcon(scaledImage)),
"Image Preview", JOptionPane.PLAIN_MESSAGE);
}
private String capitalize(String text) {
return text.substring(0, 1).toUpperCase() +
text.substring(1).toLowerCase();
}
// -------------------------------------------------------
// Lost / Found item management tab (one instance per type, so
// the admin panel does not need two near-identical copies of
// every table/button/dialog).
// -------------------------------------------------------
private class ItemManagementTab {

final String type;
final JPanel panel = new JPanel(new BorderLayout(8,
8));
final DefaultTableModel tableModel;
final JTable table;
List<Item> itemsList = new ArrayList<>();
ItemManagementTab(String type) {
this.type = type;
panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10,
10));
tableModel = createItemTableModel();
table = new JTable(tableModel);
table.setRowHeight(22);
JScrollPane tableScroll = new JScrollPane(table);
JPanel buttonRow = new JPanel(new
FlowLayout(FlowLayout.LEFT));
JButton refreshButton = new JButton("Refresh");
JButton addButton = new JButton("Add " +
capitalize(type) + " Item");
JButton updateStatusButton = new JButton("UpdateStatus");
JButton viewImageButton = new JButton("ViewImage");
JButton deleteButton = new JButton("Delete");
buttonRow.add(refreshButton);
buttonRow.add(addButton);
buttonRow.add(updateStatusButton);
buttonRow.add(viewImageButton);
buttonRow.add(deleteButton);
panel.add(tableScroll, BorderLayout.CENTER);
panel.add(buttonRow, BorderLayout.SOUTH);
refreshButton.addActionListener(e -> refresh());
addButton.addActionListener(e ->
showAddItemDialog());
updateStatusButton.addActionListener(e ->
updateSelectedStatus());
viewImageButton.addActionListener(e ->

viewSelectedImage(table, itemsList));
deleteButton.addActionListener(e ->
deleteSelected());
table.addMouseListener(new MouseAdapter() {
@Override
public void mouseClicked(MouseEvent e) {
if (e.getClickCount() == 2) {
viewSelectedImage(table, itemsList);
}
}
});
}
void refresh() {
List<Item> allItems = assetManager.getAllItems();
itemsList = new ArrayList<>();
for (Item item : allItems) {
if (item.getType().equalsIgnoreCase(type)) {
itemsList.add(item);
}
}
fillItemTable(tableModel, itemsList);
}
void updateSelectedStatus() {
int row = table.getSelectedRow();
if (row < 0) {
JOptionPane.showMessageDialog(AdminFrame.this,
"Please select an item first.",
"No Selection",
JOptionPane.WARNING_MESSAGE);
return;
}
Item item = itemsList.get(row);
Object choice =
JOptionPane.showInputDialog(AdminFrame.this,
"Update status for \"" + item.getName() +
"\":",
"Update Status",
JOptionPane.QUESTION_MESSAGE, null,
STATUS_OPTIONS, item.getStatus());

if (choice == null) {
return;
}
boolean success =
assetManager.updateItemStatus(item.getId(),
choice.toString());
if (success) {
refresh();
} else {
JOptionPane.showMessageDialog(AdminFrame.this,
"Could not update the status.",
"Error", JOptionPane.ERROR_MESSAGE);
}
}
void deleteSelected() {
int row = table.getSelectedRow();
if (row < 0) {
JOptionPane.showMessageDialog(AdminFrame.this,
"Please select an item first.",
"No Selection",
JOptionPane.WARNING_MESSAGE);
return;
}
Item item = itemsList.get(row);
int confirm =
JOptionPane.showConfirmDialog(AdminFrame.this,
"Delete \"" + item.getName() + "\"? Thiscannot be undone.",
"Confirm Delete",
JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
if (confirm == JOptionPane.YES_OPTION) {
boolean success =
assetManager.deleteItem(item.getId());
if (success) {
refresh();
refreshDashboardStats();
} else {
JOptionPane.showMessageDialog(AdminFrame.this, "Could notdelete this item.",

"Error",
JOptionPane.ERROR_MESSAGE);
}
}
}
/**
* Lets the admin log a report on behalf of someone
* (for
* example, a walk-in visitor at a front desk). The
* reporter
* must be an existing registered username — since the
* "items" table enforces a foreign key on
* reporter_username
* — so this dialog presents a dropdown of real
* usernames
* instead of a free-text field.
*
* Image attachment is intentionally left out of this
* quick
* admin dialog to keep it simple; the reporting user
* can
* always attach an image themselves from their own
* Dashboard.
*/
void showAddItemDialog() {
JTextField nameField = new JTextField(18);
JComboBox<String> categoryCombo = new JComboBox<>
(CATEGORIES);
JTextArea descriptionArea = new JTextArea(3, 18);
descriptionArea.setLineWrap(true);
descriptionArea.setWrapStyleWord(true);
JTextField colourField = new JTextField(18);
JTextField locationField = new JTextField(18);
JTextField dateField = new JTextField(18);
dateField.setText(LocalDate.now().toString());
JTextField contactField = new JTextField(18);
List<User> allUsers = assetManager.getAllUsers();
String[] usernames = new String[allUsers.size()];
int adminIndex = 0;
for (int i = 0; i < allUsers.size(); i++) {
usernames[i] = allUsers.get(i).getUsername();
if

(usernames[i].equalsIgnoreCase(currentUser.getUsername())) {
adminIndex = i;
}
}
JComboBox<String> reporterCombo = new JComboBox<>
(usernames);
if (usernames.length > 0) {
reporterCombo.setSelectedIndex(adminIndex);
}
JPanel formPanel = new JPanel(new
GridBagLayout());
GridBagConstraints gbc = new GridBagConstraints();
gbc.insets = new Insets(5, 5, 5, 5);
gbc.fill = GridBagConstraints.HORIZONTAL;
JScrollPane descScroll = new
JScrollPane(descriptionArea);
String[] labels = {"Item Name:", "Category:",
"Description:", "Colour:",
"Location:", "Date (YYYY-MM-DD):",
"Contact Info:", "Reporter Username:"};
JComponent[] fields = {nameField, categoryCombo,
descScroll, colourField,
locationField, dateField, contactField,
reporterCombo};
for (int i = 0; i < labels.length; i++) {
gbc.gridx = 0;
gbc.gridy = i;
gbc.anchor = GridBagConstraints.LINE_END;
formPanel.add(new JLabel(labels[i]), gbc);
gbc.gridx = 1;
gbc.anchor = GridBagConstraints.LINE_START;
formPanel.add(fields[i], gbc);
}
int result =
JOptionPane.showConfirmDialog(AdminFrame.this, formPanel,
"Add " + capitalize(type) + " Item",
JOptionPane.OK_CANCEL_OPTION,
JOptionPane.PLAIN_MESSAGE);
if (result != JOptionPane.OK_OPTION) {

return;
}
String name = nameField.getText().trim();
String category = (String)
categoryCombo.getSelectedItem();
String description =
descriptionArea.getText().trim();
String colour = colourField.getText().trim();
String location = locationField.getText().trim();
String dateText = dateField.getText().trim();
String contact = contactField.getText().trim();
String reporterUsername = (String)
reporterCombo.getSelectedItem();
if (name.isEmpty() || colour.isEmpty() ||
location.isEmpty()
|| dateText.isEmpty() || contact.isEmpty()
|| reporterUsername == null) {
JOptionPane.showMessageDialog(AdminFrame.this,
"Please fill in all required fields.",
"Missing Information",
JOptionPane.WARNING_MESSAGE);
return;
}
LocalDate date;
try {
date = LocalDate.parse(dateText);
} catch (DateTimeParseException ex) {
JOptionPane.showMessageDialog(AdminFrame.this,
"Please enter the date in YYYY-MM-DDformat.",
"Invalid Date",
JOptionPane.WARNING_MESSAGE);
return;
}
Item item = ItemFactory.createItem(type, name,
category, description, colour,
location, date, reporterUsername, contact,
"ACTIVE", null);
boolean success = assetManager.addItem(item);

if (success) {
refresh();
refreshDashboardStats();
JOptionPane.showMessageDialog(AdminFrame.this,
"Item added successfully.",
"Success",
JOptionPane.INFORMATION_MESSAGE);
} else {
JOptionPane.showMessageDialog(AdminFrame.this,
"Could not add the item.",
"Error", JOptionPane.ERROR_MESSAGE);
}
}
}
}
