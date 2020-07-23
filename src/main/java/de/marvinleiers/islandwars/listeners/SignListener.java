package de.marvinleiers.islandwars.listeners;

import de.marvinleiers.gameapi.GameAPI;
import de.marvinleiers.gameapi.exceptions.EntryPointNotSetException;
import de.marvinleiers.gameapi.game.Game;
import de.marvinleiers.islandwars.IslandWars;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignListener implements Listener
{
    @EventHandler
    public void onInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();

        if (IslandWars.getPlugin().getApi().inGame(player))
            return;

        if (!event.hasBlock())
            return;

        Block block = event.getClickedBlock();

        if (!block.getType().toString().contains("SIGN"))
            return;

        for (Game game : GameAPI.games)
        {
            for (Location loc : game.getSigns())
            {
                if (loc.distance(block.getLocation()) <= 1)
                {
                    try
                    {
                        game.join(player);
                    }
                    catch (EntryPointNotSetException e)
                    {
                        player.sendMessage("§cEntry point of game " + game.getName() + " has not been set. Contact staff!");
                    }
                    break;
                }
            }
        }
    }
}
