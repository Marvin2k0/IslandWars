package de.marvinleiers.islandwars.kits;

import org.bukkit.entity.Player;

public abstract class Kit
{
    private String name;
    private Player player;

    public Kit(String name, Player player)
    {
        this.name = name;
        this.player = player;
    }

    public Player getPlayer()
    {
        return player;
    }

    public String getName()
    {
        return name;
    }

    public abstract void receiveItems(byte id);
}
