package org.scrumgame.commands;

import org.scrumgame.database.models.RoomLog;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

// create a room with the RoomLog.java createRoom() method
@ShellComponent
public class RoomCommand {
    @ShellMethod(key = "cr", value = "Create a room")
    public void createRoomCommand() {
        RoomLog.createRoom();
    }
}
