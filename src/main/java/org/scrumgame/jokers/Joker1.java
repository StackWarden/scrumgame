package org.scrumgame.jokers;

import org.springframework.stereotype.Component;

@Component("joker1")
public class Joker1 extends Joker {
    @Override
    protected String applyEffect() {
        return "joker1";
    }
}
