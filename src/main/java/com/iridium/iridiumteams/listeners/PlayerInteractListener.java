package com.iridium.iridiumteams.listeners;

import java.util.Optional;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEntityEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
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

    @EventHandler(ignoreCancelled=true)
    public void onInteract(PlayerInteractEvent event) {
        if (!(event.getClickedBlock().getState() instanceof InventoryHolder)) {
            return;
        }
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }
        ifOutsideIsland(
            event.getClickedBlock().getLocation(), 
            player, 
            PermissionType.OPEN_CONTAINERS, 
            iridiumTeams.getMessages().cannotOpenContainers, 
            () -> event.setCancelled(true));
    }
    
    /**
     * Cancels the taking of of water/lava on other islands.
     */
    @EventHandler(ignoreCancelled=true)
    public void onTakeWater(PlayerBucketFillEvent event) {
        ifOutsideIsland(
            event.getBlock().getLocation(),
            event.getPlayer(),
            PermissionType.BUCKET,
            iridiumTeams.getMessages().cannotInteract,
            () -> event.setCancelled(true));
    }

    /**
     * Cancels the taking of entities with a bucket on other islands.
     */
    @EventHandler(ignoreCancelled=true)
    public void onBucketFish(PlayerBucketEntityEvent event) {
        ifOutsideIsland(
            event.getEntity().getLocation(),
            event.getPlayer(),
            PermissionType.BUCKET,
            iridiumTeams.getMessages().cannotInteract,
            () -> event.setCancelled(true));
    }
    
    /**
     * Cancels the placement of water/lava on other islands.
     */
    @EventHandler(ignoreCancelled=true)
    public void onPlaceWater(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Material type = event.getItem() == null 
               ? null : event.getItem().getType();
        
        if (type == Material.WATER_BUCKET || type == Material.LAVA_BUCKET) {
            ifOutsideIsland(
                event.getClickedBlock().getLocation(),
                event.getPlayer(),
                PermissionType.BUCKET,
                iridiumTeams.getMessages().cannotInteract,
                () -> event.setCancelled(true));
        }
    }
    
    /**
     * Prevents players from destroying crops by jumping.
     */
    @EventHandler(ignoreCancelled=true)
    public void onTrample(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL) {
            return;
        }
        ifOutsideIsland(
            event.getClickedBlock().getLocation(),
            event.getPlayer(),
            PermissionType.BLOCK_BREAK,
            "",
            () -> event.setCancelled(true));
    }
    
    /**
     * If the player has no permission at the location, runs the code.
     */
    private void ifOutsideIsland(Location loc, Player player, PermissionType permissionType,
            String message, Runnable code) {
        U user = iridiumTeams.getUserManager().getUser(player);
        if (user.isBypassing()) {
            return;
        }
        Optional<T> team = iridiumTeams.getTeamManager().getTeamViaLocation(loc);
        if (team.isPresent() && !iridiumTeams.getTeamManager()
                .getTeamPermission(team.get(), user, permissionType)) {
            
            if (!message.isEmpty()) {
                String msg = message.replace("%prefix%", iridiumTeams.getConfiguration().prefix);
                user.getPlayer().sendMessage(StringUtils.color(msg));
            }
            code.run();
        }
    }
}
