package org.scrumgame.strategies;

public class PredefinedHintStrategy implements HintStrategy {
    @Override
    public String getHint(String answer, String hint) {
        return hint;
    }
}
