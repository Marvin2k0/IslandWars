package de.marvinleiers.islandwars.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class ResetBlock
{
    private Location location;
    private Material type;

    public ResetBlock(Location location, Block block)
    {
        this.location = location;
        this.type = block.getType();
    }

    public Location getLocation()
    {
        return location;
    }

    public Material getType()
    {
        return type;
    }
}
