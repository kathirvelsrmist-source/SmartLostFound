import java.time.LocalDate;
/**
* ItemFactory.java
*
* Implements the Factory design pattern for creating Item
* objects.
* Instead of the rest of the application writing:
*
* if (type.equals("LOST")) { new LostItem(...) } else {
* new FoundItem(...) }
*
* everywhere it needs a new Item, it simply calls
* ItemFactory.createItem(type, ...) and lets this class
* decide which
* concrete subclass to instantiate. This keeps object-
* creation logic
* in one place and is what AssetManager uses both when
* inserting a
* newly-reported item and when rebuilding Item objects out of
* MySQL
* ResultSet rows.
*/
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
