package org.scrumgame.observers;

import org.scrumgame.game.GameService;
import org.scrumgame.interfaces.GameItem;
import org.scrumgame.interfaces.ItemObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class ItemEventLogger implements ItemObserver {

    private final GameService gameService;

    @Autowired
    public ItemEventLogger(@Lazy GameService gameService) {
        this.gameService = gameService;
    }

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

        if ("sword".equalsIgnoreCase(item.getName())) {
            gameService.defeatCurrentMonster();
        }
    }
}
