package org.example;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class ArenaServer {
    private static Player player;
    private static Enemy enemy;
    private static int stage = 1;
    private static boolean gameOver = false;
    private static final List<String> leaderboard = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0);

        server.createContext("/api/game", ArenaServer::handleGame);
        server.createContext("/", ArenaServer::handleStatic);

        server.start();
        System.out.println("Java Survival Arena Server Started!");
        System.out.println("Open http://localhost:8080");
    }

    private static void handleGame(HttpExchange ex) throws IOException {
        ex.getResponseHeaders().set("Content-Type",
                "application/json; charset=UTF-8");

        try {
            String path = ex.getRequestURI().getPath();
            String method = ex.getRequestMethod();
            Map<String, Object> result;

            if (path.endsWith("/new") && method.equals("POST")) {
                String query = ex.getRequestURI().getRawQuery();
                String name = "Krishna";
                if (query != null && query.startsWith("name=")) {
                    name = URLDecoder.decode(
                            query.substring(5), StandardCharsets.UTF_8);
                }
                if (name.trim().isEmpty()) name = "Player";

                player = new Player(name.trim());
                enemy = new Zombie();
                stage = 1;
                gameOver = false;
                result = state();
                result.put("message", "Stage 1: Zombie approaches!");

            } else if (path.endsWith("/state") && player != null) {
                result = state();

            } else if (path.contains("/action/") &&
                    method.equals("POST")) {
                result = action(path.substring(
                        path.lastIndexOf('/') + 1));

            } else if (path.endsWith("/save") &&
                    method.equals("POST")) {
                result = saveProfile();

            } else if (path.endsWith("/profile")) {
                result = new HashMap<>();
                result.put("success", true);
                result.put("profile", player == null
                        ? "Start a game first."
                        : profileText());

            } else if (path.endsWith("/leaderboard")) {
                result = new HashMap<>();
                result.put("players", new ArrayList<>(leaderboard));

            } else {
                result = message("Unknown API request.");
            }

            sendJson(ex, result);
        } catch (Exception e) {
            sendJson(ex, message("Error: " + e.getMessage()));
        }
    }

    private static Map<String, Object> action(String action) {
        if (player == null || gameOver) {
            return message("Start a new game first.");
        }

        String msg;
        switch (action.toLowerCase()) {
            case "attack":
                player.attack(enemy);
                msg = "You attacked " + enemy.name + "!";
                if (enemy.isAlive()) enemy.attack(player);
                break;
            case "defend":
                msg = "You defended against the enemy!";
                enemy.attack(player, true);
                break;

            case "heal":
                int old = player.health;
                player.health = Math.min(100, player.health + 20);
                int healed = player.health - old;
                msg = "You restored " + healed + " health!";
                break;

            case "run":
                gameOver = true;
                msg = "You left the arena.";
                break;
            default:
                return message("Unknown action.");
        }

        if (!player.isAlive()) {
            gameOver = true;
            msg += " You were defeated!";
        } else if (!enemy.isAlive() && !gameOver) {

            if (enemy instanceof Zombie) {
                player.addReward(50, 20);
            } else if (enemy instanceof Goblin) {
                player.addReward(75, 30);
            } else if (enemy instanceof Boss) {
                player.addReward(200, 100);
            }

// Recover 30 HP after defeating an enemy
            int oldHealth = player.health;
            player.health = Math.min(100, player.health + 30);
            int recovered = player.health - oldHealth;

            msg += " You recovered " + recovered + " HP!";

            stage++;

            if (stage == 2) {
                enemy = new Goblin();
                msg += " Goblin approaches!";
            } else if (stage == 3) {
                enemy = new Boss();
                msg += " The Dark Lord has arrived!";
            } else {
                gameOver = true;
                msg += " Victory! You conquered the arena!";
            }
        }

        if (gameOver) {
            leaderboard.removeIf(s -> s.startsWith(player.name + " |"));
            leaderboard.add(profileText());
        }

        Map<String, Object> result = state();
        result.put("message", msg);
        result.put("gameOver", gameOver);
        return result;
    }

    private static Map<String, Object> state() {
        Map<String, Object> m = new HashMap<>();
        m.put("playerName", player.name);
        m.put("playerHealth", "" + player.health);
        m.put("playerLevel", "" + player.getLevel());
        m.put("playerXp", "" + player.getXp());
        m.put("playerCoins", "" + player.getCoins());
        m.put("enemyName", enemy.name);
        m.put("enemyHealth", "" + enemy.health);
        m.put("stage", "" + stage);
        m.put("gameOver", gameOver);
        return m;
    }

    private static String profileText() {
        return player.name + " | Health: " + player.health
                + " | Level: " + player.getLevel()
                + " | XP: " + player.getXp()
                + " | Coins: " + player.getCoins();
    }

    private static Map<String, Object> saveProfile() {
        Map<String, Object> m = new HashMap<>();
        if (player == null) {
            m.put("success", "false");
            m.put("message", "Start a game first.");
        } else {
            leaderboard.removeIf(s -> s.startsWith(player.name + " |"));
            leaderboard.add(profileText());
            m.put("success", "true");
            m.put("message", "Player profile saved for this session!");
        }
        return m;
    }

    private static Map<String, Object> message(String text) {
        Map<String, Object> m = new HashMap<>();
        m.put("message", text);
        m.put("gameOver", gameOver);
        return m;
    }

    private static void sendJson(HttpExchange ex, Map<String, ?> data)
            throws IOException {
        StringBuilder json = new StringBuilder("{");
        for (Map.Entry<String, ?> e : data.entrySet()) {
            if (json.length() > 1) json.append(',');
            json.append('"').append(escape(e.getKey())).append("\":");
            Object v = e.getValue();
            if (v instanceof Number || v instanceof Boolean) {
                json.append(v);
            } else if (v instanceof List) {
                json.append('[');
                boolean first = true;
                for (Object item : (List<?>) v) {
                    if (!first) json.append(',');
                    json.append('"').append(escape(String.valueOf(item)))
                            .append('"');
                    first = false;
                }
                json.append(']');
            } else {
                json.append('"').append(escape(String.valueOf(v)))
                        .append('"');
            }
        }
        json.append('}');
        byte[] bytes = json.toString().getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(200, bytes.length);
        try (OutputStream out = ex.getResponseBody()) {
            out.write(bytes);
        }
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private static void handleStatic(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();
        if (path.equals("/")) path = "/index.html";

        Path root = Paths.get("src/main/resources/static").toAbsolutePath();
        Path file = root.resolve(path.substring(1)).normalize();

        if (!file.startsWith(root) || !Files.isRegularFile(file)) {
            byte[] notFound = "404 - File not found".getBytes(StandardCharsets.UTF_8);
            ex.sendResponseHeaders(404, notFound.length);
            try (OutputStream out = ex.getResponseBody()) {
                out.write(notFound);
            }
            return;
        }

        String type = path.endsWith(".css") ? "text/css"
                : path.endsWith(".js") ? "application/javascript"
                : "text/html";
        ex.getResponseHeaders().set("Content-Type", type + "; charset=UTF-8");

        byte[] bytes = Files.readAllBytes(file);
        ex.sendResponseHeaders(200, bytes.length);
        try (OutputStream out = ex.getResponseBody()) {
            out.write(bytes);
        }
    }
}
