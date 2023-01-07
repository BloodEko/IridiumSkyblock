package com.iridium.iridiumskyblock.managers;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.configs.Schematics;
import com.iridium.iridiumskyblock.database.Island;
import com.iridium.iridiumskyblock.schematics.Schematic;
import com.iridium.iridiumskyblock.schematics.SchematicPaster;
import com.iridium.iridiumskyblock.schematics.WorldEdit;

import io.papermc.lib.PaperLib;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SchematicManager {

    public final SchematicPaster schematicPaster;
    public final Map<String, File> schematicFiles;

    private final boolean worldEdit = Bukkit.getPluginManager().isPluginEnabled("WorldEdit");

    public SchematicManager() {
        File parent = new File(IridiumSkyblock.getInstance().getDataFolder(), "schematics");
        SchematicPaster schematicPaster = worldEdit ? new WorldEdit() : new Schematic();

        if ((worldEdit) && !WorldEdit.isWorking()) {
            IridiumSkyblock.getInstance().getLogger().warning("WorldEdit version doesn't support minecraft version, falling back to default integration");
            schematicPaster = new Schematic();
        }

        this.schematicPaster = schematicPaster;
        this.schematicFiles = new HashMap<>();
        for (File file : parent.listFiles()) {
            schematicFiles.put(file.getName(), file);
        }
    }

    public CompletableFuture<Void> pasteSchematic(Island island, Schematics.SchematicConfig schematic) {
        return CompletableFuture.runAsync(() -> {
            if (IridiumSkyblock.getInstance().getConfiguration().enabledWorlds.getOrDefault(World.Environment.NORMAL, true)) {
                pasteSchematic(island, schematic.overworld, IridiumSkyblock.getInstance().getTeamManager().getWorld(World.Environment.NORMAL)).join();
            }
            if (IridiumSkyblock.getInstance().getConfiguration().enabledWorlds.getOrDefault(World.Environment.NETHER, true)) {
                pasteSchematic(island, schematic.nether, IridiumSkyblock.getInstance().getTeamManager().getWorld(World.Environment.NETHER)).join();
            }
            if (IridiumSkyblock.getInstance().getConfiguration().enabledWorlds.getOrDefault(World.Environment.THE_END, true)) {
                pasteSchematic(island, schematic.end, IridiumSkyblock.getInstance().getTeamManager().getWorld(World.Environment.THE_END)).join();
            }
        });
    }
    
    /**
     * Pastes the island-schematic with it's center at the middle of the blocked area.
     */
    private CompletableFuture<Void> pasteSchematic(Island island, Schematics.SchematicWorld schematic, World world) {
        int xOff = IridiumSkyblock.getInstance().getConfiguration().blockedDistance / 2;
        xOff += IridiumSkyblock.getInstance().getConfiguration().islandXoff;
        Location center = island.getCenter(world).add(xOff, schematic.islandHeight, 0);
        
        File file = schematicFiles.getOrDefault(schematic.schematicID, schematicFiles.values().stream().findFirst().orElse(null));
        CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        
        generateChunksAsync(island, world);
        Bukkit.getScheduler().runTask(IridiumSkyblock.getInstance(), () -> {
            if (file == null) {
                center.getBlock().setType(Material.BEDROCK);
                IridiumSkyblock.getInstance().getLogger().warning("Could not find schematic " + schematic.schematicID);
            } else {
                schematicPaster.paste(file, center, schematic.ignoreAirBlocks, completableFuture);
            }
        });
        return completableFuture;
    }

    /**
     * Generates and loads all chunks of this island in the world asynchronously.
     * Blocks until the operation is completed.
     */
    private void generateChunksAsync(Island island, World world) {
        Location pos1 = island.getPosition1(world);
        Location pos2 = island.getPosition2(world);
        
        int x1 = pos1.getBlockX() >> 4;
        int z1 = pos1.getBlockZ() >> 4;
        
        int x2 = pos2.getBlockX() >> 4;
        int z2 = pos2.getBlockZ() >> 4;
        
        List<Chunk> chunks = new ArrayList<>(); //to prevent GC
        for (int x = x1; x <= x2; x++) {
            for (int z = z1; z <= z2; z++) {
                chunks.add(PaperLib.getChunkAtAsync(world, x, z, true).join());
            }
        }
    }
}
