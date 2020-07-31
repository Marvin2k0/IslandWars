package de.marvinleiers.islandwars.commands.subcommands;

import de.marvinleiers.gameapi.game.Game;
import de.marvinleiers.islandwars.IslandWars;
import de.marvinleiers.islandwars.utils.Messages;
import de.marvinleiers.islandwars.utils.Point;
import net.minecraft.server.v1_8_R3.EntityBat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftBat;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateFirstNetherStarCommand extends SubCommand
{
    @Override
    public String getName()
    {
        return "setfirststar";
    }

    @Override
    public String getDescription()
    {
        return "Sets the nether star for team one";
    }

    @Override
    public String getSyntax()
    {
        return "/iswarsadmin setfirststar <game>";
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

        IslandWars.getPlugin().config.setLocation("games." + game.getName() + ".team-1.star." + UUID.randomUUID().toString(), location);

        Item netherStar = location.getWorld().dropItem(location, new ItemStack(Material.NETHER_STAR));
        netherStar.setPickupDelay(Integer.MAX_VALUE);
        netherStar.setTicksLived(-32768);
        ArmorStand entity = (ArmorStand) location.getWorld().spawnEntity(location.subtract(0, 1, 0), EntityType.ARMOR_STAND);
        entity.setSmall(true);
        entity.setVisible(false);
        entity.setPassenger(netherStar);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, Integer.MAX_VALUE, false, false));

        /*
        ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation().getBlock().getLocation().clone().add(-0.17, 0, 0.10), EntityType.ARMOR_STAND);


        armorStand.setItemInHand(new ItemStack(Material.NETHER_STAR));
        armorStand.setArms(true);
        armorStand.setRightArmPose(new EulerAngle(Math.toRadians(270), Math.toRadians(320), Math.toRadians(0)));
        armorStand.setGravity(false);
        armorStand.setBasePlate(false);
        armorStand.setVisible(false);

        List<Point> locations = getLocations(0, 0, .5, 50);

        final int[] i = {0};
        final int[] tick = {0};

        new BukkitRunnable()
        {
            public void run()
            {
                tick[0]++;
                double t = ((double) tick[0] % 33.75) * Math.PI / 16.875;
                Vector v = new Vector(Math.cos(t), 0, Math.sin(t));
                v.multiply(.1);
                armorStand.setVelocity(v);
                float yaw = armorStand.getLocation().getYaw();
                yaw += 10.6666666666667;
                Location location = new Location(armorStand.getWorld(), armorStand.getLocation().getX(), armorStand.getLocation().getY(), armorStand.getLocation().getZ(), yaw, armorStand.getLocation().getPitch());
                armorStand.teleport(location.add(v));
                
                /*
                if (i[0] == locations.size())
                    i[0] = 0;

                Location circleLocation = new Location(armorStand.getWorld(), locations.get(i[0]).getX() + location.getX(), location.getY(), locations.get(i[0]).getZ() + location.getZ());

                circleLocation.setYaw(getAngle(new Vector(armorStand.getLocation().getX(), 0, armorStand.getLocation().getZ()), armorStand.getLocation().toVector()));
                System.out.println(circleLocation.getYaw());
                armorStand.teleport(circleLocation);

                i[0]++;

               /* float yaw = rotatingLoc.getYaw() + 4;
                System.out.println(yaw);

                if (yaw >= 180)
                    yaw *= -1;

                rotatingLoc.setYaw(yaw);

                armorStand.teleport(rotatingLoc);
            }
        }.runTaskTimer(IslandWars.getPlugin(), 0, 1);
         */

        player.sendMessage("§aStar for team one has been set!");
    }
}
