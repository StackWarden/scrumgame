package org.scrumgame.game;

import org.scrumgame.database.models.Session;
import org.springframework.stereotype.Component;

@Component
public class GameContext {
    private Session session;

    public boolean isActive() {
        return session != null && !session.isGameOver();
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    public void clear() {
        this.session = null;
    }
}
