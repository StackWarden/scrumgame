package org.scrumgame.jokers.test;

import org.scrumgame.game.GameService;

public class GameServiceMock extends GameService {

    private boolean defeatCurrentMonsterCalled = false;
    private String defeatedByParameter = null;
    private boolean skipQuestionCalled = false;

    public GameServiceMock() {
        super(null, null, null, null);
    }

    @Override
    public void defeatCurrentMonster(String defeatedBy) {
        defeatCurrentMonsterCalled = true;
        defeatedByParameter = defeatedBy;
    }

    @Override
    public String skipQuestion() {
        skipQuestionCalled = true;
        return "\nQuestion skipped";
    }

    public boolean wasDefeatCurrentMonsterCalled() {
        return defeatCurrentMonsterCalled;
    }

    public String getDefeatedByParameter() {
        return defeatedByParameter;
    }

    public boolean wasSkipQuestionCalled() {
        return skipQuestionCalled;
    }

    public void reset() {
        defeatCurrentMonsterCalled = false;
        defeatedByParameter = null;
        skipQuestionCalled = false;
    }
}
