package de.marvinleiers.islandwars.commands.subcommands;

import de.marvinleiers.islandwars.IslandWars;
import org.bukkit.entity.Player;

public class SetFirstDropoffCommand extends SubCommand
{
    @Override
    public String getName()
    {
        return "setfirstdropoff";
    }

    @Override
    public String getDescription()
    {
        return "Set the dropoff point for team one";
    }

    @Override
    public String getSyntax()
    {
        return "/iswarsadmin setfirstdropoff <game>";
    }

    @Override
    public void execute(Player player, String[] args)
    {
        if (args.length < 2)
        {
            player.sendMessage("§cUsage: /iswarsadmin setfirstdropoff <game>");
            return;
        }

        IslandWars.getPlugin().config.setLocation("games." + args[1] + ".team-1.dropoff", player.getLocation());
        player.sendMessage("§aDrop off has been set!");
    }
}
