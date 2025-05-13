package org.scrumgame.database.models;

import org.scrumgame.classes.Question;

import java.util.List;

public class MonsterLog {
    private int id;
    private int sessionId;
    private List<Question> questions;
    private boolean defeated;
}
