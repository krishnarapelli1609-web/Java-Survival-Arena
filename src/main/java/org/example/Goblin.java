package org.example;

class Goblin extends Enemy {

    public Goblin() {
        super("Goblin", 70, 15);
    }

    @Override
    public void attack(Character target) {
        System.out.println("Goblin strikes " + target.name + "!");

        target.takeDamage(attackPower);

        System.out.println(
                target.name + " took " + attackPower + " damage!"
        );
    }
}


