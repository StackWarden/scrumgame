package org.scrumgame.assistant.actions;

import org.scrumgame.assistant.AssistantAction;
import org.scrumgame.classes.Player;
import org.scrumgame.game.GameService;
import org.springframework.stereotype.Component;

@Component
public class HintAction implements AssistantAction {

    private final GameService gameService;

    public HintAction(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void run() {
        Player a = gameService.getPlayer();
        if (a != null) {
            System.out.println("Hint action is running. " + a.getName());
        }

        if (a == null) {
            System.out.println("Hint action is running, name is null.");
        }
    }
}
