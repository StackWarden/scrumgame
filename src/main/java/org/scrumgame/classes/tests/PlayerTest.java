package org.scrumgame.classes.tests;

import org.junit.jupiter.api.Test;
import org.scrumgame.classes.Player;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void testSetNameBoundaryCases() {
        // Speler aanmaken
        Player player = new Player();
        player.setId(1); // Speler ID zetten voor Database verwerking

        // Test een naam
        assertDoesNotThrow(() -> player.setName("John"), "Een naam zou geen foutmelding moeten geven.");
        assertEquals("John", player.getName(), "De spelers naam zou naar 'John' veranderd moeten zijn.");

        // Test een lege naam
        assertDoesNotThrow(() -> player.setName(""), "Een leeg veld voor een naam zou geen foutmelding moeten geven.");
        assertEquals("", player.getName(), "De spelers naam zou leeg moeten zijn.");

        // Test alleen spaties
        assertDoesNotThrow(() -> player.setName("   "), "Alleen spaties zou geen foutmelding moeten geven.");
        assertEquals("   ", player.getName(), "De spelers naam zou er nu staan als alleen spaties.");

        // Test een naam die maximale karakters gebruikt (255 characters)
        String boundaryName = "a".repeat(255); // Java 11 String repetition
        assertDoesNotThrow(() -> player.setName(boundaryName), "Een naam van precies 255 karakters zou geen fout melding moeten geven.");
        assertEquals(boundaryName, player.getName(), "Een speler naam die op het randje zit zou moeten werken.");

        // Test een naam die boven de max zit(256 characters)
        String longName = "a".repeat(256);
        assertThrows(IllegalArgumentException.class, () -> player.setName(longName), "Een naam langer dan 255 karakters geeft een fout melding.");

        // Test null als input
        assertThrows(NullPointerException.class, () -> player.setName(null), "Een null waarde geeft NullPointerException.");
    }
}

