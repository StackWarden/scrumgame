package org.scrumgame.commands;

import org.scrumgame.classes.Player;
import org.scrumgame.game.GameService;
import org.scrumgame.services.PlayerService;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import java.util.Scanner;

@ShellComponent
public class MenuCommands {

    private final GameService gameService;
    private final PlayerService playerService;

    public MenuCommands(GameService gameService, PlayerService playerService) {
        this.gameService = gameService;
        this.playerService = playerService;
    }

    @ShellMethod(key = "start", value = "Start a new game.")
    @ShellMethodAvailability("menuAvailable")
    public String startGame() {
        gameService.startNewSession();
        return "Game started. " + gameService.getCurrentPrompt();
    }

    @ShellMethod(key = "load-game", value = "Load a saved game.")
    @ShellMethodAvailability("menuAvailable")
    public void loadGame() {
        gameService.loadSession(1); // TODO: Player id should become the actual player id
    }

    @ShellMethod("Login with an existing player name.")
    public String login() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("What is your username? ");
        String name = scanner.nextLine();

        Player player = playerService.login(name);
        if (player == null) {
            return "No player found with that name. Use `register` to create a new account.";
        }

        gameService.setPlayer(player);
        return "Welcome back, " + player.getName() + "!";
    }

    @ShellMethod("Register a new player.")
    public String register() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("What username do you want to have? ");
        String name = scanner.nextLine();

        Player player = playerService.register(name);
        if (player == null) {
            return "Registration failed.";
        }

        gameService.setPlayer(player);
        return "Welcome, " + player.getName() + "! Your account has been created.";
    }

    @ShellMethod("Delete your account.")
    public String deleteAccount() {
        Player currentPlayer = gameService.getPlayer();
        if (currentPlayer == null) {
            return "You must be logged in to delete your account.";
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Are you sure? Type your full username to confirm: ");
        String input = scanner.nextLine();

        if (!input.equals(currentPlayer.getName())) {
            return "Username did not match. Account deletion canceled.";
        }

        boolean deleted = playerService.deleteAccount(currentPlayer.getId());
        if (deleted) {
            String name = currentPlayer.getName();
            gameService.endGame();
            return "Player '" + name + "' has been deleted.";
        }

        return "Failed to delete account.";
    }


    public Availability menuAvailable() {
        return !gameService.isInGame()
                ? Availability.available()
                : Availability.unavailable("You are already in a game.");
    }
}
