package org.scrumgame.interfaces;

import org.scrumgame.classes.Monster;

import java.util.List;

// Create MonsterSpawnObserver interface that passes a list of monsters to the observer
public interface MonsterSpawnObserver {
    void onMonsterSpawned(List<Monster> spawnedMonsters);
}
