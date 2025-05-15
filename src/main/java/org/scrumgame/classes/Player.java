package org.scrumgame.classes;

import org.scrumgame.database.DatabaseConnection;
import org.scrumgame.database.models.Session;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Player {
    int id;
    String name;
    int status;

    public Player() {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getStatus() {
        Session session = new Session();
        return session.getId();
    }

    public String setName(String name) {
        this.name = name;
        return name;
    }


}
