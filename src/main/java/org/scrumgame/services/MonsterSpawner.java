package org.scrumgame.services;

import org.scrumgame.classes.Monster;
import org.scrumgame.classes.Room;
import org.scrumgame.database.models.Session;
import org.scrumgame.observers.MonsterSpawnObserver;
import org.scrumgame.game.GameContext;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// define the mosnterspawner class and register it as spring managed service
// this class is the subject in the observer pattern for Monster spawning
// this class's responsibility is to spawn monsters and notify the registered observers about this event
@Service
public class MonsterSpawner {
    // reference gamecontext to know what monster to spawn for a room/session
    private final GameContext context;
    // this is the key for the observer pattern, this keeps a list of all observers who want to know when monsters are spawned
    private final List<MonsterSpawnObserver> observers = new ArrayList<>();
    // constructor with gamecontext object injected by spring and assign this to it's own context field (internal)
    public MonsterSpawner(GameContext context) {
        this.context = context;
    }
    // add each observer to the list if it's not already on th elist
    public void addObserver(MonsterSpawnObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }
    // remove an observer from the list, in case it needs to be re-used
    public void removeObserver(MonsterSpawnObserver observer) {
        observers.remove(observer);
    }
    // pass a list of the monsters that are spawned to ever known observer
    private void notifyObservers(List<Monster> spawnedMonsters) {
        for (MonsterSpawnObserver observer : observers) {
            observer.onMonsterSpawned(spawnedMonsters);
        }
    }
    // the main method of this monsterspawner class, it's what the gameservice calls when a player answers a question wrong
    public List<Monster> spawnMonstersForRoom(Session session, Room room) {
        // use the gamecontext to generate a list of monsters
        List<Monster> monsters = context.spawnMonstersForRoom(session, room);
        // notifies all REGISTERED observers
        notifyObservers(monsters);
        // return lol, you know this
        return monsters;
    }
}
