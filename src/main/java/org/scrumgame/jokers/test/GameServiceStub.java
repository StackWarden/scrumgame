package org.scrumgame.jokers.test;

import org.scrumgame.game.GameService;

public class GameServiceStub extends GameService {

    private String skipQuestionResult = "Question skipped successfully";

    public GameServiceStub() {
        super(null, null, null, null, null, null);
    }

    @Override
    public void defeatCurrentMonster(String defeatedBy) {
        // does nothing lol
    }

    @Override
    public String skipQuestion() {
        return skipQuestionResult;
    }

    public void setSkipQuestionResult(String result) {
        this.skipQuestionResult = result;
    }
}
