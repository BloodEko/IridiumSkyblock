package com.iridium.iridiumskyblock.test;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * Visualizes the center algorithm.
 */
public class DistanceTester {
    
    public static void main(String[] args) {
        for (int i = 1; i <= 10; i++) {
            System.out.println(i + ": " + getCenter(null, i, 151));
        }
    }
    
    public static Location getCenter(World world, int id, int distance) {
        
        if (id == 1) return new Location(world, 0, 0, 0);
        // In this algorithm position 2 is where id 1 is, position 3 is where id 2 is, ect.
        int position = id - 1;

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
                throw new IllegalStateException("Could not find island location with ID: " + id);
        }

        return location.multiply(distance);
    }
}
