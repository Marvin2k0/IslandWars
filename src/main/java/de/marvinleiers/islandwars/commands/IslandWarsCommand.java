package de.marvinleiers.islandwars.commands;

import de.marvinleiers.islandwars.commands.subcommands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class IslandWarsCommand implements CommandExecutor
{
    private ArrayList<SubCommand> subcommands = new ArrayList<>();

    public IslandWarsCommand()
    {
        subcommands.add(new LeaveCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0)
        {
            player.sendMessage("§c/iswars leave");
            return true;
        }

        for (SubCommand subCommand : subcommands)
        {
            if (subCommand.getName().equalsIgnoreCase(args[0]))
            {
                subCommand.execute(player, args);
                break;
            }
        }

        return true;
    }
}
