package de.marvinleiers.islandwars.commands.subcommands;

import de.marvinleiers.gameapi.game.Game;
import de.marvinleiers.islandwars.IslandWars;
import org.bukkit.entity.Player;

public class SetSecondSpawn extends SubCommand
{
    @Override
    public String getName()
    {
        return "setsecondspawn";
    }

    @Override
    public String getDescription()
    {
        return "Set the island spawn for team two";
    }

    @Override
    public String getSyntax()
    {
        return "/iswarsadmin setsecondspawn <game>";
    }

    @Override
    public void execute(Player player, String[] args)
    {
        if (args.length < 2)
        {
            player.sendMessage("§cUsage: /iswarsadmin setsecondspawn <game>");
            return;
        }

        Game game = IslandWars.getPlugin().getApi().getGame(args[1]);

        IslandWars.getPlugin().config.setLocation("games." + game.getName() + ".team-2.spawn", player.getLocation());

        player.sendMessage("§aSecond spawn for game §2" + game.getName() + " §ahas been set!");
    }
}
