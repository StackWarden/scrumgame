package org.scrumgame.commands;

import org.scrumgame.factories.ItemSpawner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.Availability;
import org.scrumgame.game.GameService;

@ShellComponent
public class Game{

    private final GameService gameService;

    @Autowired
    public Game(GameService gameService) {
        this.gameService = gameService;
    }

    @ShellMethod(key = "prompt", value = "Show current question or monster prompt.")
    @ShellMethodAvailability("gameAvailable")
    public String getCurrentPrompt() {
        if (!gameService.isInGame()) {
            return "You are not in a game. Type 'start' to begin.";
        }
        return gameService.getCurrentPrompt(); // TODO: Return prompt from Room or Monster
    }

    @ShellMethod(key = "answer", value = "Submit an answer to the current question.")
    @ShellMethodAvailability("gameAvailable")
    public String submitAnswer(@ShellOption(help = "Your answer") String answer) {
        if (!gameService.isInGame()) {
            return "You are not in a game. Type 'start' to begin.";
        }
        return gameService.submitAnswer(answer);
    }

    @ShellMethod(key = "next", value = "Go to the next room if possible.")
    @ShellMethodAvailability("gameAvailable")
    public String goToNextRoom() {
        if (!gameService.isInGame()) {
            return "You are not in a game. Type 'start' to begin.";
        }
        return gameService.goToNextRoom(); // TODO: Check if monsters are cleared before advancing
    }

    @ShellMethod(key = "status", value = "Show current game status.")
    @ShellMethodAvailability("gameAvailable")
    public String getStatus() {
        if (!gameService.isInGame()) {
            return "You are not in a game. Type 'start' to begin.";
        }
        return gameService.getStatus(); // TODO: Display room, score, and monster count
    }

    @ShellMethod(key = "quit", value = "End the current game.")
    @ShellMethodAvailability("gameAvailable")
    public String quitGame() {
        if (!gameService.isInGame()) {
            return "No game session to quit.";
        }
        gameService.endGame(); // TODO: Clear session state and mark game as ended
        return "Game ended.";
    }

    @ShellMethod(key = "help", value = "List available game commands.")
    public String showHelp() {
        return """
            Available commands:
            - start       → Start a new game
            - load-game   → Loads an existing game
            - prompt      → Show current question or monster
            - answer x    → Submit answer x
            - next        → Move to the next room
            - status      → View game state
            - quit        → End the current game
        """;
    }

    public Availability gameAvailable() {
        return gameService.isInGame()
                ? Availability.available()
                : Availability.unavailable("You are not in a game. Type 'start' to begin.");
    }
}
