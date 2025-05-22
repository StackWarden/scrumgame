package org.scrumgame.items;

import org.scrumgame.interfaces.GameItem;

public class Totem implements GameItem {
    @Override
    public String getName() {
        return "Totem of undying";
    }

    @Override
    public void use(Runnable onComplete) {
        System.out.println("Used a " + getName().toUpperCase());
        onComplete.run();
    }

    @Override
    public void pickUp(Runnable onComplete) {
        System.out.println("You picked up a " + getName().toUpperCase());
        onComplete.run();
    }

    @Override
    public void drop(Runnable onComplete) {
        System.out.println("You dropped your " + getName().toUpperCase());
        onComplete.run();
    }
}
