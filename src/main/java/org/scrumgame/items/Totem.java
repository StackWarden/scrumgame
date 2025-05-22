package org.scrumgame.items;

import org.scrumgame.interfaces.GameItem;

public class Totem implements GameItem {
    @Override
    public String getName() {
        return "Totem of undying";
    }

    @Override
    public String use(Runnable onComplete) {
        onComplete.run();
        return "You used a " + getName().toUpperCase();
    }

    @Override
    public String pickUp(Runnable onComplete) {
        onComplete.run();
        return "You picked up " + getName().toUpperCase();
    }

    @Override
    public String drop(Runnable onComplete) {
        onComplete.run();
        return "You dropped a " + getName().toUpperCase();
    }
}
