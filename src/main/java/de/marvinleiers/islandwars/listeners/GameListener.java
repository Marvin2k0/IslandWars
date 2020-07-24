package de.marvinleiers.islandwars.listeners;

import de.marvinleiers.gameapi.events.GameResetEvent;
import de.marvinleiers.gameapi.events.GameStartEvent;
import de.marvinleiers.gameapi.events.PlayerGameJoinEvent;
import de.marvinleiers.gameapi.game.Game;
import de.marvinleiers.gameapi.game.GamePlayer;
import de.marvinleiers.gameapi.utils.CountdownTimer;
import de.marvinleiers.islandwars.IslandWars;
import de.marvinleiers.islandwars.Teams;
import de.marvinleiers.islandwars.kits.Kit;
import de.marvinleiers.islandwars.kits.StandardKit;
import de.marvinleiers.islandwars.utils.Messages;
import de.marvinleiers.islandwars.utils.ResetBlock;
import net.minecraft.server.v1_8_R3.EntityBat;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftBat;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameListener implements Listener
{
    private HashMap<Game, CountdownTimer> timers = new HashMap<>();
    private HashMap<String, ArrayList<ResetBlock>> blocks = new HashMap<>();
    private ArrayList<Player> runners = new ArrayList<>();
    private HashMap<Player, Kit> kits = new HashMap<>();

    @EventHandler
    public void onPlace(BlockPlaceEvent event)
    {
        Player player = event.getPlayer();

        if (!IslandWars.getPlugin().getApi().inGame(player))
            return;

        Game game = IslandWars.getPlugin().getApi().gameplayers.get(player).getGame();
        ArrayList<ResetBlock> list = blocks.get(game.getName()) == null ? new ArrayList<>() : blocks.get(game.getName());

        list.add(new ResetBlock(event.getBlock().getLocation(), event.getBlock()));
        blocks.put(game.getName(), list);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event)
    {
        if (IslandWars.getPlugin().getApi().inGame((Player) event.getWhoClicked()))
        {
            if (event.getCursor().getType() == Material.NETHER_STAR)
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onReset(GameResetEvent event)
    {
        if (blocks.get(event.getGame().getName()) == null)
            return;

        for (ResetBlock block : blocks.get(event.getGame().getName()))
        {
            block.getLocation().getWorld().getBlockAt(block.getLocation()).setType(Material.AIR);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event)
    {
        Player player = event.getPlayer();

        if (!IslandWars.getPlugin().getApi().inGame(player))
            return;

        Game game = IslandWars.getPlugin().getApi().gameplayers.get(player).getGame();

        ArrayList<ResetBlock> list = blocks.get(game.getName());

        if (list == null)
        {
            event.setCancelled(true);
            return;
        }

        for (ResetBlock block : list)
        {
            if (block.getLocation().distance(event.getBlock().getLocation()) <= 0)
            {
                return;
            }
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event)
    {
        Player player = event.getEntity();

        if (!IslandWars.getPlugin().getApi().inGame(player))
            return;

        Game game = IslandWars.getPlugin().getApi().gameplayers.get(player).getGame();
        CountdownTimer timer = timers.get(game);

        if (runners.contains(player))
        {
            String team = IslandWars.getPlugin().teams.get(game).getFirstTeam().contains(player) ? "§cRed" : "§9Blue";

            game.sendMessage(Messages.get("message-runner-died").replace("%team%", team));
        }

        Location spawn = null;
        Teams team = IslandWars.getPlugin().teams.get(game);

        if (team.getFirstTeam().contains(player))
        {
            spawn = IslandWars.getPlugin().config.getLocation("games." + game.getName() + ".team-1.spawn");
        }
        else if (team.getSecondTeam().contains(player))
        {
            spawn = IslandWars.getPlugin().config.getLocation("games." + game.getName() + ".team-2.spawn");
        }

        player.setHealth(player.getMaxHealth());
        player.teleport(spawn);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        event.getDrops().clear();
        event.setDroppedExp(0);

        if (timer.getSecondsLeft() <= 5 * 60)
        {
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(Messages.get("message-death-spectator"));

            if (team.getFirstTeam().contains(player))
            {
                team.getFirstTeam().remove(player);

                if (team.getFirstTeam().size() <= 0)
                {
                    firstWin(game);
                    return;
                }
            }
            else if (team.getSecondTeam().contains(player))
            {
                team.getSecondTeam().remove(player);

                if (team.getSecondTeam().size() <= 0)
                {
                    secondWin(game);
                    return;
                }
            }
        }
        else
        {
            Bukkit.getScheduler().scheduleSyncDelayedTask(IslandWars.getPlugin(), () -> kits.get(player).receiveItems(IslandWars.getPlugin().teams.get(game).getFirstTeam().contains(player) ? (byte) 11 : (byte) 14), 5);
        }

        //TODO: get new kit inventory
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();

        if (player.getGameMode() != GameMode.SURVIVAL)
            return;

        if (!IslandWars.getPlugin().getApi().inGame(player))
            return;

        GamePlayer gp = IslandWars.getPlugin().getApi().gameplayers.get(player);
        Game game = gp.getGame();

        Teams teams = IslandWars.getPlugin().teams.get(game);

        if (teams == null)
            return;

        if (runners.contains(player))
        {
            return;
        }

        ArrayList<Location> enemyStar = new ArrayList<>();

        if (teams.getFirstTeam().contains(player))
        {
            for (Map.Entry<String, Object> entry : IslandWars.getPlugin().config.getConfig().getConfigurationSection("games." + game.getName() + ".team-2.star").getValues(false).entrySet())
            {
                enemyStar.add(IslandWars.getPlugin().config.getLocation("games." + game.getName() + ".team-2.star." + entry.getKey()));
            }
        }
        else if (teams.getSecondTeam().contains(player))
        {
            for (Map.Entry<String, Object> entry : IslandWars.getPlugin().config.getConfig().getConfigurationSection("games." + game.getName() + ".team-1.star").getValues(false).entrySet())
            {
                enemyStar.add(IslandWars.getPlugin().config.getLocation("games." + game.getName() + ".team-1.star." + entry.getKey()));
            }
        }

        for (Location stars : enemyStar)
        {
            double distance = player.getLocation().distance(stars);

            if (distance <= 3)
            {
                runners.add(player);

                new BukkitRunnable()
                {
                    int i = 0;

                    @Override
                    public void run()
                    {
                        if (i >= 3 && player.getLocation().distance(stars) > 3)
                        {
                            runners.remove(player);
                            this.cancel();
                            return;
                        }

                        if (player.getLocation().distance(stars) <= 3 && i >= 3)
                        {
                            pickUp(player, stars);
                            this.cancel();

                            new BukkitRunnable()
                            {
                                @Override
                                public void run()
                                {
                                    Location dropOff = null;

                                    if (teams.getFirstTeam().contains(player))
                                    {
                                        dropOff = IslandWars.getPlugin().config.getLocation("games." + gp.getGame().getName() + ".team-1.dropoff");
                                    }
                                    else if (teams.getSecondTeam().contains(player))
                                    {
                                        dropOff = IslandWars.getPlugin().config.getLocation("games." + gp.getGame().getName() + ".team-2.dropoff");
                                    }

                                    if (player.getLocation().distance(dropOff) <= 1)
                                    {
                                        this.cancel();
                                        runners.remove(player);

                                        if (teams.getFirstTeam().contains(player))
                                        {
                                            teams.reduceSecondStars();

                                            player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));

                                            if (teams.getSecondStarsLeft() <= 0)
                                            {
                                                firstWin(game);
                                            }
                                            else
                                            {
                                                game.sendMessage(Messages.get("message-stole-star").replace("%player%", player.getName()).replace("%team%", "§cRed"));
                                            }
                                        }
                                        else if (teams.getSecondTeam().contains(player))
                                        {
                                            teams.reduceFirstStars();

                                            player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));

                                            if (teams.getFirstStarsLeft() <= 0)
                                            {
                                                secondWin(game);
                                            }
                                            else
                                            {
                                                game.sendMessage(Messages.get("message-stole-star").replace("%player%", player.getName()).replace("%team%", "§9Blue"));
                                            }
                                        }
                                    }
                                }
                            }.runTaskTimer(IslandWars.getPlugin(), 0, 1);
                        }

                        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1, 1);
                        showActionbar(player, Messages.get("message-actionbar", false).replace("%secs%", (3 - i) + ""));

                        i++;
                    }
                }.runTaskTimer(IslandWars.getPlugin(), 0, 20);

                break;

                /*
                new BukkitRunnable()
                {
                    int i = 0;

                    @Override
                    public void run()
                    {
                        Location star = stars;

                        if (i >= 12 && player.getLocation().distance(star) <= 3)
                        {
                            runners.add(player);
                            pickUp(player, star);

                            if (runners.contains(player))
                            {
                                this.cancel();
                                return;
                            }

                            Location dropOff = null;

                            if (teams.getFirstTeam().contains(player))
                            {
                                dropOff = IslandWars.getPlugin().config.getLocation("games." + gp.getGame().getName() + ".team-1.dropoff");
                            }
                            else if (teams.getSecondTeam().contains(player))
                            {
                                dropOff = IslandWars.getPlugin().config.getLocation("games." + gp.getGame().getName() + ".team-2.dropoff");
                            }

                            if (player.getLocation().distance(dropOff) <= 2)
                            {
                                System.out.println(dropOff);

                                if (teams.getFirstTeam().contains(player))
                                {
                                    teams.reduceSecondStars();

                                    System.out.println(teams.getSecondStarsLeft());

                                    if (teams.getSecondStarsLeft() <= 0)
                                    {
                                        //TODO: first team won!
                                        System.out.println("firs team won!");
                                    }
                                }
                                else if (teams.getSecondTeam().contains(player))
                                {
                                    teams.reduceFirstStars();
                                    System.out.println(teams.getFirstStarsLeft());

                                    if (teams.getFirstStarsLeft() <= 0)
                                    {
                                        //TODO: second team won!
                                        System.out.println("second team won!");
                                    }
                                }
                            }

                            runners.remove(player);
                            this.cancel();
                            return;
                        }

                        player.spigot().sendMessage(TextComponent.fromLegacyText("§6" + i));

                        i++;
                    }
                }.runTaskTimer(IslandWars.getPlugin(), 0, 5);

                break;
                 */
            }
        }
    }

    private void firstWin(Game game)
    {
        for (Player all : game.players)
        {
            all.sendTitle("§7Team §9Blue", "§7won the game");
            all.playSound(all.getLocation(), Sound.LEVEL_UP, 1, 1);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(IslandWars.getPlugin(), () -> game.stop(), 100);
    }

    private void secondWin(Game game)
    {
        for (Player all : game.players)
        {
            all.sendTitle("§7Team §CRed", "§7won the game");
            all.playSound(all.getLocation(), Sound.LEVEL_UP, 1, 1);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(IslandWars.getPlugin(), () -> game.stop(), 100);
    }

    private void pickUp(Player player, Location star)
    {
        for (Entity entity : star.getWorld().getNearbyEntities(star, 2, 2, 2))
        {
            if (entity instanceof Item)
            {
                Item item = (Item) entity;

                if (item.getItemStack().getType() == Material.NETHER_STAR)
                {
                    item.remove();
                    player.getInventory().setHelmet(new ItemStack(Material.NETHER_STAR));
                }
            }
        }

        GamePlayer gp = IslandWars.getPlugin().getApi().gameplayers.get(player);

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (!runners.contains(player))
                {
                    this.cancel();
                    return;
                }

                player.getWorld().playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
            }
        }.runTaskTimer(IslandWars.getPlugin(), 0, 20);
    }

    @EventHandler
    public void onStart(GameStartEvent event)
    {
        if (event.getPlugin() != IslandWars.getPlugin())
            return;

        Game game = event.getGame();

        IslandWars.getPlugin().teams.put(game, new Teams(game));
        ArrayList<Location> starOne = new ArrayList<>();

        for (Map.Entry<String, Object> entry : IslandWars.getPlugin().config.getConfig().getConfigurationSection("games." + game.getName() + ".team-1.star").getValues(false).entrySet())
        {
            starOne.add(IslandWars.getPlugin().config.getLocation("games." + game.getName() + ".team-1.star." + entry.getKey()));
        }

        ArrayList<Location> starTwo = new ArrayList<>();

        for (Map.Entry<String, Object> entry : IslandWars.getPlugin().config.getConfig().getConfigurationSection("games." + game.getName() + ".team-2.star").getValues(false).entrySet())
        {
            starTwo.add(IslandWars.getPlugin().config.getLocation("games." + game.getName() + ".team-2.star." + entry.getKey()));
        }

        for (Location one : starOne)
        {
            if (one != null)
            {
                boolean found = false;

                for (Entity entity : one.getWorld().getNearbyEntities(one, 1, 1, 1))
                {
                    if (entity instanceof Item)
                    {
                        Item star = (Item) entity;

                        if (star.getItemStack().getType() == Material.NETHER_STAR)
                            found = true;
                    }
                }

                if (!found)
                {
                    Item star = one.getWorld().dropItem(one, new ItemStack(Material.NETHER_STAR));
                    star.setPickupDelay(Integer.MAX_VALUE);

                    ArmorStand entity = (ArmorStand) one.getWorld().spawnEntity(one.subtract(0, 1, 0), EntityType.ARMOR_STAND);
                    entity.setSmall(true);
                    entity.setVisible(false);
                    entity.setPassenger(star);
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false));
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, Integer.MAX_VALUE, false, false));
                }
            }
        }

        for (Location two : starTwo)
        {
            if (two != null)
            {
                boolean found = false;

                for (Entity entity : two.getWorld().getNearbyEntities(two, 1, 1, 1))
                {
                    if (entity instanceof Item)
                    {
                        Item star = (Item) entity;

                        if (star.getItemStack().getType() == Material.NETHER_STAR)
                            found = true;
                    }
                }

                if (!found)
                {
                    Item star = two.getWorld().dropItem(two, new ItemStack(Material.NETHER_STAR));
                    star.setPickupDelay(Integer.MAX_VALUE);

                    ArmorStand entity = (ArmorStand) two.getWorld().spawnEntity(two.subtract(0, 1, 0), EntityType.ARMOR_STAND);
                    entity.setSmall(true);
                    entity.setVisible(false);
                    entity.setPassenger(star);
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false));
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, Integer.MAX_VALUE, false, false));
                }
            }
        }

        for (Player player : game.players)
        {
            if (kits.containsKey(player))
            {
                kits.get(player).receiveItems(IslandWars.getPlugin().teams.get(game).getFirstTeam().contains(player) ? (byte) 11 : (byte) 14);
            }
        }

        CountdownTimer timer = new CountdownTimer(IslandWars.getPlugin(), 60 * 15,
                () -> {
                },
                () -> {
                    game.sendMessage(Messages.get("message-time"));
                    game.stop();
                },
                (t) -> {
                    if (t.getSecondsLeft() == 60 * 5)
                        game.sendMessage(Messages.get("message-respawn-disabled"));
                    else if (t.getSecondsLeft() <= 15 && t.getSecondsLeft() % 5 == 0)
                        game.sendMessage("§b" + t.getSecondsLeft() + " §7seconds left");
                    else if (t.getSecondsLeft() <= 60 && t.getSecondsLeft() % 15 == 0)
                        game.sendMessage("§b" + t.getSecondsLeft() + " §7seconds left");
                });

        timers.put(game, timer);
        timer.scheduleTimer();
    }

    @EventHandler
    public void onGameJoin(PlayerGameJoinEvent event)
    {
        Game game = event.getGame();
        Player player = event.getPlayer();

        if (!IslandWars.getPlugin().config.isSet("games." + game.getName() + ".team-1") || !IslandWars.getPlugin().config.isSet("games." + game.getName() + ".team-2"))
        {
            game.leave(player);
            player.sendMessage("§cNot all spawns set. Please contact staff!");
            return;
        }

        kits.put(player, new StandardKit(player));

        game.sendMessage(Messages.get("join-message").replace("%player%", player.getName()).replace("%c%", game.players.size() + "").replace("%m%", IslandWars.getPlugin().getConfig().getString("max-players")));
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event)
    {
        Player player = event.getPlayer();

        if (IslandWars.getPlugin().getApi().inGame(player))
            event.setCancelled(true);
    }

    private void showActionbar(Player player, String text)
    {
        PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text + "\"}"), (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
