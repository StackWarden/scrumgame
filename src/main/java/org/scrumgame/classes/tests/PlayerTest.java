package org.scrumgame.classes.tests;

import org.junit.jupiter.api.Test;
import org.scrumgame.classes.Player;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest extends Player{

    @Test
    void testPlayerLevelBoundary() {
        // Test minimale waarde
        Player playerMin = new Player();
        playerMin.setLevel(1); // Minimum toegestane waarde
        assertEquals(1, playerMin.getPlayerLevel(), "De minimale levelwaarde van de speler moet 1 zijn.");

        // Test maximale waarde
        Player playerMax = new Player();
        playerMax.setLevel(100); // Maximum toegestane waarde
        assertEquals(100, playerMax.getPlayerLevel(), "De maximale levelwaarde van de speler moet 100 zijn.");

        // Test waarde onder de grens
        Player playerUnderMin = new Player();
        Exception minException = assertThrows(IllegalArgumentException.class, () -> playerUnderMin.setLevel(0));
        assertEquals("Invalid level value: 0", minException.getMessage(), "Verwachte exceptie bij waarde onder minimale grens.");

        // Test waarde boven de grens
        Player playerOverMax = new Player();
        Exception maxException = assertThrows(IllegalArgumentException.class, () -> playerOverMax.setLevel(101));
        assertEquals("Invalid level value: 101", maxException.getMessage(), "Verwachte exceptie bij waarde boven maximale grens.");
    }
}
