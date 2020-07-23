package de.marvinleiers.islandwars.commands.subcommands;

import de.marvinleiers.gameapi.game.Game;
import de.marvinleiers.islandwars.IslandWars;
import org.bukkit.entity.Player;

public class SetFirstSpawn extends SubCommand
{
    @Override
    public String getName()
    {
        return "setfirstspawn";
    }

    @Override
    public String getDescription()
    {
        return "Set the island spawn for team one.";
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
            player.sendMessage("§cUsage: /iswarsadmin setfirstspawn <game>");
            return;
        }

        Game game = IslandWars.getPlugin().getApi().getGame(args[1]);

        IslandWars.getPlugin().config.setLocation("games." + game.getName() + ".team-1.spawn", player.getLocation());

        player.sendMessage("§aFirst spawn for game §2" + game.getName() + " §ahas been set!");
    }
}
