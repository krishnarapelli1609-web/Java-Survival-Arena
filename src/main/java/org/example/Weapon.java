package org.example;

class Weapon {
    private String name;
    private int damage;
    private int price;

    public Weapon(String var1, int var2, int var3) {
        this.name = var1;
        this.damage = var2;
        this.price = var3;
    }

    public String getName() {
        return this.name;
    }

    public int getDamage() {
        return this.damage;
    }

    public int getPrice() {
        return this.price;
    }

    public void displayWeapon() {
        System.out.println("Weapon : " + this.name);
        System.out.println("Damage : +" + this.damage);
        System.out.println("Price  : " + this.price + " coins");
    }
}


