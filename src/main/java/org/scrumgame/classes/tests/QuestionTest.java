package org.scrumgame.classes.tests;

import org.junit.jupiter.api.Test;
import org.scrumgame.classes.Question;
import org.scrumgame.questions.BaseQuestion;

import static org.junit.jupiter.api.Assertions.*;

class QuestionTest {

    @Test
    void testCheckAnswerBoundaryCases() {
        // Maak een basisvraag aan met een correct antwoord
        Question question = new BaseQuestion(1, "Wat is 2 + 2?", "4", "Het is gelijk aan twee keer twee.");

        // Test exact juiste antwoord
        assertTrue(question.checkAnswer("4"), "Het antwoord '4' zou correct moeten zijn.");

        // Test juiste antwoord met spaties
        assertTrue(question.checkAnswer(" 4 "), "Het antwoord ' 4 ' met spaties zou correct moeten zijn.");

        // Test juiste antwoord met verschillende hoofdletters
        assertTrue(question.checkAnswer("FOUR"), "Het antwoord 'FOUR' zou correct moeten zijn (hoofdletterongevoelig).");

        // Test fout antwoord
        assertFalse(question.checkAnswer("5"), "Het antwoord '5' zou fout moeten zijn.");

        // Test leeg antwoord
        assertFalse(question.checkAnswer(""), "Een leeg antwoord zou fout moeten zijn.");

        // Test antwoord met alleen spaties
        assertFalse(question.checkAnswer("   "), "Een antwoord bestaande uit alleen spaties zou fout moeten zijn.");

        // Test null als input
        assertThrows(NullPointerException.class, () -> question.checkAnswer(null), "Null als antwoord zou een NullPointerException moeten veroorzaken.");
    }
}