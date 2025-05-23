package org.scrumgame.items;

import org.scrumgame.interfaces.GameItem;

public class Totem implements GameItem {
    @Override
    public String getName() {
        return "Totem of undying";
    }

    @Override
    public void use(Runnable onComplete) {
        onComplete.run();
    }

    @Override
    public void pickUp(Runnable onComplete) {
        onComplete.run();
    }

    @Override
    public void drop(Runnable onComplete) {
        onComplete.run();
    }
}
