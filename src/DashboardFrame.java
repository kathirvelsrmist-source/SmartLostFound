import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
/**
* DashboardFrame.java
*
* The main screen shown to a normal (non-admin) user after
* logging
* in. Organised as four tabs:
*
* 1. Report Lost Item - form to report something the user
* lost
* 2. Report Found Item - form to report something the user
* found
* 3. Search & Browse - GLOBAL view of every Lost/Found
* item in
* the system, with keyword search
* and filters
* 4. My Reports - only the items THIS user has
* reported,
* with the ability to update status
* or delete
*
* Every read/write to the database goes through the shared
* AssetManager instance — this class only builds and reacts
* to the
* Swing UI.
*/
public class DashboardFrame extends JFrame {
private static final String[] CATEGORIES =
{"Electronics", "Documents", "Bags & Wallets",
"Accessories", "Clothing", "Keys", "Others"};
private static final String[] FILTER_CATEGORIES =
{"All", "Electronics", "Documents", "Bags &Wallets", "Accessories", "Clothing", "Keys", "Others"};
private static final String[] TYPE_FILTER_OPTIONS =
{"All", "LOST", "FOUND"};

private static final String[] STATUS_OPTIONS = {"ACTIVE",
"CLAIMED", "CLOSED"};
private static final String[] FILTER_STATUS_OPTIONS =
{"All", "ACTIVE", "CLAIMED", "CLOSED"};
private final User currentUser;
private final AssetManager assetManager = new
AssetManager();
private final ReportFormPanel lostForm = new
ReportFormPanel("Submit Lost Report");
private final ReportFormPanel foundForm = new
ReportFormPanel("Submit Found Report");
// Search & Browse tab
private JTextField searchField;
private JComboBox<String> typeFilterCombo;
private JComboBox<String> categoryFilterCombo;
private JComboBox<String> statusFilterCombo;
private JTextField filterLocationField;
private DefaultTableModel browseTableModel;
private JTable browseTable;
private List<Item> browseItemsList = new ArrayList<>();
// My Reports tab
private DefaultTableModel myReportsTableModel;
private JTable myReportsTable;
private List<Item> myReportsItemsList = new ArrayList<>();
public DashboardFrame(User user) {
this.currentUser = user;
setTitle("Smart Lost & Found Asset Tracking System -Dashboard");
setSize(980, 640);
setLocationRelativeTo(null);
setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
setLayout(new BorderLayout());
add(buildTopBar(), BorderLayout.NORTH);
JTabbedPane tabbedPane = new JTabbedPane();

tabbedPane.addTab("Report Lost Item", lostForm.panel);
tabbedPane.addTab("Report Found Item",
foundForm.panel);
tabbedPane.addTab("Search & Browse",
buildBrowsePanel());
tabbedPane.addTab("My Reports",
buildMyReportsPanel());
add(tabbedPane, BorderLayout.CENTER);
lostForm.submitButton.addActionListener(e ->
submitReport(lostForm, "LOST"));
foundForm.submitButton.addActionListener(e ->
submitReport(foundForm, "FOUND"));
refreshBrowseTable();
refreshMyReportsTable();
}
private JPanel buildTopBar() {
JPanel topBar = new JPanel(new BorderLayout());
topBar.setBorder(BorderFactory.createEmptyBorder(8,
12, 8, 12));
JLabel welcomeLabel = new JLabel(
"Welcome, " + currentUser.getName() + " (" +
currentUser.getUsername() + ")");
welcomeLabel.setFont(new Font("SansSerif", Font.BOLD,
14));
JButton logoutButton = new JButton("Logout");
logoutButton.addActionListener(e -> logout());
JPanel logoutPanel = new JPanel(new
FlowLayout(FlowLayout.RIGHT));
logoutPanel.add(logoutButton);
topBar.add(welcomeLabel, BorderLayout.WEST);
topBar.add(logoutPanel, BorderLayout.EAST);
return topBar;
}
private void logout() {
new LoginFrame().setVisible(true);
dispose();
}

// -------------------------------------------------------
// REPORT LOST / REPORT FOUND (shared submit logic)
// -------------------------------------------------------
private void submitReport(ReportFormPanel form, String
type) {
String name = form.nameField.getText().trim();
String category = (String)
form.categoryCombo.getSelectedItem();
String description =
form.descriptionArea.getText().trim();
String colour = form.colourField.getText().trim();
String location = form.locationField.getText().trim();
String dateText = form.dateField.getText().trim();
String contact = form.contactField.getText().trim();
if (name.isEmpty() || colour.isEmpty() ||
location.isEmpty()
|| dateText.isEmpty() || contact.isEmpty()) {
JOptionPane.showMessageDialog(this,
"Please fill in name, colour, location,date and contact info.",
"Missing Information",
JOptionPane.WARNING_MESSAGE);
return;
}
LocalDate date;
try {
date = LocalDate.parse(dateText);
} catch (DateTimeParseException ex) {
JOptionPane.showMessageDialog(this,
"Please enter the date in YYYY-MM-DDformat (example: 2026-08-13).",
"Invalid Date",
JOptionPane.WARNING_MESSAGE);
return;
}
String imagePath = null;
if (form.selectedImageFile != null) {

try {
imagePath =
copyImageToAppFolder(form.selectedImageFile);
} catch (IOException ex) {
JOptionPane.showMessageDialog(this,
"Could not save the selected image: "
+ ex.getMessage(),
"Image Error",
JOptionPane.ERROR_MESSAGE);
return;
}
}
Item item = ItemFactory.createItem(type, name,
category, description, colour,
location, date, currentUser.getUsername(),
contact, "ACTIVE", imagePath);
boolean success = assetManager.addItem(item);
if (!success) {
JOptionPane.showMessageDialog(this,
"Could not save your report. Please tryagain.",
"Error", JOptionPane.ERROR_MESSAGE);
return;
}
form.clearFields();
refreshBrowseTable();
refreshMyReportsTable();
announceSubmissionResult(item);
}
private void announceSubmissionResult(Item item) {
List<Item> matches =
assetManager.checkPossibleMatches(item);
if (matches.isEmpty()) {
JOptionPane.showMessageDialog(this,
"Your report has been submittedsuccessfully.",
"Report Submitted",
JOptionPane.INFORMATION_MESSAGE);

return;
}
StringBuilder message = new StringBuilder();
message.append("Your report was submittedsuccessfully.\n\n");
message.append("Good news - ").append(matches.size())
.append(matches.size() == 1 ? " possible matchwas" : " possible matches were")
.append(" found:\n\n");
for (Item match : matches) {
message.append("- ").append(match.getName())
.append("(").append(match.getType()).append(") at ")
.append(match.getLocation())
.append(" - reported by").append(match.getReporterUsername())
.append(", contact:").append(match.getContact())
.append("\n");
}
JOptionPane.showMessageDialog(this,
message.toString(),
"Possible Match Found",
JOptionPane.INFORMATION_MESSAGE);
}
/**
* Copies a user-chosen image file into a local "images"
* folder
* next to the running application (created automatically
* the
* first time it's needed) and returns the relative path
* that
* gets stored in the "imagePath" field / image_path
* column.
*
* A local absolute path (e.g.
* "C:\Users\Name\Pictures\bag.jpg")
* would only make sense on the computer that selected it.
* Copying
* the file into the project's own "images" folder means

* every
* user of the app running on the same machine can view
* it, and
* the stored path stays short and portable within the
* project.
*/
private String copyImageToAppFolder(File sourceFile)
throws IOException {
File imagesDir = new File("images");
if (!imagesDir.exists()) {
imagesDir.mkdirs();
}
String originalName = sourceFile.getName();
String extension = "";
int dotIndex = originalName.lastIndexOf('.');
if (dotIndex >= 0) {
extension = originalName.substring(dotIndex);
}
String uniqueName = "img_" +
System.currentTimeMillis() + extension;
File destination = new File(imagesDir, uniqueName);
Files.copy(sourceFile.toPath(), destination.toPath(),
StandardCopyOption.REPLACE_EXISTING);
return "images" + File.separator + uniqueName;
}
// -------------------------------------------------------
// SEARCH & BROWSE TAB (global — every user's items)
// -------------------------------------------------------
private JPanel buildBrowsePanel() {
JPanel panel = new JPanel(new BorderLayout(8, 8));
panel.setBorder(BorderFactory.createEmptyBorder(10,
10, 10, 10));
JPanel controls = new JPanel();
controls.setLayout(new BoxLayout(controls,
BoxLayout.Y_AXIS));

JPanel searchRow = new JPanel(new
FlowLayout(FlowLayout.LEFT, 6, 4));
searchField = new JTextField(20);
JButton searchButton = new JButton("Search");
JButton showAllButton = new JButton("Show All");
searchRow.add(new JLabel("Keyword:"));
searchRow.add(searchField);
searchRow.add(searchButton);
searchRow.add(showAllButton);
JPanel filterRow = new JPanel(new
FlowLayout(FlowLayout.LEFT, 6, 4));
typeFilterCombo = new JComboBox<>
(TYPE_FILTER_OPTIONS);
categoryFilterCombo = new JComboBox<>
(FILTER_CATEGORIES);
statusFilterCombo = new JComboBox<>
(FILTER_STATUS_OPTIONS);
filterLocationField = new JTextField(12);
JButton filterButton = new JButton("Apply Filters");
filterRow.add(new JLabel("Type:"));
filterRow.add(typeFilterCombo);
filterRow.add(new JLabel("Category:"));
filterRow.add(categoryFilterCombo);
filterRow.add(new JLabel("Status:"));
filterRow.add(statusFilterCombo);
filterRow.add(new JLabel("Location:"));
filterRow.add(filterLocationField);
filterRow.add(filterButton);
controls.add(searchRow);
controls.add(filterRow);
browseTableModel = createItemTableModel();
browseTable = new JTable(browseTableModel);
browseTable.setRowHeight(22);
JScrollPane tableScroll = new
JScrollPane(browseTable);
JPanel bottomRow = new JPanel(new
FlowLayout(FlowLayout.LEFT));
JButton viewImageButton = new JButton("View Image");
JLabel hintLabel = new JLabel(" (tip: double-click arow to view its image)");

bottomRow.add(viewImageButton);
bottomRow.add(hintLabel);
panel.add(controls, BorderLayout.NORTH);
panel.add(tableScroll, BorderLayout.CENTER);
panel.add(bottomRow, BorderLayout.SOUTH);
searchButton.addActionListener(e -> performSearch());
showAllButton.addActionListener(e -> {
searchField.setText("");
refreshBrowseTable();
});
filterButton.addActionListener(e -> performFilter());
viewImageButton.addActionListener(e ->
viewSelectedImage(browseTable, browseItemsList));
browseTable.addMouseListener(new MouseAdapter() {
@Override
public void mouseClicked(MouseEvent e) {
if (e.getClickCount() == 2) {
viewSelectedImage(browseTable,
browseItemsList);
}
}
});
return panel;
}
private void performSearch() {
String keyword = searchField.getText().trim();
if (keyword.isEmpty()) {
refreshBrowseTable();
return;
}
browseItemsList = assetManager.searchItems(keyword);
fillItemTable(browseTableModel, browseItemsList);
}
private void performFilter() {
String type = (String)
typeFilterCombo.getSelectedItem();
String category = (String)
categoryFilterCombo.getSelectedItem();
String status = (String)

statusFilterCombo.getSelectedItem();
String location =
filterLocationField.getText().trim();
browseItemsList = assetManager.filterItems(type,
category, status, location);
fillItemTable(browseTableModel, browseItemsList);
}
private void refreshBrowseTable() {
browseItemsList = assetManager.getAllItems();
fillItemTable(browseTableModel, browseItemsList);
}
// -------------------------------------------------------
// MY REPORTS TAB (only this user's items)
// -------------------------------------------------------
private JPanel buildMyReportsPanel() {
JPanel panel = new JPanel(new BorderLayout(8, 8));
panel.setBorder(BorderFactory.createEmptyBorder(10,
10, 10, 10));
myReportsTableModel = createItemTableModel();
myReportsTable = new JTable(myReportsTableModel);
myReportsTable.setRowHeight(22);
JScrollPane tableScroll = new
JScrollPane(myReportsTable);
JPanel buttonRow = new JPanel(new
FlowLayout(FlowLayout.LEFT));
JButton refreshButton = new JButton("Refresh");
JButton viewImageButton = new JButton("View Image");
JButton updateStatusButton = new JButton("UpdateStatus");
JButton deleteButton = new JButton("Delete Report");
buttonRow.add(refreshButton);
buttonRow.add(viewImageButton);
buttonRow.add(updateStatusButton);
buttonRow.add(deleteButton);
panel.add(new JLabel(" Items you have personally reported:"), BorderLayout.NORTH);
panel.add(tableScroll, BorderLayout.CENTER);
panel.add(buttonRow, BorderLayout.SOUTH);
refreshButton.addActionListener(e ->
refreshMyReportsTable());
viewImageButton.addActionListener(e ->
viewSelectedImage(myReportsTable, myReportsItemsList));
updateStatusButton.addActionListener(e ->
updateSelectedMyReportStatus());
deleteButton.addActionListener(e ->
deleteSelectedMyReport());
myReportsTable.addMouseListener(new MouseAdapter() {
@Override
public void mouseClicked(MouseEvent e) {
if (e.getClickCount() == 2) {
viewSelectedImage(myReportsTable,
myReportsItemsList);
}
}
});
return panel;
}
private void refreshMyReportsTable() {
myReportsItemsList =
assetManager.getUserItems(currentUser.getUsername());
fillItemTable(myReportsTableModel,
myReportsItemsList);
}
private void updateSelectedMyReportStatus() {
int row = myReportsTable.getSelectedRow();
if (row < 0) {
JOptionPane.showMessageDialog(this, "Please selectan item first.",
"No Selection",
JOptionPane.WARNING_MESSAGE);
return;
}
Item item = myReportsItemsList.get(row);
Object choice = JOptionPane.showInputDialog(this,

"Update status for \"" + item.getName() +
"\":",
"Update Status", JOptionPane.QUESTION_MESSAGE,
null,
STATUS_OPTIONS, item.getStatus());
if (choice == null) {
return;
}
boolean success =
assetManager.updateItemStatus(item.getId(),
choice.toString());
if (success) {
refreshMyReportsTable();
refreshBrowseTable();
} else {
JOptionPane.showMessageDialog(this, "Could notupdate the status.",
"Error", JOptionPane.ERROR_MESSAGE);
}
}
private void deleteSelectedMyReport() {
int row = myReportsTable.getSelectedRow();
if (row < 0) {
JOptionPane.showMessageDialog(this, "Please selectan item first.",
"No Selection",
JOptionPane.WARNING_MESSAGE);
return;
}
Item item = myReportsItemsList.get(row);
int confirm = JOptionPane.showConfirmDialog(this,
"Delete your report \"" + item.getName() +
"\"? This cannot be undone.",
"Confirm Delete", JOptionPane.YES_NO_OPTION,
JOptionPane.WARNING_MESSAGE);
if (confirm == JOptionPane.YES_OPTION) {
boolean success =
assetManager.deleteItem(item.getId());
if (success) {

refreshMyReportsTable();
refreshBrowseTable();
} else {
JOptionPane.showMessageDialog(this, "Could notdelete this report.",
"Error", JOptionPane.ERROR_MESSAGE);
}
}
}
// -------------------------------------------------------
// SHARED TABLE + IMAGE HELPERS
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
// -------------------------------------------------------
// Report form: groups the Swing fields for ONE report form
// (used once for "Report Lost Item" and once for "Report Found
// Item") so the two tabs do not need to duplicate ten
// separate field declarations each.
// -------------------------------------------------------
private static class ReportFormPanel {
final JPanel panel = new JPanel(new GridBagLayout());
final JTextField nameField = new JTextField(18);
final JComboBox<String> categoryCombo = new
JComboBox<>(CATEGORIES);
final JTextArea descriptionArea = new JTextArea(3,
18);
final JTextField colourField = new JTextField(18);
final JTextField locationField = new JTextField(18);
final JTextField dateField = new JTextField(18);
final JTextField contactField = new JTextField(18);
final JButton chooseImageButton = new JButton("ChooseImage...");
final JLabel imageStatusLabel = new JLabel("No ImageSelected");
final JButton submitButton;
File selectedImageFile;
ReportFormPanel(String submitLabel) {
submitButton = new JButton(submitLabel);
buildLayout();
wireImageChooser();
}
private void buildLayout() {

panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20,
30));
GridBagConstraints gbc = new GridBagConstraints();
gbc.insets = new Insets(6, 6, 6, 6);
gbc.fill = GridBagConstraints.HORIZONTAL;
descriptionArea.setLineWrap(true);
descriptionArea.setWrapStyleWord(true);
JScrollPane descScroll = new
JScrollPane(descriptionArea);
int row = 0;
addRow(gbc, row++, "Item Name:", nameField);
addRow(gbc, row++, "Category:", categoryCombo);
addRow(gbc, row++, "Description:", descScroll);
addRow(gbc, row++, "Colour:", colourField);
addRow(gbc, row++, "Location:", locationField);
addRow(gbc, row++, "Date (YYYY-MM-DD):",
dateField);
addRow(gbc, row++, "Contact Info:", contactField);
JPanel imagePanel = new JPanel(new
FlowLayout(FlowLayout.LEFT, 8, 0));
imagePanel.add(chooseImageButton);
imagePanel.add(imageStatusLabel);
addRow(gbc, row++, "Image:", imagePanel);
gbc.gridx = 0;
gbc.gridy = row;
gbc.gridwidth = 2;
gbc.insets = new Insets(16, 6, 6, 6);
panel.add(submitButton, gbc);
}
private void addRow(GridBagConstraints gbc, int row,
String label, JComponent field) {
gbc.gridx = 0;
gbc.gridy = row;
gbc.gridwidth = 1;
gbc.anchor = GridBagConstraints.LINE_END;
panel.add(new JLabel(label), gbc);
gbc.gridx = 1;
gbc.anchor = GridBagConstraints.LINE_START;
panel.add(field, gbc);

}
private void wireImageChooser() {
chooseImageButton.addActionListener(e -> {
JFileChooser chooser = new JFileChooser();
chooser.setDialogTitle("Select an Image");
chooser.setFileFilter(new
FileNameExtensionFilter(
"Image files", "jpg", "jpeg", "png",
"gif"));
int result = chooser.showOpenDialog(panel);
if (result == JFileChooser.APPROVE_OPTION) {
selectedImageFile =
chooser.getSelectedFile();
imageStatusLabel.setText(selectedImageFile.getName());
}
});
}
void clearFields() {
nameField.setText("");
categoryCombo.setSelectedIndex(0);
descriptionArea.setText("");
colourField.setText("");
locationField.setText("");
dateField.setText("");
contactField.setText("");
imageStatusLabel.setText("No Image Selected");
selectedImageFile = null;
}
}
}
