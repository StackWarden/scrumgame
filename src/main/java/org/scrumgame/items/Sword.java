package org.scrumgame.items;

import org.scrumgame.interfaces.GameItem;

public class Sword implements GameItem {

    @Override
    public void pickUp(Runnable onComplete) {
        System.out.println("You picked up a" + getName().toUpperCase());
        onComplete.run();
    }

    @Override
    public void drop(Runnable onComplete) {
        System.out.println("You dropped your " + getName().toUpperCase());
        onComplete.run();
    }

    @Override
    public String getName() {
        return "Sword";
    }

    @Override
    public void use(Runnable onComplete) {
        System.out.println("You used a " + getName().toUpperCase());
        onComplete.run();
    }
}
