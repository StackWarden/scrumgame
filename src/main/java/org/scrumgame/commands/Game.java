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
    public void submitAnswer(@ShellOption(help = "Your answer") String answer) {
        if (!gameService.isInGame()) {
            System.out.println("You are not in a game. Type 'start' to begin.");
            return;
        }
         gameService.submitAnswer(answer);
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

    @ShellMethod(key = "items", value = "List available items in the current room.")
    @ShellMethodAvailability("gameAvailable")
    public String listRoomItems() {
        if (!gameService.isInGame()) {
            return "You are not in a game. Type 'start' to begin.";
        }
        return gameService.getRoomItems();
    }

    @ShellMethod(key = "pickup", value = "Pick up an item by its ID.")
    @ShellMethodAvailability("gameAvailable")
    public void pickUpItem(@ShellOption(help = "Item ID to pick up") int itemId) {
        if (!gameService.isInGame()) {
            System.out.println("You are not in a game. Type 'start' to begin.");
        }
        gameService.pickUpItem(itemId);
    }

    @ShellMethod(key = "inventory", value = "View all items currently held by the player.")
    @ShellMethodAvailability("gameAvailable")
    public String viewInventory() {
        if (!gameService.isInGame()) {
            return "You are not in a game. Type 'start' to begin.";
        }

        return gameService.viewPlayerInventory();
    }

    @ShellMethod(key = "quit", value = "End the current game.")
    @ShellMethodAvailability("gameAvailable")
    public String quitGame() {
        if (!gameService.isInGame()) {
            return "No game session to quit.";
        }
        gameService.endGame();
        return "Game ended.";
    }

    @ShellMethod(key = "use-item", value = "Use an item from your inventory.")
    @ShellMethodAvailability("gameAvailable")
    public void useItem(@ShellOption(help = "ID of the item to use") int itemId) {
        if (!gameService.isInGame()) {
            System.out.println("You are not in a game. Type 'start' to begin.");
            return;
        }
        gameService.useItem(itemId);
    }

    @ShellMethod(key = "drop-item", value = "Drop an item from your inventory.")
    @ShellMethodAvailability("gameAvailable")
    public void dropItem(@ShellOption(help = "The item ID to drop") int itemId) {
        gameService.dropItem(itemId);
    }

    @ShellMethod(key = "hint", value = "Get a hint for the current question.")
    public String getHint() {
        if (!gameService.isInGame()) {
            return "You are not in a game. Type 'start' to begin.";
        }
        return gameService.getHint();
    }

    @ShellMethod(key = "help", value = "List available game commands.")
    public String showHelp() {
        return """
        === Scrum Game Commands ===

        General:
        - start               → Start a new game session
        - load-game           → Load an existing saved game
        - quit                → End the current game session

        Gameplay:
        - prompt              → Show the current question or monster prompt
        - answer <x>          → Submit your answer to the current challenge
        - next                → Proceed to the next room if eligible
        - status              → View current game status

        Inventory:
        - items               → View available items in the current room
        - inventory           → View your currently held items
        - pickup <itemId>     → Pick up an item from the current room
        - use-item <itemId>   → Use an item out of your inventory
        - drop-item <itemId>  → Drops an item out of your inventory

        Use 'help' anytime to redisplay this list.
        """;
    }

    public Availability gameAvailable() {
        return gameService.isInGame()
                ? Availability.available()
                : Availability.unavailable("You are not in a game. Type 'start' to begin.");
    }
}
