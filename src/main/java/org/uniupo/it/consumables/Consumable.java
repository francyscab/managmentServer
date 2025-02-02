package org.uniupo.it.consumables;

public class Consumable {
    String name;
    int quantity;
    int maxQuantity;

    public Consumable(String name, int quantity, int maxQuantity) {
        this.name = name;
        this.quantity = quantity;
        this.maxQuantity = maxQuantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(int maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    @Override
    public String toString() {
        return "Consumable{" +
                "name=" + name +
                ", quantity=" + quantity +
                ", maxQuantity=" + maxQuantity +
                '}';
    }
}
