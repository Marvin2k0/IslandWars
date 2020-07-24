package de.marvinleiers.islandwars.commands.subcommands;

import de.marvinleiers.islandwars.IslandWars;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.Map;

public class RemoveStarCommand extends SubCommand
{
    @Override
    public String getName()
    {
        return "removestar";
    }

    @Override
    public String getDescription()
    {
        return "Removes a star near you";
    }

    @Override
    public String getSyntax()
    {
        return "/iswarsadmin removestar <game>";
    }

    @Override
    public void execute(Player player, String[] args)
    {
        if (args.length < 2)
        {
            player.sendMessage("§cUsage: /iswarsadmin removestar <game>");
            return;
        }
        
        if (!IslandWars.getPlugin().config.isSet("games." + args[1]))
        {
            player.sendMessage("§cThe game §4" + args[1] + " §cdoes not exist!");
            return;
        }
        
        try
        {
            for (Map.Entry<String, Object> entry : IslandWars.getPlugin().config.getConfig().getConfigurationSection("games." + args[1] + ".team-1.star").getValues(false).entrySet())
            {
                if (IslandWars.getPlugin().config.getLocation("games." + args[1] + ".team-1.star." + entry.getKey()).distance(player.getLocation()) <= 1)
                {
                    IslandWars.getPlugin().config.set("games." + args[1] + ".team-1.star." + entry.getKey(), null);

                    for (Entity entity : player.getNearbyEntities(1, 1, 1))
                    {
                        if (entity instanceof Item)
                        {
                            if (((Item) entity).getItemStack().getType() == Material.NETHER_STAR)
                                ((Item) entity).remove();
                        }
                    }

                    player.sendMessage("§aRemoved!");
                    return;
                }
            }

            for (Map.Entry<String, Object> entry : IslandWars.getPlugin().config.getConfig().getConfigurationSection("games." + args[1] + ".team-2.star").getValues(false).entrySet())
            {
                if (IslandWars.getPlugin().config.getLocation("games." + args[1] + ".team-2.star." + entry.getKey()).distance(player.getLocation()) <= 1)
                {
                    IslandWars.getPlugin().config.set("games." + args[1] + ".team-2.star." + entry.getKey(), null);
                    for (Entity entity : player.getNearbyEntities(1, 1, 1))
                    {
                        if (entity instanceof Item)
                        {
                            if (((Item) entity).getItemStack().getType() == Material.NETHER_STAR)
                                ((Item) entity).remove();
                        }
                    }

                    player.sendMessage("§aRemoved!");
                    return;
                }
            }

            player.sendMessage("§cNo star has been found.");
        }
        catch (Exception ignored) {}
        
    }
}
