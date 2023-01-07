package com.iridium.iridiumteams.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

import com.iridium.iridiumskyblock.gui.CreateGUI;

/**
 * Listens to the event to propagate it.
 */
public class InventoryCloseListener implements Listener {
    
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof CreateGUI) {
            ((CreateGUI) holder).onClose();
        }
    }
}
