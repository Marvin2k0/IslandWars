package de.marvinleiers.islandwars.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class Messages
{
    static FileConfiguration config;
    static Plugin plugin;

    public static String get(String path)
    {
        return path.equalsIgnoreCase("prefix") ? get(path, false) : get(path, true);
    }

    public static String get(String path, boolean prefix)
    {
        return ChatColor.translateAlternateColorCodes('&', prefix ? config.getString("prefix") + " " + config.getString(path) : config.getString(path));
    }

    public static void setUp(Plugin plugin)
    {
        Messages.plugin = plugin;
        Messages.config = plugin.getConfig();

        config.options().copyDefaults(true);
        config.addDefault("prefix", "&b&l[ISWARS]&r");
        config.addDefault("message-no-player", "&cThis command is only for players!");
        config.addDefault("message-not-in-game", "&cYou are in no game!");
        config.addDefault("message-confirm-leave", "&7To &cconfirm &7please perform this command again!");
        config.addDefault("message-actionbar", "&6Wait %secs% more seconds!");
        config.addDefault("message-respawn-disabled", "&75 Minutes left. Respawns are now disabled!");
        config.addDefault("message-death-spectator", "&cYou died. Use /leave to leave");
        config.addDefault("message-stole-star", "&7%player% stole %team%'s §7nether star!");
        config.addDefault("message-runner-died", "The person who picked up %team%'s Nether Star has died!");
        config.addDefault("message-time", "&cTime ran out! Game ending...");
        config.addDefault("join-message", "&e%player% &7joined the game. (%c%/%m%)");
        config.addDefault("star-name-blue", "&9&lSTAR");
        config.addDefault("star-name-red", "&c&lSTAR");
        config.addDefault("min-players", 12);
        config.addDefault("max-players", 12);

        saveConfig();
    }

    private static void saveConfig()
    {
        plugin.saveConfig();
    }
}
