package org.scrumgame.observers;

import org.scrumgame.interfaces.GameItem;
import org.scrumgame.interfaces.ItemObserver;
import org.scrumgame.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ItemEventLogger implements ItemObserver {

    private final PlayerService playerService;

    @Autowired
    public ItemEventLogger(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Override
    public void onItemPickedUp(GameItem item, int playerId) {
        System.out.println("Player " + playerService.getPlayerNameById(playerId) + " picked up " + item.getName());
    }

    @Override
    public void onItemDropped(GameItem item, int playerId) {
        System.out.println("Player " + playerService.getPlayerNameById(playerId) + " dropped " + item.getName());
    }

    @Override
    public void onItemUsed(GameItem item, int playerId) {
        System.out.println("Player " + playerService.getPlayerNameById(playerId) + " used " + item.getName());
    }
}
