package org.scrumgame.strategies;

import java.util.Random;

public class RandomObfuscatedHintStrategy implements HintStrategy {
    private final double revealRatio;

    public RandomObfuscatedHintStrategy(double revealRatio) {
        this.revealRatio = revealRatio;
    }

    @Override
    public String getHint(String answer, String hint) {
        StringBuilder obfuscated = new StringBuilder();
        Random random = new Random();

        for (char c : answer.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                obfuscated.append(random.nextDouble() < revealRatio ? c : "_");
            } else {
                obfuscated.append(c);
            }
        }

        return "Here's a clue: " + obfuscated;
    }
}
