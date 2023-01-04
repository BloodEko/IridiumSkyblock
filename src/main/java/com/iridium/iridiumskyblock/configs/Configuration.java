package com.iridium.iridiumskyblock.configs;

import java.util.Arrays;
import java.util.Map;

import org.bukkit.World;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.ImmutableMap;
import com.iridium.iridiumcore.Color;
import com.iridium.iridiumcore.Item;

public class Configuration extends com.iridium.iridiumteams.configs.Configuration {
    public Configuration() {
        super("&9", "Island", "IridiumSkyblock");
        this.createRequiresName = false;
    }

    public String islandCreateTitle = "&b&lIsland Created";
    public String islandCreateSubTitle = "&7IridiumSkyblock by Peaches_MLG";
    public String defaultDescription = "Default island description :c";
    public String worldName = "IridiumSkyblock";
    public String spawnWorldName = "world";

    public boolean obsidianBucket = true;
    
    /** The absolute distance between the island centers. */
    public int distance = 151;
    
    /** The distance that will be blocked within the island. */
    public int blockedDistance = 30;
    
    /** The xoff which will be applied to the schematic pasting. */
    public int islandXoff = 1;
    
    /** Clears the player inventory on island deletion. */
    public boolean clearInventory = true;
    
    public Item islandCrystal = new Item(XMaterial.NETHER_STAR, 1, "&9*** &9&lIsland Crystal &9***", Arrays.asList(
            "",
            "&9%amount% Island Crystals",
            "&9&l[!] &9Right-Click to Redeem"
    ));

    public Map<World.Environment, Boolean> enabledWorlds = new ImmutableMap.Builder<World.Environment, Boolean>()
            .put(World.Environment.NORMAL, true)
            .put(World.Environment.NETHER, true)
            .put(World.Environment.THE_END, true)
            .build();


    public Color defaultBorderColor = Color.BLUE;
    public Map<Color, Boolean> enabledBorders = new ImmutableMap.Builder<Color, Boolean>()
            .put(Color.BLUE, true)
            .put(Color.RED, true)
            .put(Color.GREEN, true)
            .put(Color.OFF, true)
            .build();

}
