package org.scrumgame.jokers;

import org.scrumgame.game.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component("kill-monster")
public class KillMonsterJoker extends Joker {

    private final GameService gameService;

    @Autowired
    public KillMonsterJoker(@Lazy GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    protected String applyEffect() {
        gameService.defeatCurrentMonster("Joker");
        return "Used kill-monster Joker!";
    }
}
