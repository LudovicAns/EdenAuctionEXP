package fr.edencraft.edenauctionexp.provider.utils;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface Action {

    void run(InventoryClickEvent event);

}
