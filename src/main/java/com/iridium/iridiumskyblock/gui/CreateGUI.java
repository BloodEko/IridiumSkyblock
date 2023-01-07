package com.iridium.iridiumskyblock.gui;

import java.util.concurrent.CompletableFuture;

import org.bukkit.inventory.Inventory;

import lombok.Getter;

@Getter
public class CreateGUI extends SchematicGUI {
    private final CompletableFuture<String> completableFuture;

    public CreateGUI(Inventory previousInventory, CompletableFuture<String> completableFuture) {
        super(previousInventory);
        this.completableFuture = completableFuture;
    }

    @Override
    public void selectSchematic(String schematic) {
        completableFuture.complete(schematic);
    }

    /**
     * Called when the inventory is closed, to free resources.
     */
    public void onClose() {
        completableFuture.cancel(false);
    }
}
