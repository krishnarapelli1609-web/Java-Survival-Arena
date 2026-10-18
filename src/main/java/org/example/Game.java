package org.example;

import java.util.Scanner;

public class Game {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("================================");
        System.out.println("       JAVA SURVIVAL ARENA");
        System.out.println("================================");

        Player player = new Player("Krishna");

        // Stage 1
        Enemy enemy = new Zombie();

        int stage = 1;

        while (player.isAlive() && stage <= 3) {

            System.out.println("\n================================");
            System.out.println("           STAGE " + stage);
            System.out.println("================================");

            System.out.println("Enemy: " + enemy.name);

            while (player.isAlive() && enemy.isAlive()) {

                System.out.println("\n--------------------------------");
                player.displayStatus();
                enemy.displayStatus();
                System.out.println("--------------------------------");

                System.out.println("\nChoose your action:");
                System.out.println("1. Attack");
                System.out.println("2. Defend");
                System.out.println("3. Heal");
                System.out.println("4. Run");

                System.out.print("\nEnter choice: ");
                int choice = scanner.nextInt();

                switch (choice) {

                    case 1:
                        player.attack(enemy);

                        if (enemy.isAlive()) {
                            enemy.attack(player);
                        }
                        break;

                    case 2:
                        System.out.println("\n" + player.name + " is defending!");

                        if (enemy.isAlive()) {
                            enemy.attack(player, true);
                        }
                        break;

                    case 3:
                        System.out.println("\n" + player.name
                                + " used a healing potion!");

                        player.health += 20;

                        if (player.health > 100) {
                            player.health = 100;
                        }

                        System.out.println("Health restored!");
                        break;

                    case 4:
                        System.out.println("\n" + player.name + " ran away!");
                        System.out.println("GAME OVER!");

                        scanner.close();
                        return;

                    default:
                        System.out.println("\nInvalid choice! Choose 1-4.");
                }
            }

            // Player defeated
            if (!player.isAlive()) {
                System.out.println("\n================================");
                System.out.println("        YOU WERE DEFEATED!");
                System.out.println("================================");
                break;
            }

            System.out.println("\n================================");
            System.out.println("        ENEMY DEFEATED!");
            System.out.println("==================================");


            if (enemy instanceof Zombie) {

                player.addReward(50, 20);

            }
            else if (enemy instanceof Goblin) {

                player.addReward(75, 30);

            }
            else if (enemy instanceof Boss) {

                player.addReward(200, 100);

            }

            player.displayPlayerStats();

            stage++;
            // Create next enemy
            if (stage == 2) {
                enemy = new Goblin();

                System.out.println("\nNext enemy approaching...");
                System.out.println("👺 GOBLIN!");
            }
            else if (stage == 3) {
                enemy = new Boss();

                System.out.println("\nWARNING!");
                System.out.println("👹 THE DARK LORD HAS ARRIVED!");
            }
        }

        // Final victory
        if (player.isAlive() && stage > 3) {

            System.out.println("\n================================");
            System.out.println("        🏆 VICTORY!");
            System.out.println("================================");

            System.out.println("Congratulations, " + player.name + "!");
            System.out.println("You conquered the entire arena!");
        }

        Weapon sword = new Weapon("Iron Sword", 10, 60);

        System.out.println("\n========== WEAPON TEST ==========");

        sword.displayWeapon();

        System.out.println("=================================");

        scanner.close();
    }
}