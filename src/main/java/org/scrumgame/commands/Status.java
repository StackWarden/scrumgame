package org.scrumgame.commands;

import org.scrumgame.classes.Player;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class Status {

    private Player player;

    @ShellMethod(key = "status", value = "Show the status of the game")
    public String status() {
        Player player = new Player();
        return "You are in room " + player.getStatus() + " / 10";
    }
}