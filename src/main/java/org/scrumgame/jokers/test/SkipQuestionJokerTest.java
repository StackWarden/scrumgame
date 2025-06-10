package org.scrumgame.jokers.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.scrumgame.jokers.SkipQuestionJoker;

import static org.junit.jupiter.api.Assertions.*;

public class SkipQuestionJokerTest {

    private GameServiceStub gameServiceStub;
    private GameServiceMock gameServiceMock;
    private SkipQuestionJoker skipQuestionJoker;

    @BeforeEach
    public void setUp() {
        gameServiceStub = new GameServiceStub();
        gameServiceMock = new GameServiceMock();
    }

    @Test
    public void testUseJoker_WithStub_ShouldReturnDefaultResponse() {
        skipQuestionJoker = new SkipQuestionJoker(gameServiceStub);

        String result = skipQuestionJoker.use();

        assertEquals("Used skip-question joker! Question skipped successfully", result,
                "Joker should return message with stub's default response");
    }

    @Test
    public void testUseJoker_WithStub_CustomResponse() {
        gameServiceStub.setSkipQuestionResult("No active session.");
        skipQuestionJoker = new SkipQuestionJoker(gameServiceStub);

        String result = skipQuestionJoker.use();

        assertEquals("Used skip-question joker! No active session.", result,
                "Joker should return message with custom stub response");
    }

    @Test
    public void testUseJoker_WithMock_ShouldCallSkipQuestion() {
        skipQuestionJoker = new SkipQuestionJoker(gameServiceMock);

        String result = skipQuestionJoker.use();

        assertTrue(gameServiceMock.wasSkipQuestionCalled(),
                "skipQuestion should be called when using skip-question joker");
        assertEquals("Used skip-question joker! Question skipped", result,
                "Joker should return message with mock's response");
    }

    @Test
    public void testUseJokerTwice_ShouldOnlyWorkOnce() {
        skipQuestionJoker = new SkipQuestionJoker(gameServiceMock);

        String firstUse = skipQuestionJoker.use();
        gameServiceMock.reset();
        String secondUse = skipQuestionJoker.use();

        assertTrue(firstUse.contains("Used skip-question joker!"),
                "First use should return success message");
        assertEquals("This joker has already been used.", secondUse,
                "Second use should return already used message");
        assertFalse(gameServiceMock.wasSkipQuestionCalled(),
                "skipQuestion should NOT be called on second use");
    }
}
