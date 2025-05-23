package org.scrumgame.factories;

import org.scrumgame.database.models.Item;
import org.scrumgame.interfaces.GameItem;
import org.scrumgame.items.Sword;
import org.scrumgame.items.Totem;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class ItemSpawner {
    public final static Random random = new Random();

    public ItemSpawner() {

    }

    public void spawnItems(int levelId, int amount) {
        for (int i = 0; i < amount; i++) {
            GameItem gameItem;
            int index = random.nextInt(2);
            switch (index) {
                case 0 -> gameItem = new Sword();
                case 1 -> gameItem = new Totem();
                default -> throw new IllegalStateException("Random out of bounds");
            }

            Item item = new Item(gameItem.getName(), levelId);
            item.save();
        }
    }
}
