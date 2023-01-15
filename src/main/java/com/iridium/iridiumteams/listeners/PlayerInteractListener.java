package com.iridium.iridiumteams.listeners;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumteams.IridiumTeams;
import com.iridium.iridiumteams.PermissionType;
import com.iridium.iridiumteams.database.IridiumUser;
import com.iridium.iridiumteams.database.Team;

import lombok.AllArgsConstructor;

/**
 * A listener which restricts non-allowed container opening.
 */
@AllArgsConstructor
public class PlayerInteractListener<T extends Team, U extends IridiumUser<T>> implements Listener {
    private final IridiumTeams<T, U> iridiumTeams;

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (!(event.getClickedBlock().getState() instanceof InventoryHolder)) {
            return;
        }        
        Player player = event.getPlayer();
        U user = iridiumTeams.getUserManager().getUser(player);
        Optional<T> team = iridiumTeams.getTeamManager().getTeamViaLocation(event.getClickedBlock().getLocation());
        
        if (team.isPresent()) {
            if (!iridiumTeams.getTeamManager().getTeamPermission(team.get(), user, PermissionType.OPEN_CONTAINERS)) {
                player.sendMessage(StringUtils.color(iridiumTeams.getMessages().cannotOpenContainers
                        .replace("%prefix%", iridiumTeams.getConfiguration().prefix)
                ));
                event.setCancelled(true);
            }
        }
    }
}
