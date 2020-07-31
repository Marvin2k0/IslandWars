package de.marvinleiers.islandwars.commands.subcommands;

import de.marvinleiers.gameapi.game.Game;
import de.marvinleiers.islandwars.IslandWars;
import de.marvinleiers.islandwars.utils.Messages;
import net.minecraft.server.v1_8_R3.EntityBat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftBat;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.UUID;

public class CreateSecondNetherStarCommand extends SubCommand
{
    @Override
    public String getName()
    {
        return "setsecondstar";
    }

    @Override
    public String getDescription()
    {
        return "Sets the nether star for team two";
    }

    @Override
    public String getSyntax()
    {
        return "/iswarsadmin setsecondstar <game>";
    }

    @Override
    public void execute(Player player, String[] args)
    {
        if (args.length < 2)
        {
            player.sendMessage("§cUsage: /iswarsadmin setfirststar <game>");
            return;
        }

        Game game = IslandWars.getPlugin().getApi().getGame(args[1]);
        Location location = player.getLocation();

        IslandWars.getPlugin().config.setLocation("games." + game.getName() + ".team-2.star." + UUID.randomUUID().toString(), location);


        Item netherStar = location.getWorld().dropItem(location, new ItemStack(Material.NETHER_STAR));
        netherStar.setPickupDelay(Integer.MAX_VALUE);
        netherStar.setTicksLived(-32768);

        ArmorStand entity = (ArmorStand) location.getWorld().spawnEntity(location.subtract(0, 1, 0), EntityType.ARMOR_STAND);
        entity.setSmall(true);
        entity.setVisible(false);
        entity.setPassenger(netherStar);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, Integer.MAX_VALUE, false, false));

        player.sendMessage("§aStar for team two has been set!");
    }
}
