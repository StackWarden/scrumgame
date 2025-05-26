package org.scrumgame.commands;

import org.scrumgame.database.models.Session;
import org.scrumgame.game.GameService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class Status {

    private final Session session;
    private final GameService gameService;

    public Status(Session session, GameService gameService) {
        this.session = session;
        this.gameService = gameService;
    }

    @ShellMethod(key = "status", value = "Show the status of the game")
    public String status() {
        return String.format("You are in room " + gameService.getCurrentRoom()) + " out of 10";
    }
}