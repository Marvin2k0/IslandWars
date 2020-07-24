package de.marvinleiers.islandwars.commands.subcommands;

import de.marvinleiers.gameapi.game.Game;
import de.marvinleiers.islandwars.IslandWars;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class CreateSign extends SubCommand
{
    @Override
    public String getName()
    {
        return "sign";
    }

    @Override
    public String getDescription()
    {
        return "Create an information sign for a game.";
    }

    @Override
    public String getSyntax()
    {
        return "/iswarsadmin sign <game>";
    }

    @Override
    public void execute(Player player, String[] args)
    {
        Set<Material> nullMap = null;
        Block block = player.getTargetBlock(nullMap, 50);

        if (args.length != 2)
        {
            player.sendMessage("§cUsage: /islandwarsadmin sign <game>");
            return;
        }

        System.out.println(block.getType());

        if (!block.getType().toString().contains("SIGN"))
        {
            player.sendMessage("§cFor this operation you have to look at a sign!");
            return;
        }

        Sign sign = (Sign) block.getState();
        Game game = IslandWars.getPlugin().getApi().getGame(args[1]);
        game.addSign("IslandWars", sign.getLocation());
    }
}
