package de.marvinleiers.islandwars.commands.subcommands;

import de.marvinleiers.gameapi.game.Game;
import de.marvinleiers.islandwars.IslandWars;
import de.marvinleiers.islandwars.utils.Messages;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.UUID;

public class CreateSecondNetherStarCommand extends SubCommand
{
    @Override
    public String getName()
    {
        return "setsecondstar";
    }

    @Override
    public String getDescription()
    {
        return "Sets the nether star for a team";
    }

    @Override
    public String getSyntax()
    {
        return null;
    }

    @Override
    public void execute(Player player, String[] args)
    {
        if (args.length < 2)
        {
            player.sendMessage("§cUsage: /iswarsadmin setfirststar <game>");
            return;
        }

        Game game = IslandWars.getPlugin().getApi().getGame(args[1]);
        Location location = player.getLocation();

        IslandWars.getPlugin().config.setLocation("games." + game.getName() + ".team-2.star." + UUID.randomUUID().toString(), location);

        Item netherStar = location.getWorld().dropItem(location, new ItemStack(Material.NETHER_STAR));
        netherStar.setVelocity(new Vector(0, 0, 0));
        netherStar.setPickupDelay(Integer.MAX_VALUE);

        player.sendMessage("§aStar for team two has been set!");
    }
}
