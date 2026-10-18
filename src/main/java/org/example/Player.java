package org.example;

class Player extends Character {

    private int level;
    private int xp;
    private int coins;

    public Player(String name) {
        super(name, 100, 20);

        level = 1;
        xp = 0;
        coins = 0;
    }

    @Override
    public void attack(Character target) {

        System.out.println(name + " attacks " + target.name + "!");

        target.takeDamage(attackPower);

        System.out.println(
                target.name + " took " + attackPower + " damage!"
        );
    }

    public void addReward(int xpGained, int coinsGained) {

        xp += xpGained;
        coins += coinsGained;

        System.out.println("\n+ " + xpGained + " XP");
        System.out.println("+ " + coinsGained + " Coins");

        checkLevelUp();
    }

    private void checkLevelUp() {

        int requiredXP = level * 50;

        if (xp >= requiredXP) {

            level++;

            attackPower += 5;

            System.out.println("\n⭐ LEVEL UP!");
            System.out.println("Level: " + level);
            System.out.println("Attack Power: " + attackPower);
        }
    }

    public void displayPlayerStats() {

        System.out.println("\n========== PLAYER ==========");
        System.out.println("Name   : " + name);
        System.out.println("HP     : " + health);
        System.out.println("Attack : " + attackPower);
        System.out.println("Level  : " + level);
        System.out.println("XP     : " + xp);
        System.out.println("Coins  : " + coins);
        System.out.println("============================");
    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }

    public int getCoins() {
        return coins;
    }
}