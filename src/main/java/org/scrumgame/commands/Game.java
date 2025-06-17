package org.scrumgame.commands;

import org.scrumgame.classes.Achievements;
import org.scrumgame.database.models.AchievementList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.scrumgame.game.GameService;

import java.util.List;

@ShellComponent
public class Game {

    private final GameService gameService;
    private final GameService isLoggedIn;

    @Autowired
    public Game(GameService gameService, GameService isLoggedIn) {
        this.gameService = gameService;
        this.isLoggedIn = isLoggedIn;
    }

    @ShellMethod(key = "prompt", value = "Show current question or monster prompt.")
    public String getCurrentPrompt() {
        if (!isInGame()) return notInGameMessage();
        return gameService.getCurrentPrompt();
    }

    @ShellMethod(key = "answer", value = "Submit an answer to the current question.")
    public void submitAnswer() {
        if (!isInGame()) {
            System.out.println(notInGameMessage());
            return;
        }

        if (gameService.getCurrentRoom().getRemainingQuestions().isEmpty())
        {
            System.out.println("There are no questions left in this room. Use 'next'");
            return;
        }
        gameService.submitAnswer(false);
    }
    @ShellMethod(key = "next", value = "Go to the next room if possible.")
    public String goToNextRoom() {
        if (!isInGame()) return notInGameMessage();
        return gameService.goToNextRoom();
    }

    @ShellMethod(key = "status", value = "Show current game status.")
    public String getStatus() {
        if (!isInGame()) return notInGameMessage();
        return gameService.getStatus();
    }

    @ShellMethod(key = "items", value = "List available items in the current room.")
    public String listRoomItems() {
        if (!isInGame()) return notInGameMessage();
        return gameService.getRoomItems();
    }

    @ShellMethod(key = "pickup", value = "Pick up an item by its ID.")
    public void pickUpItem(@ShellOption(help = "Item ID to pick up") int itemId) {
        if (!isInGame()) {
            System.out.println(notInGameMessage());
            return;
        }
        gameService.pickUpItem(itemId);
    }

    @ShellMethod(key = "inventory", value = "View all items currently held by the player.")
    public String viewInventory() {
        if (!isInGame()) return notInGameMessage();
        return gameService.viewPlayerInventory();
    }

    @ShellMethod(key = "room-info", value = "Bekijk een overzicht van de openstaande vragen in de huidige kamer.")
    public void viewRoomInfo() {
        if (!isInGame()) System.out.println(notInGameMessage());
        gameService.printRoomOverview();
    }

    @ShellMethod(key = "quit", value = "End the current game.")
    public String quitGame() {
        if (!isInGame()) return "No game session to quit.";
        gameService.endGame();
        return "Game ended.";
    }

    @ShellMethod(key = "use-item", value = "Use an item from your inventory.")
    public void useItem(@ShellOption(help = "ID of the item to use") int itemId) {
        if (!isInGame()) {
            System.out.println(notInGameMessage());
            return;
        }
        gameService.useItem(itemId);
    }

    @ShellMethod(key = "drop-item", value = "Drop an item from your inventory.")
    public void dropItem(@ShellOption(help = "The item ID to drop") int itemId) {
        if (!isInGame()) {
            System.out.println(notInGameMessage());
            return;
        }
        gameService.dropItem(itemId);
    }

    @ShellMethod(key = "achievements", value = "Shows current player's achievements.")
    public String achievements() {
        if (!isLoggedIn()) {
            return "There is no player logged in, please log in with a player to see achievements.";
        } else {
            List<Achievements> achievements = AchievementList.getAllAchievements();
            if (achievements.isEmpty()) {
                return "No achievements found.";
            }

            StringBuilder sb = new StringBuilder("Player Achievements:\n");
            for (Achievements a : achievements) {
                sb.append("- ").append(a.getName())
                        .append(": ").append(a.getDescription())
                        .append(" [").append(a.isUnlocked() ? "Unlocked" : "Locked").append("]\n");
            }
            return sb.toString();
        }
    }

    @ShellMethod(key = "help", value = "List available game and account commands.")
    public String showHelp() {
        return """
        === Scrum Game Commands ===

        ▶ Game Session:
        - start               → Start a new game session
        - load-game           → Load an existing saved game
        - quit                → End the current game session

        ▶ Gameplay:
        - prompt              → Show the current question or monster prompt
        - answer <x>          → Submit your answer to the current challenge
        - next                → Proceed to the next room if eligible
        - room-info           → View current game status

        ▶ Inventory:
        - items               → View available items in the current room
        - inventory           → View your currently held items
        - pickup <itemId>     → Pick up an item from the current room
        - use-item <itemId>   → Use an item from your inventory
        - drop-item <itemId>  → Drop an item from your inventory

        ▶ Jokers:
        - use-joker <joker>   → Use a joker ability (e.g., skip-room, kill-monster)

        ▶ Account:
        - login               → Log in with an existing player name
        - logout              → Log out of ur account
        - register            → Create a new player account
        - delete-account      → Permanently delete your player account
        - achievements        → Shows current players achievements

        Use 'help' anytime to redisplay this list.
        """;
    }

    private boolean isInGame() {
        return gameService.isInGame();
    }

    private boolean isLoggedIn() {
        return gameService.isLoggedIn();
    }

    private String notInGameMessage() {
        return "You are not in a game. Use 'start' to begin.";
    }
}
