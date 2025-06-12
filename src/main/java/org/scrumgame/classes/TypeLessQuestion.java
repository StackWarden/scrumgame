package org.scrumgame.classes;

public class TypeLessQuestion extends Question{
    public TypeLessQuestion(int id, String question, String answer, String hint) {
        super(id, question, answer, hint);
    }

    public TypeLessQuestion(String question, String answer) {
        super(question, answer);
    }
}
