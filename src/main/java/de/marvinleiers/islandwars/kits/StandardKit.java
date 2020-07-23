package de.marvinleiers.islandwars.kits;

import de.marvinleiers.islandwars.IslandWars;
import de.marvinleiers.islandwars.Teams;
import de.marvinleiers.islandwars.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class StandardKit extends Kit
{
    private Teams teams;

    public StandardKit(Player player)
    {
        super("Standard", player);

        this.teams = IslandWars.getPlugin().teams.get(IslandWars.getPlugin().getApi().gameplayers.get(player).getGame());
    }

    @Override
    public void receiveItems(byte id)
    {
        getPlayer().getInventory().clear();
        getPlayer().getInventory().setArmorContents(null);

        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        sword.addUnsafeEnchantment(Enchantment.DURABILITY, 10);

        ItemStack wool = ItemUtils.create(Material.WOOL, id, "Wool");
        wool.setAmount(64);

        ItemStack helmet = new ItemStack(Material.IRON_HELMET);
        helmet.addUnsafeEnchantment(Enchantment.DURABILITY, 10);

        ItemStack chest = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        chest.addUnsafeEnchantment(Enchantment.DURABILITY, 10);

        ItemStack leggins = new ItemStack(Material.CHAINMAIL_LEGGINGS);
        leggins.addUnsafeEnchantment(Enchantment.DURABILITY, 10);

        ItemStack boots = new ItemStack(Material.CHAINMAIL_BOOTS);
        boots.addUnsafeEnchantment(Enchantment.DURABILITY, 10);

        getPlayer().getInventory().setItem(0, sword);

        for (int i = 0; i < 3; i++)
            getPlayer().getInventory().addItem(wool);

        getPlayer().getInventory().setHelmet(helmet);
        getPlayer().getInventory().setChestplate(chest);
        getPlayer().getInventory().setLeggings(leggins);
        getPlayer().getInventory().setBoots(boots);
    }
}
