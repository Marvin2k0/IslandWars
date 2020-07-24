package de.marvinleiers.islandwars.commands.subcommands;

import de.marvinleiers.islandwars.IslandWars;
import org.bukkit.entity.Player;

public class SetSecondDropoffCommand extends SubCommand
{
    @Override
    public String getName()
    {
        return "setseconddropoff";
    }

    @Override
    public String getDescription()
    {
        return "Set the dropoff point for team two";
    }

    @Override
    public String getSyntax()
    {
        return "/iswarsadmin setseconddropoff <game>";
    }

    @Override
    public void execute(Player player, String[] args)
    {
        if (args.length < 2)
        {
            player.sendMessage("§cUsage: /iswarsadmin setseconddropoff <game>");
            return;
        }

        IslandWars.getPlugin().config.setLocation("games." + args[1] + ".team-2.dropoff", player.getLocation());
        player.sendMessage("§aDrop off has been set!");
    }
}
