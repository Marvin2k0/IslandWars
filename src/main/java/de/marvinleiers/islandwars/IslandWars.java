package de.marvinleiers.islandwars;

import de.marvinleiers.gameapi.GameAPI;
import de.marvinleiers.gameapi.game.Game;
import de.marvinleiers.gameapi.utils.CustomConfig;
import de.marvinleiers.islandwars.commands.IslandWarsAdminCommand;
import de.marvinleiers.islandwars.commands.IslandWarsCommand;
import de.marvinleiers.islandwars.commands.JoinCommand;
import de.marvinleiers.islandwars.listeners.GameListener;
import de.marvinleiers.islandwars.listeners.SignListener;
import de.marvinleiers.islandwars.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class IslandWars extends JavaPlugin
{
    public HashMap<Game, Teams> teams = new HashMap<>();
    public CustomConfig config;
    public GameAPI api;

    @Override
    public void onEnable()
    {
        api = GameAPI.getInstance(this);
        Messages.setUp(this);

        config = new CustomConfig(getDataFolder().getPath() + "/games/games.yml");

        saveDefaultConfig();

        getCommand("iswars").setExecutor(new IslandWarsCommand());
        getCommand("iswarsadmin").setExecutor(new IslandWarsAdminCommand());
        getCommand("leave").setExecutor(this);
        getCommand("join").setExecutor(new JoinCommand());

        getServer().getPluginManager().registerEvents(new SignListener(), this);
        getServer().getPluginManager().registerEvents(new GameListener(), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage(Messages.get("message-no-player"));
            return true;
        }

        Bukkit.dispatchCommand(sender, "iswars leave");
        return true;
    }

    public GameAPI getApi()
    {
        return api;
    }

    public static IslandWars getPlugin()
    {
        return getPlugin(IslandWars.class);
    }
}
