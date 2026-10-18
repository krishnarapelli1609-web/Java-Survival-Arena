package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseTest {
    public static void main(String[] args) {
        String url =
                "jdbc:mysql://localhost:3306/survival_arena";
        String username =
                System.getenv().getOrDefault("DB_USER", "root");

        String password =
                System.getenv("DB_PASSWORD");
        try (Connection connection =
                     DriverManager.getConnection(
                             url, username, password)) {

            System.out.println(
                    "JDBC CONNECTION SUCCESSFUL!");
            System.out.println(
                    "Connected to survival_arena.");

        } catch (SQLException e) {
            System.out.println(
                    "JDBC CONNECTION FAILED!");
            System.out.println(e.getMessage());
        }
    }
}
