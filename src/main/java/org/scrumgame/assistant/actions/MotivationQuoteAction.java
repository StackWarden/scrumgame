package org.scrumgame.assistant.actions;

import org.scrumgame.assistant.AssistantAction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class MotivationQuoteAction implements AssistantAction {

    private static final List<String> QUOTES = List.of(
            "Motivational quote 1",
            "Motivational quote 2",
            "Motivational quote 3",
            "Motivational quote 4",
            "Motivational quote 5"
    );

    @Override
    public void run() {
        int index = ThreadLocalRandom.current().nextInt(QUOTES.size());
        String quote = QUOTES.get(index);
        System.out.println(quote);
    }
}
