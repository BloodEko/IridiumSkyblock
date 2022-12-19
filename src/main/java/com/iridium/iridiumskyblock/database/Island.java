package com.iridium.iridiumskyblock.database;

import com.iridium.iridiumcore.Color;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.enhancements.SizeEnhancementData;
import com.iridium.iridiumskyblock.managers.IslandManager;
import com.iridium.iridiumteams.Rank;
import com.iridium.iridiumteams.database.Team;
import com.j256.ormlite.field.DatabaseField;
import com.sk89q.worldedit.math.BlockVector3;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class Island extends Team {
    @DatabaseField(columnName = "color", canBeNull = false)
    private Color color;

    public Island(String name) {
        setName(name);
        setDescription(IridiumSkyblock.getInstance().getConfiguration().defaultDescription);
        setCreateTime(LocalDateTime.now());
        this.color = IridiumSkyblock.getInstance().getConfiguration().defaultBorderColor;
    }

    public Island(int id) {
        setId(id);
    }

    @Override
    public double getValue() {
        return IridiumSkyblock.getInstance().getTeamManager().getTeamValue(this);
    }
    
    /**
     * Returns a new location for the the fixed center point which has 
     * a constant distance to other centers in the world.
     */
    public Location getCenter(World world) {
        if (getId() == 1) return new Location(world, 0, 0, 0);
        // In this algorithm position 2 is where id 1 is, position 3 is where id 2 is, ect.
        int position = getId() - 1;

        // The radius of the last completed square
        int radius = (int) (Math.floor((Math.sqrt(position) - 1) / 2) + 1);
        int diameter = radius * 2;
        int perimeter = diameter * 4;

        // The position the square was last completed at
        int lastCompletePosition = (perimeter * (radius - 1)) / 2;

        // The current index in the perimeter where 1 is first and 0 is the last index
        int currentIndexInPerimeter = (position - lastCompletePosition) % perimeter;

        Location location;

        switch (currentIndexInPerimeter / diameter) {
            case 0:
                location = new Location(world, (currentIndexInPerimeter - radius), 0, -radius);
                break;
            case 1:
                location = new Location(world, radius, 0, (currentIndexInPerimeter % diameter) - radius);
                break;
            case 2:
                location = new Location(world, radius - (currentIndexInPerimeter % diameter), 0, radius);
                break;
            case 3:
                location = new Location(world, -radius, 0, radius - (currentIndexInPerimeter % diameter));
                break;
            default:
                throw new IllegalStateException("Could not find island location with ID: " + getId());
        }

        return location.multiply(IridiumSkyblock.getInstance().getConfiguration().distance);
    }
    
    /**
     * Returns a new location, for the center of the worldborder.
     */
    public Location newWorldBorderCenter(World world) {
        int blocked = IridiumSkyblock.getInstance().getConfiguration().blockedDistance;
                   // 50      / 2  -   20     = 5
                   // 100     / 2  -   20     = 30
                   // 150     / 2  -   20     = 55
        int xOff = (getSize() / 2) -  blocked;
        return getCenter(world).subtract(new Location(world, xOff, 0, 0));
    }
    
    /**
     * Returns a new location, where the player would spawn on the island, facing west.
     */
    public Location newSpawnLocation(World world) {
        //int yaw = 90; //west
        //return getCenter(world).add(new Location(world, 0, 0, 0, yaw, 0));
        return getCenter(world);
    }
    
    /**
     * Returns the lower 2D location for that island in that world.
     */
    public Location getPosition1(World world) {
        int xOff = getSize() - IridiumSkyblock.getInstance().getConfiguration().blockedDistance;
        int zOff = getSize() / 2;
        return getCenter(world).subtract(new Location(world, xOff, 0, zOff));
    }
    
    /**
     * Returns the upper 2D location for that island in that world.
     */
    public Location getPosition2(World world) {
        int xOff = IridiumSkyblock.getInstance().getConfiguration().blockedDistance;
        int zOff = getSize() / 2;
        return getCenter(world).add(new Location(world, xOff, 0, zOff));
    }
    
    /**
     * Returns the lower blocked position.
     */
    public BlockVector3 getBlockedPos1(int minWorldHeight) {
        int xOff = 1;
        int zOff = IridiumSkyblock.getInstance().getConfiguration().distance / 2;
        Location pos = getCenter(null);
        return BlockVector3.at(pos.getBlockX() + xOff, minWorldHeight, pos.getBlockZ() - zOff);
    }
    
    /**
     * Returns the upper blocked position.
     */
    public BlockVector3 getBlockedPos2(int maxWorldHeight) {
        int xOff = IridiumSkyblock.getInstance().getConfiguration().blockedDistance;
        int zOff = IridiumSkyblock.getInstance().getConfiguration().distance / 2;
        Location pos = getCenter(null);
        return BlockVector3.at(pos.getBlockX() + xOff, maxWorldHeight, pos.getBlockZ() + zOff);
    }

    public int getSize() {
        int sizeLevel = IridiumSkyblock.getInstance().getTeamManager().getTeamEnhancement(this, "size").getLevel();
        SizeEnhancementData sizeEnhancementData = IridiumSkyblock.getInstance().getEnhancements().sizeEnhancement.levels.get(sizeLevel);
        if (sizeEnhancementData == null) {
            IridiumSkyblock.getInstance().getLogger().warning("size enhancement for level " + sizeLevel + " is null, defaulting to 50");
            return 50;
        }
        return sizeEnhancementData.size;
    }

    public boolean isInIsland(Location location) {
        IslandManager islandManager = IridiumSkyblock.getInstance().getTeamManager();
        World world = location.getWorld();
        if (islandManager.isInSkyblockWorld(world)) {
            return isInIsland(location.getBlockX(), location.getBlockZ());
        } else {
            return false;
        }
    }

    public boolean isInIsland(int x, int z) {
        Location pos1 = getPosition1(null);
        Location pos2 = getPosition2(null);

        return pos1.getX() <= x && pos1.getZ() <= z && pos2.getX() >= x && pos2.getZ() >= z;
    }

    public void setColor(Color color) {
        this.color = color;
        IridiumSkyblock.getInstance().getTeamManager().getMembersOnIsland(this).forEach(user -> IridiumSkyblock.getInstance().getTeamManager().sendIslandBorder(user.getPlayer()));
    }

    @Override
    public @NotNull String getName() {
        if (super.getName() != null) return super.getName();
        String ownerName = IridiumSkyblock.getInstance().getTeamManager().getTeamMembers(this).stream()
                .filter(user -> user.getUserRank() == Rank.OWNER.getId())
                .findFirst()
                .map(User::getName)
                .orElse("N/A");
        return ownerName + "'s Island";
    }
}
