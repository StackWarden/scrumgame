package org.scrumgame.classes.tests;

import org.junit.jupiter.api.Test;
import org.scrumgame.classes.Player;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void testSetNameBoundaryCases() {
        // Create a player
        Player player = new Player();
        player.setId(1); // Set an ID to perform the database update

        // Test a typical name
        assertDoesNotThrow(() -> player.setName("John"), "A typical name should not throw any exception.");
        assertEquals("John", player.getName(), "The player's name should be correctly set to 'John'.");

        // Test an empty name
        assertDoesNotThrow(() -> player.setName(""), "An empty name should not throw any exception.");
        assertEquals("", player.getName(), "The player's name should be correctly set to an empty string.");

        // Test a name with only spaces
        assertDoesNotThrow(() -> player.setName("   "), "A name with only spaces should not throw any exception.");
        assertEquals("   ", player.getName(), "The player's name should be correctly set to the string with spaces.");

        // Test a name exactly at the boundary (255 characters)
        String boundaryName = "a".repeat(255); // Java 11 String repetition
        assertDoesNotThrow(() -> player.setName(boundaryName), "A name of exactly 255 characters should not throw any exception.");
        assertEquals(boundaryName, player.getName(), "The player's name should correctly handle names at the boundary length.");

        // Test a name exceeding the boundary (256 characters)
        String longName = "a".repeat(256);
        assertThrows(IllegalArgumentException.class, () -> player.setName(longName), "A name longer than 255 characters should throw an exception.");

        // Test a null name
        assertThrows(NullPointerException.class, () -> player.setName(null), "A null value for the name should throw a NullPointerException.");
    }
}
