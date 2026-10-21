import java.time.LocalDate;
public abstract class Item {
protected int id;
protected String name;
protected String category;
protected String description;
protected String colour;
protected String location;
protected LocalDate date;
protected String reporterUsername;
protected String contact;
protected String status;
protected String imagePath;
protected Item(String name,
String category,
String description,
String colour,
String location,
LocalDate date,
String reporterUsername,
String contact,
String status,
String imagePath) {
this.name = name;
this.category = category;
this.description = description;
this.colour = colour;
this.location = location;
this.date = date;
this.reporterUsername = reporterUsername;
this.contact = contact;
this.status = status;
this.imagePath = imagePath;
}
/**
* Returns "LOST" or "FOUND" depending on the concrete
subclass.
* This value is also what gets stored in the MySQL "type"
column.
*/
public abstract String getType();
public int getId() {
return id;
}
public void setId(int id) {
this.id = id;
}
public String getName() {
return name;
}
public void setName(String name) {
this.name = name;
}
public String getCategory() {
return category;
}
public void setCategory(String category) {
this.category = category;
}
public String getDescription() {
return description;
}
public void setDescription(String description) {
this.description = description;
}
public String getColour() {
return colour;
}
public void setColour(String colour) {
this.colour = colour;
}
public String getLocation() {
return location;
}
public void setLocation(String location) {
this.location = location;
}
public LocalDate getDate() {
return date;
}
public void setDate(LocalDate date) {
this.date = date;
}
public String getReporterUsername() {
return reporterUsername;
}
public void setReporterUsername(String reporterUsername) {
this.reporterUsername = reporterUsername;
}
public String getContact() {
return contact;
}
public void setContact(String contact) {
this.contact = contact;
}
public String getStatus() {
return status;
}
public void setStatus(String status) {
this.status = status;
}
public String getImagePath() {
return imagePath;
}
public void setImagePath(String imagePath) {
this.imagePath = imagePath;
}
/**
* True if this item currently has an attached image.
* Used by DashboardFrame/AdminFrame to decide whether to
show
* "View Image" or "No Image" in the table and dialogs.
*/
public boolean hasImage() {
return imagePath != null && !imagePath.isBlank();
}
@Override
public String toString() {
return getType() + "{" +
"id=" + id +
", name='" + name + '\'' +
", category='" + category + '\'' +
", colour='" + colour + '\'' +
", location='" + location + '\'' +
", status='" + status + '\'' +
'}';
}
}