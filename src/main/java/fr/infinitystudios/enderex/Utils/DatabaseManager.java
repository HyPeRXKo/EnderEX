package fr.infinitystudios.enderex.Utils;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {

    private final JavaPlugin plugin;
    private Connection connection;

    public DatabaseManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void connect() throws SQLException {
        File dataDir = new File(plugin.getDataFolder(), "data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        File dbFile = new File(dataDir, "users.db");
        String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();

        connection = DriverManager.getConnection(url);
    }

    public void initDatabase() throws SQLException {
        createTable();
        createIndexes();
    }


    /*
    public Connection getConnection() {
        return connection;
    }
     */

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void createTable() throws SQLException {
        String sql = """
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            uuid TEXT NOT NULL,
            platform TEXT NOT NULL
        );
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void createIndexes() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
            CREATE INDEX IF NOT EXISTS idx_users_uuid
            ON users(uuid);
        """);

            stmt.execute("""
            CREATE INDEX IF NOT EXISTS idx_users_name_nocase
            ON users(name COLLATE NOCASE);
        """);

        }
    }

    public void insertUser(String name, UUID uuid, Platform platform) throws SQLException {
        String sql = """
        INSERT INTO users (name, uuid, platform)
        VALUES (?, ?, ?);
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, uuid.toString());
            ps.setString(3, platform.name());
            ps.executeUpdate();
        }
    }

    public List<UserEntry> getUsersByName(String name) throws SQLException {
        String sql = "SELECT * FROM users WHERE name = ? COLLATE NOCASE;";
        List<UserEntry> results = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                results.add(mapRow(rs));
            }
        }
        return results;
    }

    public UserEntry getUserByID(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ? LIMIT 1;";

        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return null;
    }

    public UserEntry getUserByUUID(UUID uuid) throws SQLException {
        String sql = "SELECT * FROM users WHERE uuid = ? LIMIT 1;";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return null;
    }

    public List<UserEntry> getUsersByPage(int page) throws SQLException {
        List<UserEntry> results = new ArrayList<>();

        int pageSize = 10;
        int pageNumber = Math.max(page, 1);
        int offset = (pageNumber - 1) * pageSize;

        String sql = "SELECT * FROM users ORDER BY id LIMIT ? OFFSET ?;";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, pageSize);
            ps.setInt(2, offset);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        }

        return results;
    }

    public int getTotalPages() throws SQLException {
        int pageSize = 10;
        int totalUsers = 0;

        String sql = "SELECT COUNT(*) FROM users;";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                totalUsers = rs.getInt(1);
            }

        }

        return (int) Math.ceil(totalUsers / (double) pageSize);
    }

    public boolean isPageValid(int page) throws SQLException {
        int totalPages = getTotalPages();

        if (totalPages == 0) {
            return page == 1;
        }

        return page >= 1 && page <= totalPages;
    }


    private UserEntry mapRow(ResultSet rs) throws SQLException {
        return new UserEntry(
                rs.getInt("id"),
                rs.getString("name"),
                UUID.fromString(rs.getString("uuid")),
                Platform.valueOf(rs.getString("platform"))
        );
    }



}