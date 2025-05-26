package org.scrumgame.jokers;

public abstract class Joker {
    private boolean used = false;

    public final String use() {
        if (used) {
            return "This joker has already been used.";
        }
        used = true;
        return applyEffect();
    }

    protected abstract String applyEffect();
}
