package de.marvinleiers.islandwars.commands;

import de.marvinleiers.islandwars.commands.subcommands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class IslandWarsAdminCommand implements CommandExecutor
{
    private ArrayList<SubCommand> subcommands = new ArrayList<>();

    public IslandWarsAdminCommand()
    {
        subcommands.add(new SetFirstSpawn());
        subcommands.add(new SetSecondSpawn());
        subcommands.add(new CreateSign());
        subcommands.add(new SetLobby());
        subcommands.add(new CreateFirstNetherStarCommand());
        subcommands.add(new CreateSecondNetherStarCommand());
        subcommands.add(new SetFirstDropoffCommand());
        subcommands.add(new SetSecondDropoffCommand());
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
            player.sendMessage("§c/iswarsadmin setfirstspawn <game>");
            player.sendMessage("§c/iswarsadmin setsecondspawn <game>");
            player.sendMessage("§c/iswarsadmin setfirststar <game>");
            player.sendMessage("§c/iswarsadmin setsecondstar <game>");
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
