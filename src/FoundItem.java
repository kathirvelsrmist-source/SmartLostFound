import java.time.LocalDate;
public class FoundItem extends Item {
public FoundItem(String name,
String category,
String description,
String colour,
String location,
LocalDate date,
String reporterUsername,
String contact,
String status,
String imagePath) {
super(name, category, description, colour, location,
date,
reporterUsername, contact, status, imagePath);
}
@Override
public String getType() {
return "FOUND";
}
}