package org.scrumgame.assistant.actions;

import org.scrumgame.assistant.AssistantAction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class MotivationQuoteAction implements AssistantAction {

    private static final List<String> QUOTES = List.of(
            "Believe you can and you're halfway there.",
            "Success is not final, failure is not fatal: It is the courage to continue that counts.",
            "Don't watch the clock; do what it does. Keep going.",
            "The harder you work for something, the greater you'll feel when you achieve it.",
            "Dream it. Wish it. Do it.",
            "Push yourself, because no one else is going to do it for you.",
            "Great things never come from comfort zones.",
            "Success doesn’t just find you. You have to go out and get it.",
            "It always seems impossible until it's done.",
            "You are capable of amazing things."
    );

    @Override
    public void run() {
        int index = ThreadLocalRandom.current().nextInt(QUOTES.size());
        String quote = QUOTES.get(index);
        System.out.println(quote);
    }
}
