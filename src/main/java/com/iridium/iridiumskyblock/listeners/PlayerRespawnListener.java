package com.iridium.iridiumskyblock.listeners;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.database.User;

/**
 * A listener that respawns players on their island after death.
 */
public class PlayerRespawnListener implements Listener {
    
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        World world = Bukkit.getWorld(IridiumSkyblock.getInstance().getConfiguration().worldName);
        if (event.getRespawnLocation().getWorld() != world) {
            return;
        }

        User user = IridiumSkyblock.getInstance().getUserManager().getUser(event.getPlayer());
        user.getCurrentIsland().ifPresent(island -> {
            event.setRespawnLocation(island.getHome());
        });
    }
}
