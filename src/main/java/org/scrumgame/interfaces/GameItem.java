package org.scrumgame.interfaces;

public interface GameItem {
    String getName();
    void use(Runnable onComplete);
    void pickUp(Runnable onComplete);
    void drop(Runnable onComplete);
}
