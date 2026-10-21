public class User {
private int id;
private String username;
private String password;
private String name;
private String phone;
private String role;
/**
* Constructor used when the user does not have a database
id yet
* (for example, right before registration).
*/
public User(String username, String password, String name,
String phone, String role) {
this.username = username;
this.password = password;
this.name = name;
this.phone = phone;
this.role = role;
}
/**
* Constructor used when rebuilding a User object from a
database
* row, where the id is already known.
*/
public User(int id, String username, String password,
String name, String phone, String role) {
this(username, password, name, phone, role);
this.id = id;
}
public int getId() {
return id;
}
public void setId(int id) {
this.id = id;
}
public String getUsername() {
return username;
}
public void setUsername(String username) {
this.username = username;
}
public String getPassword() {
return password;
}
public void setPassword(String password) {
this.password = password;
}
public String getName() {
return name;
}
public void setName(String name) {
this.name = name;
}
public String getPhone() {
return phone;
}
public void setPhone(String phone) {
this.phone = phone;
}
public String getRole() {
return role;
}
public void setRole(String role) {
this.role = role;
}
/**
* Convenience method used by the UI layer to decide
whether the
* logged-in user should see AdminFrame or DashboardFrame.
*/
public boolean isAdmin() {
return "ADMIN".equalsIgnoreCase(role);
}
@Override
public String toString() {
return "User{" +
"id=" + id +
", username='" + username + '\'' +
", name='" + name + '\'' +
", phone='" + phone + '\'' +
", role='" + role + '\'' +
'}';
}
}