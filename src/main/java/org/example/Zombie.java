package org.example;

class Zombie extends Enemy {

    public Zombie() {
        super("Zombie", 60, 10);
    }

    @Override
    public void attack(Character target) {
        System.out.println("🧟 Zombie bites " + target.name + "!");

        target.takeDamage(attackPower);

        System.out.println(
                target.name + " took " + attackPower + " damage!"
        );
    }
}


