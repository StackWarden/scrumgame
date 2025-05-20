package org.scrumgame.strategies;

public class HintContext {
    private HintStrategy strategy;

    public void setStrategy(HintStrategy strategy) {
        this.strategy = strategy;
    }

    public String getHint(String answer, String predefinedHint) {
        return strategy.getHint(answer, predefinedHint);
    }
}
