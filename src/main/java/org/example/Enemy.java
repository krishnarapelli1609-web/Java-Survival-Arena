package org.example;

class Enemy extends Character {

    public Enemy(String name, int health, int attackPower) {
        super(name, health, attackPower);
    }

    @Override
    public void attack(Character target) {

        System.out.println(name + " attacks " + target.name + "!");

        target.takeDamage(attackPower);

        System.out.println(
                target.name + " took " + attackPower + " damage!"
        );
    }

    // Overloaded attack method
    public void attack(Character target, boolean defending) {

        System.out.println(name + " attacks " + target.name + "!");

        int damage = attackPower;

        if (defending) {
            damage = attackPower / 2;
            System.out.println("Defense reduced the damage!");
        }

        target.takeDamage(damage);

        System.out.println(
                target.name + " took " + damage + " damage!"
        );
    }
}