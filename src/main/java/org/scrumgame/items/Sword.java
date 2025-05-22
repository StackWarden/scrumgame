package org.scrumgame.items;

import org.scrumgame.interfaces.GameItem;

public class Sword implements GameItem {

    @Override
    public String  pickUp(Runnable onComplete) {
        onComplete.run();
        return "You picked up a " + getName().toUpperCase();
    }

    @Override
    public String drop(Runnable onComplete) {
        onComplete.run();
        return "You dropped your " + getName().toUpperCase();
    }

    @Override
    public String getName() {
        return "Sword";
    }

    @Override
    public String use(Runnable onComplete) {
        onComplete.run();
        return "You used a " + getName().toUpperCase();
    }
}
