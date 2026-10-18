package org.example;

class Boss extends Enemy {

    public Boss() {
        super("Dark Lord", 150, 25);
    }

    @Override
    public void attack(Character target) {
        System.out.println("👹 Dark Lord unleashes a powerful attack!");

        target.takeDamage(attackPower);

        System.out.println(
                target.name + " took " + attackPower + " damage!"
        );
    }
}

