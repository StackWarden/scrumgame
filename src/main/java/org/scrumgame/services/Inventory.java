package org.scrumgame.services;

import org.scrumgame.database.models.Item;
import org.scrumgame.database.models.Session;
import org.scrumgame.interfaces.GameItem;
import org.scrumgame.interfaces.ItemObserver;
import org.scrumgame.items.Sword;
import org.scrumgame.items.Totem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class Inventory {
    private final List<ItemObserver> observers = new ArrayList<>();
    private final Map<Integer, GameItem> itemCache = new HashMap<>();

    @Autowired
    public Inventory(List<ItemObserver> itemObservers) {
        this.observers.addAll(itemObservers);
    }

    public List<Item> getAvailableItemsInRoom(int levelLogId) {
        return Item.getUnpickedItemsForRoom(levelLogId);
    }

    public void pickUpItem(int itemId, Session session) {
        Item item = Item.loadById(itemId);
        if (item == null || item.getLevel_log_id() != session.getCurrentRoomId()) {
            System.out.println("Item not found.");
            return;
        }
        if (item.getPlayer_id() != null) {
            System.out.println("This item is already picked up.");
            return;
        }

        GameItem gameItem = resolveGameItem(item.getName());
        if (gameItem == null) {
            System.out.println("Unknown item type.");
            return;
        }

        gameItem.pickUp(() -> {
            item.setPlayer_id(session.getPlayerId());
            item.setSession_id(session.getId());
            item.save();
            observers.forEach(o -> o.onItemPickedUp(gameItem, session.getPlayerId()));
        });

        itemCache.put(item.getId(), gameItem);
    }

    public void drop(GameItem item, int playerId) {
        item.drop(() -> observers.forEach(o -> o.onItemDropped(item, playerId)));
    }

    public void use(int itemId, int playerId, int sessionId) {
        List<Item> items = Item.getItemsByPlayerAndSession(playerId, sessionId);
        Item item = items.stream()
                .filter(i -> i.getId() == itemId)
                .findFirst()
                .orElse(null);

        if (item == null) {
            System.out.println("Item not found in your inventory.");
            return;
        }

        GameItem gameItem = resolveGameItem(item.getName());
        if (gameItem == null) {
            System.out.println("Invalid item.");
            return;
        }

        gameItem.use(() -> {
            item.setUsed(true);
            item.save();
            observers.forEach(o -> o.onItemUsed(gameItem, playerId));
        });
    }

    private GameItem resolveGameItem(String name) {
        return switch (name.toLowerCase()) {
            case "sword" -> new Sword();
            case "totem of undying" -> new Totem();
            default -> null;
        };
    }

    public List<Item> getInventoryItems(int playerId, int sessionId) {
        return Item.getItemsByPlayerAndSession(playerId, sessionId);
    }
}
