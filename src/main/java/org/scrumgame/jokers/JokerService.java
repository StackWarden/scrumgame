package org.scrumgame.jokers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class JokerService {

    private final Map<String, Joker> jokers;

    @Autowired
    public JokerService(Map<String, Joker> jokers) {
        this.jokers = jokers;
    }

    public String useJoker(String jokerId) {
        Joker joker = jokers.get(jokerId);
        if (joker == null) {
            return "Joker not found: " + jokerId;
        }
        return joker.use();
    }
}
