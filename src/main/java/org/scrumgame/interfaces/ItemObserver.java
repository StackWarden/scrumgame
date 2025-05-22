package org.scrumgame.interfaces;

public interface ItemObserver {
    void onItemPickedUp(GameItem item, int playerId);
    void onItemDropped(GameItem item, int playerId);
    void onItemUsed(GameItem item, int playerId);
}
