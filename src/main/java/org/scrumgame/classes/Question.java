package org.scrumgame.classes;

// public record Question is a simplified immutable data carrier class which stores immutable data
// a record automatically generates a constructor, getters (called accessors) for each field and equals(), hashCode() and toString() methods
// all fields are private and final
public record Question(int id, String text, String correctAnswer){}