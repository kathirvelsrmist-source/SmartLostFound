import java.time.LocalDate;
/**
* LostItem.java
*
* Represents an item that a user has LOST. Inherits every
common
* field and getter/setter from Item, and only adds the piece
of
* behaviour that makes it a "LostItem" instead of a
"FoundItem":
* the getType() override.
*/
public class LostItem extends Item {
public LostItem(String name,
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
return "LOST";
}
}