package org.scrumgame.jokers;

import org.springframework.stereotype.Component;

@Component("joker2")
public class Joker2 extends Joker {
    @Override
    protected String applyEffect() {
        return "joker2";
    }
}
