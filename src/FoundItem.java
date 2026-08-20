import java.time.LocalDate;
/**
* FoundItem.java
*
* Represents an item that a user has FOUND (and is reporting
so the
* original owner can claim it). Inherits every common field
and
* getter/setter from Item, and only adds the piece of
behaviour
* that makes it a "FoundItem" instead of a "LostItem": the
* getType() override.
*/
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