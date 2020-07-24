package de.marvinleiers.islandwars.commands.subcommands;

import de.marvinleiers.gameapi.game.Game;
import de.marvinleiers.islandwars.IslandWars;
import org.bukkit.entity.Player;

public class SetLobby extends SubCommand
{
    @Override
    public String getName()
    {
        return "setlobby";
    }

    @Override
    public String getDescription()
    {
        return "Set the entry point for a game";
    }

    @Override
    public String getSyntax()
    {
        return "/iswarsadmin setlobby <game>";
    }

    @Override
    public void execute(Player player, String[] args)
    {
        if (args.length < 2)
        {
            player.sendMessage("§cUsage: /iswarsadmin setlobby <game>");
            return;
        }

        Game game = IslandWars.getPlugin().getApi().getGame(args[1]);
        game.setEntryPoint(player.getLocation());
        player.sendMessage("§aEntry point for game §2" + game.getName() + " §ahas been set!");
    }
}
