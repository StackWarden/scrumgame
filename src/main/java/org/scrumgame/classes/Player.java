package org.scrumgame.classes;

import org.scrumgame.database.models.Session;

public class Player {
    int id;
    String name;
    int status;

    public int getStatus() {
        Session session = new Session();
        return session.getId();
    }

}
