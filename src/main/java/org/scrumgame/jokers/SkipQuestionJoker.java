package org.scrumgame.jokers;

import org.scrumgame.game.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component("skip-question")
public class SkipQuestionJoker extends Joker {

    private final GameService gameService;

    @Autowired
    public SkipQuestionJoker(@Lazy GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    protected String applyEffect() {
        String result = gameService.skipQuestion();
        return "Used skip-question joker! " + result;
    }
}