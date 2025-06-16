package org.scrumgame.classes.tests;

import org.junit.jupiter.api.Test;
import org.scrumgame.classes.Player;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void testSetNameBoundaryCases() {
        // Creeër een speler
        Player player = new Player();
        player.setId(1); // Stel een ID in zodat de database-update wordt uitgevoerd

        // Test gebruikelijke naam
        assertDoesNotThrow(() -> player.setName("John"), "Een gewone naam zou zonder uitzondering verwerkt moeten worden.");
        assertEquals("John", player.getName(), "De naam van de speler moet correct ingesteld zijn op 'John'.");

        // Test lege naam
        assertDoesNotThrow(() -> player.setName(""), "Een lege naam zou zonder uitzondering verwerkt moeten worden.");
        assertEquals("", player.getName(), "De naam van de speler moet correct ingesteld zijn op een lege string.");

        // Test naam met alleen spaties
        assertDoesNotThrow(() -> player.setName("   "), "Een naam met alleen spaties zou zonder uitzondering verwerkt moeten worden.");
        assertEquals("   ", player.getName(), "De naam van de speler moet correct ingesteld zijn op de string met spaties.");

        // Test een lange naam (bijvoorbeeld 256 karakters)
        String longName = "a".repeat(256); // Java 11 String herhaling
        assertDoesNotThrow(() -> player.setName(longName), "Een lange naam mag niet leiden tot een uitzondering.");
        assertEquals(longName, player.getName(), "De naam van de speler moet correct ingesteld zijn, zelfs als deze lang is.");

        // Test null-naam
        assertThrows(NullPointerException.class, () -> player.setName(null), "Een null-waarde voor de naam moet een NullPointerException veroorzaken.");
    }
}