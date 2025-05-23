package org.scrumgame.observers;

import org.scrumgame.interfaces.GameItem;
import org.scrumgame.interfaces.ItemObserver;
import org.springframework.stereotype.Component;

@Component
public class ItemEventLogger implements ItemObserver {

    @Override
    public void onItemPickedUp(GameItem item, int playerId) {
        System.out.println("You picked up a " + item.getName());
    }

    @Override
    public void onItemDropped(GameItem item, int playerId) {
        System.out.println("You dropped a " + item.getName());
    }

    @Override
    public void onItemUsed(GameItem item, int playerId) {
        System.out.println("You used a " + item.getName());
    }
}
