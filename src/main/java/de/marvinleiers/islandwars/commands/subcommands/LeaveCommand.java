package de.marvinleiers.islandwars.commands.subcommands;

import de.marvinleiers.gameapi.game.Game;
import de.marvinleiers.islandwars.IslandWars;
import de.marvinleiers.islandwars.utils.Messages;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class LeaveCommand extends SubCommand
{
    private ArrayList<Player> confirm = new ArrayList<>();

    @Override
    public String getName()
    {
        return "leave";
    }

    @Override
    public String getDescription()
    {
        return "Leave a IslandWars game";
    }

    @Override
    public String getSyntax()
    {
        return null;
    }

    @Override
    public void execute(Player player, String[] args)
    {
        if (IslandWars.getPlugin().getApi().inGame(player))
        {
            if (!confirm.contains(player))
            {
                player.sendMessage(Messages.get("message-confirm-leave"));
                confirm.add(player);
                return;

            }

            confirm.remove(player);

            Game game = IslandWars.getPlugin().getApi().gameplayers.get(player).getGame();
            game.leave(player);
        }
        else
        {
            player.sendMessage(Messages.get("message-not-in-game"));
        }
    }
}
