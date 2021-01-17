package me.prosl3nderman.procandrv4.shop;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class MainShopMenu implements Listener {

    public void giveItem(Player p) {
        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Shop");
        item.setItemMeta(itemMeta);
        if (p.getInventory().getItem(8) != null && p.getInventory().getItem(8).getType() != Material.EMERALD) {
            ItemStack oldItem = p.getInventory().getItem(8);
            p.getInventory().setItem(8, item);
            p.getInventory().addItem(oldItem);
        } else
            p.getInventory().setItem(8, item);
        p.updateInventory();
    }

    public void openMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, 36, ChatColor.GOLD + "Shop");

        for (int i = 0; i < 36; i++)
            inv.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1));

        ItemStack armor = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
        ItemMeta meta = armor.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE + "Armory");
        meta.setLore(Arrays.asList(ChatColor.YELLOW + "Buy armor for time in the joint."));
        armor.setItemMeta(meta);

        ItemStack sword = new ItemStack(Material.WOODEN_SWORD, 1);
        meta = sword.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE + "Weaponry");
        meta.setLore(Arrays.asList(ChatColor.YELLOW + "Buy weapons for shanking..?"));
        sword.setItemMeta(meta);

        ItemStack potion = new ItemStack(Material.POTION);
        meta = potion.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE + "Potionry");
        meta.setLore(Arrays.asList(ChatColor.YELLOW + "Buy potions for benefits."));
        potion.setItemMeta(meta);

        inv.setItem(11, armor);
        inv.setItem(13, sword);
        inv.setItem(15, potion);

        p.openInventory(inv);
    }

    @EventHandler
    public void menuListener(InventoryClickEvent e) {
        if (e.getClickedInventory() == null)
            return;
        if (e.getClickedInventory().getSize() != 36)
            return;
        if (!e.getClickedInventory().getTitle().equalsIgnoreCase(ChatColor.GOLD + "Shop"))
            return;
        InventoryAction a = e.getAction();
        if ((a == InventoryAction.UNKNOWN) || (a == InventoryAction.NOTHING) || (e.getCurrentItem() == null) || (e.getCurrentItem().getType() == Material.AIR))
            return;
        e.setCancelled(true);

        if (e.getCurrentItem().getType() == Material.LEATHER_CHESTPLATE) {
            e.getWhoClicked().closeInventory();
            new ArmoryShopMenu().openMenu((Player) e.getWhoClicked());
            return;
        }
        if (e.getCurrentItem().getType() == Material.WOODEN_SWORD) {
            e.getWhoClicked().closeInventory();
            new WeaponryShopMenu().openMenu((Player) e.getWhoClicked());
            return;
        }
        if (e.getCurrentItem().getType() == Material.POTION) {
            e.getWhoClicked().closeInventory();
            new PotionryShopMenu().openMenu((Player) e.getWhoClicked());
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMoveItemIntoGUI(InventoryClickEvent e) {
        if (e.getClickedInventory() == null)
            return;
        if (e.getClickedInventory().getSize() != 27 && e.getClickedInventory().getType() != InventoryType.PLAYER)
            return;
        if (!e.getWhoClicked().getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase(org.bukkit.ChatColor.GREEN + "Chat Color"))
            return;
        e.setCancelled(true);
    }
}
