import java.time.LocalDate;
public class ItemFactory {
// Private constructor: this class only exposes static
// factory
// methods and is never meant to be instantiated.
private ItemFactory() {
}
public static Item createItem(String type,
String name,
String category,
String description,
String colour,

String location,
LocalDate date,
String reporterUsername,
String contact,
String status,
String imagePath) {
if (type == null) {
throw new IllegalArgumentException("Item typecannot be null");
}
switch (type.toUpperCase()) {
case "LOST":
return new LostItem(name, category,
description, colour, location,
date, reporterUsername, contact,
status, imagePath);
case "FOUND":
return new FoundItem(name, category,
description, colour, location,
date, reporterUsername, contact,
status, imagePath);
default:
throw new IllegalArgumentException("Unknownitem type: " + type);
}
}
}
