package org.scrumgame.interfaces;

public interface GameItem {
    String getName();
    String use(Runnable onComplete);
    String pickUp(Runnable onComplete);
    String drop(Runnable onComplete);
}
