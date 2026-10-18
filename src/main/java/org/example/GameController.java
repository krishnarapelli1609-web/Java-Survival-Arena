package org.example;

import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
@CrossOrigin
public class GameController {

    private Player player;
    private Enemy enemy;
    private int stage = 1;
    private boolean gameOver = false;
    private boolean defending = false;


    @PostMapping("/new")
    public Map<String, Object> newGame(
            @RequestParam(defaultValue = "Krishna") String name) {

        name = name.trim();

        if (name.isEmpty()) {
            name = "Player";
        }

        player = new Player(name);
        enemy = new Zombie();
        stage = 1;
        gameOver = false;
        defending = false;

        Map<String, Object> result = state();
        result.put("message", "Stage 1: Zombie approaches!");
        return result;
    }

    @PostMapping("/action/{action}")
    public Map<String, Object> action(@PathVariable String action) {
        if (player == null || gameOver) {
            return response("Start a new game first.");
        }

        String message;

        switch (action.toLowerCase()) {
            case "attack":
                player.attack(enemy);
                message = "You attacked " + enemy.name + "!";
                if (enemy.isAlive()) {
                    enemy.attack(player);
                }
                break;

            case "defend":
                defending = true;
                message = "You defended against the enemy!";
                enemy.attack(player, true);
                break;


            case "heal":
                int oldHealth = player.health;
                player.health += 20;

                if (player.health > 100) {
                    player.health = 100;
                }

                int healed = player.health - oldHealth;
                message = "You restored " + healed + " health!";
                break;


            case "run":
                gameOver = true;
                message = "You left the arena.";
                break;

            default:
                return response("Unknown action.");
        }

        if (!player.isAlive()) {
            gameOver = true;
            message += " You were defeated!";
        } else if (!enemy.isAlive() && !gameOver) {
            if (enemy instanceof Zombie) {
                player.addReward(50, 20);
            } else if (enemy instanceof Goblin) {
                player.addReward(75, 30);
            } else if (enemy instanceof Boss) {
                player.addReward(200, 100);
            }

            stage++;
            if (stage == 2) {
                enemy = new Goblin();
                message += " Goblin approaches!";
            } else if (stage == 3) {
                enemy = new Boss();
                message += " The Dark Lord has arrived!";
            } else {
                gameOver = true;
                message += " Victory! You conquered the arena!";
            }
        }


        if (gameOver) {
            Database.savePlayer(
                    player.name,
                    player.health,
                    player.getLevel(),
                    player.getXp(),
                    player.getCoins()
            );
        }

        Map<String, Object> result = state();
        result.put("message", message);
        result.put("gameOver", gameOver);
        return result;

    }

    @GetMapping("/state")
    public Map<String, Object> getState() {
        if (player == null) return response("Start a new game first.");
        return state();
    }

    private Map<String, Object> state() {
        Map<String, Object> data = new HashMap<>();
        data.put("playerName", player.name);
        data.put("playerHealth", player.health);
        data.put("playerLevel", player.getLevel());
        data.put("playerXp", player.getXp());
        data.put("playerCoins", player.getCoins());
        data.put("enemyName", enemy.name);
        data.put("enemyHealth", enemy.health);
        data.put("stage", stage);
        data.put("gameOver", gameOver);
        return data;
    }

    private Map<String, Object> response(String message) {
        Map<String, Object> data = new HashMap<>();
        data.put("message", message);
        data.put("gameOver", gameOver);
        return data;
    }


    @PostMapping("/save")
    public Map<String, Object> savePlayer() {
        Map<String, Object> result = new HashMap<>();

        if (player == null) {
            result.put("message", "Start a new game first.");
            result.put("success", false);
            return result;
        }

        boolean saved = Database.savePlayer(
                player.name,
                player.health,
                player.getLevel(),
                player.getXp(),
                player.getCoins()
        );

        result.put("success", saved);
        result.put("message", saved
                ? "Player profile saved to MySQL!"
                : "Failed to save player profile. Check the server console.");

        return result;
    }

    @GetMapping("/profile")
    public Map<String, Object> getPlayerProfile() {
        Map<String, Object> result = new HashMap<>();

        if (player == null) {
            result.put("message", "Start a new game first.");
            result.put("success", false);
            return result;
        }

        String profile = Database.getPlayerProfile(player.name);

        result.put("success", true);
        result.put("profile", profile);
        return result;
    }

    @GetMapping("/leaderboard")
    public Map<String, Object> getLeaderboard() {
        Map<String, Object> result = new HashMap<>();

        result.put("players", Database.getLeaderboard());

        return result;
    }

}
