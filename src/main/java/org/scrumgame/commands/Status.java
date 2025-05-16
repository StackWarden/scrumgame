package org.scrumgame.commands;

import org.scrumgame.classes.Player;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class Status {

    @ShellMethod(key = "status", value = "Show the status of the game")
    public String status() {
        Player currentPlayer = Player.getCurrentPlayer();
        return currentPlayer.getName() + " is in room " + currentPlayer.getStatus() + " / 10 met een score van";
    }
}