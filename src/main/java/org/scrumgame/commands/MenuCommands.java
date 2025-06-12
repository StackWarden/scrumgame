package org.scrumgame.commands;

import org.scrumgame.classes.Player;
import org.scrumgame.game.GameService;
import org.scrumgame.services.PlayerService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

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
    public void startGame() {
        if (!isLoggedIn()) {
            System.out.println("You must be logged in. Use `login` or `register` first.");
            return;
        }
        if (isInGame()) {
            System.out.println("You are already in a game. Use `status` or `quit`.");
            return;
        }

        gameService.startNewSession();
    }

    @ShellMethod(key = "load-game", value = "Load a saved game.")
    public String loadGame() {
        if (!isLoggedIn()) {
            return "You must be logged in. Use `login` or `register` first.";
        }
        if (isInGame()) {
            return "You are already in a game. Use `status` or `quit`.";
        }

        gameService.loadSession(gameService.getPlayer().getId());
        return "Game session loaded.";
    }

    @ShellMethod("Login with an existing player name.")
    public String login() {
        if (isLoggedIn()) {
            return "You are already logged in.";
        }

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

    @ShellMethod("Logout from current account.")
    public String logout() {
        if (!isLoggedIn()) {
            return "You are not logged in.";
        }
        gameService.setPlayer(null);

        return "Until next time!";
    }

    @ShellMethod("Register a new player.")
    public String register() {
        if (isLoggedIn()) {
            return "You are already logged in.";
        }

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
        if (!isLoggedIn()) {
            return "You must be logged in to delete your account.";
        }
        if (isInGame()) {
            return "You cannot delete your account while in a game. Use `quit` first.";
        }

        Player currentPlayer = gameService.getPlayer();

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

    private boolean isLoggedIn() {
        return gameService.isLoggedIn();
    }

    private boolean isInGame() {
        return gameService.isInGame();
    }
}
