package org.scrumgame.observers;

import org.scrumgame.classes.Monster;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

// mark the class as a spring component
@Component
// implement mosterspawnobserver so it reacts to monster spawn events from the subject
public class MonsterSpawnMessageObserver implements MonsterSpawnObserver {
    // empty message, this is the coconut in essence
    private String lastMessage = "";
    // override the monsterspawnobserver interface automatically when the monsterspawn.notifyobservers is called
    @Override
    public void onMonsterSpawned(List<Monster> spawnedMonsters) {
        // if the list is null or empty set a failure message
        if (spawnedMonsters == null || spawnedMonsters.isEmpty()) {
            lastMessage = "Failed to spawn monsters.";
            return;
        }
        // use java stream to loop over every monster individually, call getPrompt() method on each monster and get a string. Also join all the prompts into a single string (comma seperated)
        String monstersList = spawnedMonsters.stream()
                .map(Monster::getPrompt)
                .collect(Collectors.joining(", "));

        lastMessage = "Wrong! You've awakened " + spawnedMonsters.size() + " monsters!\nMonsters appear: " + monstersList;
    }
    // inform the player of their impending doom (that monsters will spawn)
    public String getLastMessage() {
        return lastMessage;
    }
    // clears last message for reusability so no data persists (big bang type shit)
    public void clear() {
        lastMessage = "";
    }
}
