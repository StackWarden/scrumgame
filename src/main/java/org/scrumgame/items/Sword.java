package org.scrumgame.items;

import org.scrumgame.interfaces.GameItem;

public class Sword implements GameItem {

    @Override
    public void pickUp(Runnable onComplete) {
        onComplete.run();
    }

    @Override
    public void drop(Runnable onComplete) {
        onComplete.run();
    }

    @Override
    public String getName() {
        return "Sword";
    }

    @Override
    public void use(Runnable onComplete) {
        onComplete.run();
    }
}
