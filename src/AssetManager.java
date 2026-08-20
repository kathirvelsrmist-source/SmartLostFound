import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AssetManager {

    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean register(String username, String password, String name, String phone) {
        if (isUsernameTaken(username)) {
            return false;
        }

        String sql = "INSERT INTO users (username, password, name, phone, role) " +
                     "VALUES (?, ?, ?, ?, 'USER')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, name);
            ps.setString(4, phone);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isUsernameTaken(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addItem(Item item) {
        String sql = "INSERT INTO items " +
                     "(type, name, category, description, colour, location, item_date, " +
                     "reporter_username, contact, status, image_path) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, item.getType());
            ps.setString(2, item.getName());
            ps.setString(3, item.getCategory());
            ps.setString(4, item.getDescription());
            ps.setString(5, item.getColour());
            ps.setString(6, item.getLocation());
            ps.setDate(7, item.getDate() != null ? Date.valueOf(item.getDate()) : null);
            ps.setString(8, item.getReporterUsername());
            ps.setString(9, item.getContact());
            ps.setString(10, item.getStatus());
            ps.setString(11, item.getImagePath());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateItemStatus(int itemId, String newStatus) {
        String sql = "UPDATE items SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setInt(2, itemId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteItem(int itemId) {
        String sql = "DELETE FROM items WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, itemId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Item> getAllItems() {
        String sql = "SELECT * FROM items ORDER BY id DESC";
        return runItemQuery(sql);
    }

    public List<Item> getUserItems(String username) {
        String sql = "SELECT * FROM items WHERE reporter_username = ? ORDER BY id DESC";
        return runItemQuery(sql, username);
    }

    public List<Item> searchItems(String keyword) {
        String sql = "SELECT * FROM items WHERE " +
                     "name LIKE ? OR category LIKE ? OR description LIKE ? OR " +
                     "colour LIKE ? OR location LIKE ? OR reporter_username LIKE ? OR " +
                     "status LIKE ? ORDER BY id DESC";

        String likeKeyword = "%" + keyword + "%";

        return runItemQuery(
                sql,
                likeKeyword,
                likeKeyword,
                likeKeyword,
                likeKeyword,
                likeKeyword,
                likeKeyword,
                likeKeyword
        );
    }

    public List<Item> filterItems(String type, String category, String status, String location) {
        StringBuilder sql = new StringBuilder("SELECT * FROM items WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (type != null && !type.equalsIgnoreCase("All")) {
            sql.append(" AND type = ?");
            params.add(type.toUpperCase());
        }

        if (category != null && !category.equalsIgnoreCase("All")) {
            sql.append(" AND category = ?");
            params.add(category);
        }

        if (status != null && !status.equalsIgnoreCase("All")) {
            sql.append(" AND status = ?");
            params.add(status);
        }

        if (location != null && !location.isBlank() && !location.equalsIgnoreCase("All")) {
            sql.append(" AND location LIKE ?");
            params.add("%" + location + "%");
        }

        sql.append(" ORDER BY id DESC");

        return runItemQuery(sql.toString(), params.toArray());
    }

    public List<Item> checkPossibleMatches(Item item) {
        String oppositeType =
                "LOST".equalsIgnoreCase(item.getType()) ? "FOUND" : "LOST";

        String sql = "SELECT * FROM items WHERE type = ? AND status = 'ACTIVE' AND " +
                     "category = ? AND (name LIKE ? OR colour LIKE ?) ORDER BY id DESC";

        String likeName = "%" + item.getName() + "%";
        String likeColour = "%" + item.getColour() + "%";

        return runItemQuery(
                sql,
                oppositeType,
                item.getCategory(),
                likeName,
                likeColour
        );
    }

    private List<Item> runItemQuery(String sql, Object... params) {
        List<Item> items = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapRowToItem(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    private Item mapRowToItem(ResultSet rs) throws SQLException {
        Date sqlDate = rs.getDate("item_date");
        LocalDate localDate = sqlDate != null ? sqlDate.toLocalDate() : null;

        Item item = ItemFactory.createItem(
                rs.getString("type"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getString("description"),
                rs.getString("colour"),
                rs.getString("location"),
                localDate,
                rs.getString("reporter_username"),
                rs.getString("contact"),
                rs.getString("status"),
                rs.getString("image_path")
        );

        item.setId(rs.getInt("id"));
        return item;
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("name"),
                rs.getString("phone"),
                rs.getString("role")
        );
    }

    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users ORDER BY (role = 'ADMIN') DESC, username ASC";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public boolean deleteUser(String username) {
        if ("kathir".equalsIgnoreCase(username)) {
            return false;
        }

        String sql = "DELETE FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static class Stats {
        public int totalUsers;
        public int totalItems;
        public int totalLostItems;
        public int totalFoundItems;
    }

    public Stats getDashboardStats() {
        Stats stats = new Stats();

        String sql = "SELECT " +
                     "(SELECT COUNT(*) FROM users) AS totalUsers, " +
                     "(SELECT COUNT(*) FROM items) AS totalItems, " +
                     "(SELECT COUNT(*) FROM items WHERE type = 'LOST') AS totalLost, " +
                     "(SELECT COUNT(*) FROM items WHERE type = 'FOUND') AS totalFound";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                stats.totalUsers = rs.getInt("totalUsers");
                stats.totalItems = rs.getInt("totalItems");
                stats.totalLostItems = rs.getInt("totalLost");
                stats.totalFoundItems = rs.getInt("totalFound");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stats;
    }
}

