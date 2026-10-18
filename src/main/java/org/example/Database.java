package org.example;

import java.util.ArrayList;
import java.util.List;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {


    private static final String URL =
            System.getenv().getOrDefault(
                    "DB_URL",
                    "jdbc:mysql://localhost:3306/survival_arena"
            );

    private static final String USER =
            System.getenv().getOrDefault("DB_USER", "root");

    private static final String PASSWORD =
            System.getenv("DB_PASSWORD");


    public static Connection getConnection()
            throws SQLException {

        return DriverManager.getConnection(
                URL, USER, PASSWORD);
    }

    public static boolean testConnection() {
        try (Connection connection = getConnection()) {
            System.out.println(
                    "Java Survival Arena database connected!");
            return true;

        } catch (SQLException e) {
            System.out.println(
                    "org.example.Database connection failed: "
                            + e.getMessage());
            return false;
        }
    }

    public static boolean savePlayer(
            String name,
            int health,
            int level,
            int xp,
            int coins) {

        String sql = """
        INSERT INTO players
        (player_name, health, player_level, xp, coins)
        VALUES (?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
        health = VALUES(health),
        player_level = VALUES(player_level),
        xp = VALUES(xp),
        coins = VALUES(coins)
        """;

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, health);
            ps.setInt(3, level);
            ps.setInt(4, xp);
            ps.setInt(5, coins);

            ps.executeUpdate();

            System.out.println("Player saved successfully!");
            return true;

        } catch (SQLException e) {
            System.out.println(
                    "Failed to save player: " + e.getMessage());
            return false;
        }
    }

    public static String getPlayerProfile(String name) {
        String sql = """
        SELECT player_name, health, player_level, xp, coins
        FROM players
        WHERE player_name = ?
        """;

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return "Player: " + rs.getString("player_name")
                            + " | Health: " + rs.getInt("health")
                            + " | Level: " + rs.getInt("player_level")
                            + " | XP: " + rs.getInt("xp")
                            + " | Coins: " + rs.getInt("coins");
                }
            }

            return "No saved profile found for " + name + ".";

        } catch (SQLException e) {
            System.out.println("Failed to load profile: " + e.getMessage());
            return "Could not load profile from MySQL.";
        }
    }

    public static List<String> getLeaderboard() {
        List<String> players = new ArrayList<>();

        String sql = """
        SELECT player_name, player_level, xp, coins
        FROM players
        ORDER BY player_level DESC, xp DESC
        LIMIT 10
        """;

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String player = rs.getString("player_name")
                        + " | Level: " + rs.getInt("player_level")
                        + " | XP: " + rs.getInt("xp")
                        + " | Coins: " + rs.getInt("coins");

                players.add(player);
            }

        } catch (SQLException e) {
            System.out.println("Failed to load leaderboard: " + e.getMessage());
        }

        return players;
    }


}

