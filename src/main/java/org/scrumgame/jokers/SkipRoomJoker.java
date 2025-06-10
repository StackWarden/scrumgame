package org.scrumgame.jokers;

import org.scrumgame.game.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component("skip-room")
public class SkipRoomJoker extends Joker {

    private final GameService gameService;

    @Autowired
    public SkipRoomJoker(@Lazy GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    protected String applyEffect() {
        String result = gameService.skipRoom();
        return "Used skip-room joker! " + result;
    }
}