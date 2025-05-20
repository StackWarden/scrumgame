package org.scrumgame.commands;

import org.scrumgame.classes.Question;
import org.scrumgame.database.models.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.scrumgame.game.GameService;

@ShellComponent
public class Game{

    private final GameService gameService;

    @Autowired
    public Game(GameService gameService) {
        this.gameService = gameService;
    }

    @ShellMethod(key = "start", value = "Start a new game session.")
    public String startNewGame() {
        if (gameService.isInGame()) {
            return "A game is already running.";
        }
        gameService.startNewSession();
        return "Game started. Question: " + gameService.getCurrentPrompt();
    }

    @ShellMethod(key = "load-game", value = "Loads an existing game session.")
    public String loadGame(@ShellOption(help = "Your savefile") int saveSlot) {
        if (gameService.isInGame()) {
            return "A game is already running.";
        }
        gameService.loadSession(saveSlot); // TODO: Initialize session and first room
        return "Game started.";
    }

    @ShellMethod(key = "prompt", value = "Show current question or monster prompt.")
    public String getCurrentPrompt() {
        if (!gameService.isInGame()) {
            return "You are not in a game. Type 'start' to begin.";
        }
        return gameService.getCurrentPrompt(); // TODO: Return prompt from Room or Monster
    }

    @ShellMethod(key = "answer", value = "Submit an answer to the current question.")
    public String submitAnswer(@ShellOption(help = "Your answer") String answer) {
        if (!gameService.isInGame()) {
            return "You are not in a game. Type 'start' to begin.";
        }
        return gameService.submitAnswer(answer);
    }

    @ShellMethod(key = "next", value = "Go to the next room if possible.")
    public String goToNextRoom() {
        if (!gameService.isInGame()) {
            return "You are not in a game. Type 'start' to begin.";
        }
        return gameService.goToNextRoom(); // TODO: Check if monsters are cleared before advancing
    }

    @ShellMethod(key = "status", value = "Show current game status.")
    public String getStatus() {
        if (!gameService.isInGame()) {
            return "You are not in a game. Type 'start' to begin.";
        }
        return gameService.getStatus(); // TODO: Display room, score, and monster count
    }

    @ShellMethod(key = "quit", value = "End the current game.")
    public String quitGame() {
        if (!gameService.isInGame()) {
            return "No game session to quit.";
        }
        gameService.endGame(); // TODO: Clear session state and mark game as ended
        return "Game ended.";
    }

    @ShellMethod(key = "hint", value = "Get a hint for the current question.")
    public String getHint() {
        if (!gameService.isInGame()) {
            return "You are not in a game. Type 'start' to begin.";
        }
        return gameService.getHint();
    }

    @ShellMethod(key = "help-game", value = "List available game commands.")
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
}
