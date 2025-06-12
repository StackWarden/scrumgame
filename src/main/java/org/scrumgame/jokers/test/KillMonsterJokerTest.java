package org.scrumgame.jokers.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.scrumgame.jokers.KillMonsterJoker;

import static org.junit.jupiter.api.Assertions.*;

public class KillMonsterJokerTest {

    private GameServiceMock gameServiceMock;
    private KillMonsterJoker killMonsterJoker;

    @BeforeEach
    public void setUp() {
        gameServiceMock = new GameServiceMock();
        killMonsterJoker = new KillMonsterJoker(gameServiceMock);
    }

    @Test
    public void testUseJoker_ShouldCallDefeatCurrentMonsterWithJokerParameter() {
        String result = killMonsterJoker.use();

        assertTrue(gameServiceMock.wasDefeatCurrentMonsterCalled(),
                "defeatCurrentMonster should be called when using kill-monster joker");
        assertEquals("Joker", gameServiceMock.getDefeatedByParameter(),
                "defeatCurrentMonster should be called with 'Joker' parameter");
        assertEquals("Used kill-monster Joker!", result,
                "Joker should return correct success message");
    }

    @Test
    public void testUseJokerTwice_ShouldOnlyCallGameServiceOnce() {
        String firstUse = killMonsterJoker.use();
        gameServiceMock.reset();
        String secondUse = killMonsterJoker.use();

        assertEquals("Used kill-monster Joker!", firstUse,
                "First use should return success message");
        assertEquals("This joker has already been used.", secondUse,
                "Second use should return already used message");
        assertFalse(gameServiceMock.wasDefeatCurrentMonsterCalled(),
                "defeatCurrentMonster should NOT be called on second use");
    }
}
