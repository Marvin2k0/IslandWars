package de.marvinleiers.islandwars.commands;

import de.marvinleiers.gameapi.game.Game;
import de.marvinleiers.islandwars.IslandWars;
import de.marvinleiers.islandwars.utils.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage("§cOnly players can execute this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1)
        {
            player.sendMessage("§cUsage: /join <game>");
            return true;
        }

        if (!IslandWars.getPlugin().getApi().exists(args[0]))
        {
            player.sendMessage(Messages.get("message-game-does-not-exist"));
            return true;
        }

        if (IslandWars.getPlugin().getApi().inGame(player))
        {
            player.sendMessage(Messages.get("message-already-in-game"));
            return true;
        }

        Game game = IslandWars.getPlugin().getApi().getGame(args[0]);
        game.join(player);

        return true;
    }
}
