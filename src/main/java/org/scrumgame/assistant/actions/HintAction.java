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
        Player player = gameService.getPlayer();

        if (player == null) {
            System.out.println("Hint action is running, but no player found.");
            return;
        }

        String hint = gameService.getHint();
        System.out.println("Hint: " + hint);
    }
}