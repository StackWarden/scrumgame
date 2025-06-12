package org.scrumgame.classes;




public class Achievements {
    private int id;
    private String name;
    private String description;
    private boolean unlocked;

    public Achievements(int id, String name, String description, boolean unlocked) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.unlocked = unlocked;
    }

    // getters and setters for all fields
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }
}