package me.prosl3nderman.procandrv4.shop;

import me.prosl3nderman.procandrv4.Database.ItemsTable;
import me.prosl3nderman.procandrv4.ProCandrV4;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_13_R2.ItemArmor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
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

public class ArmoryShopMenu implements Listener {

    public void openMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Shop " + ChatColor.BLACK + "|" + ChatColor.GOLD + " Armory");

        for (int i = 0; i < 9; i++)
            inv.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1));

        ItemsManager IM = new ItemsManager();
        String uuid = p.getUniqueId().toString();

        ItemStack armor = new ItemStack(Material.LEATHER_HELMET, 1);
        ItemMeta meta = armor.getItemMeta();
        if (!IM.playerOwnsItem(uuid, armor)) {
            meta.setDisplayName(ChatColor.RED + "Leather Helmet");
            meta.setLore(Arrays.asList(ChatColor.YELLOW + "Click to Buy", ChatColor.YELLOW + "Price: " + ChatColor.GREEN + "$" + IM.getPrice(armor)));
        } else {
            meta.setDisplayName(ChatColor.GREEN + "Leather Helmet");
            meta.setLore(Arrays.asList(ChatColor.YELLOW + "Click to Equip"));
        }
        armor.setItemMeta(meta);
        inv.setItem(0, armor);

        armor = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
        meta = armor.getItemMeta();
        if (!IM.playerOwnsItem(uuid, armor)) {
            meta.setDisplayName(ChatColor.RED + "Leather Chestplate");
            meta.setLore(Arrays.asList(ChatColor.YELLOW + "Click to Buy", ChatColor.YELLOW + "Price: " + ChatColor.GREEN + "$" + IM.getPrice(armor)));
        } else {
            meta.setDisplayName(ChatColor.GREEN + "Leather Chestplate");
            meta.setLore(Arrays.asList(ChatColor.YELLOW + "Click to Equip"));
        }
        armor.setItemMeta(meta);
        inv.setItem(1, armor);

        armor = new ItemStack(Material.LEATHER_LEGGINGS, 1);
        meta = armor.getItemMeta();
        if (!IM.playerOwnsItem(uuid, armor)) {
            meta.setDisplayName(ChatColor.RED + "Leather Leggings");
            meta.setLore(Arrays.asList(ChatColor.YELLOW + "Click to Buy", ChatColor.YELLOW + "Price: " + ChatColor.GREEN + "$" + IM.getPrice(armor)));
        } else {
            meta.setDisplayName(ChatColor.GREEN + "Leather Leggings");
            meta.setLore(Arrays.asList(ChatColor.YELLOW + "Click to Equip"));
        }
        armor.setItemMeta(meta);
        inv.setItem(2, armor);

        armor = new ItemStack(Material.LEATHER_BOOTS, 1);
        meta = armor.getItemMeta();
        if (!IM.playerOwnsItem(uuid, armor)) {
            meta.setDisplayName(ChatColor.RED + "Leather Boots");
            meta.setLore(Arrays.asList(ChatColor.YELLOW + "Click to Buy", ChatColor.YELLOW + "Price: " + ChatColor.GREEN + "$" + IM.getPrice(armor)));
        } else {
            meta.setDisplayName(ChatColor.GREEN + "Leather Boots");
            meta.setLore(Arrays.asList(ChatColor.YELLOW + "Click to Equip"));
        }
        armor.setItemMeta(meta);
        inv.setItem(3, armor);

        armor = new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1);
        meta = armor.getItemMeta();
        if (!IM.playerOwnsItem(uuid, armor)) {
            meta.setDisplayName(ChatColor.RED + "Chainmail Chestplate");
            meta.setLore(Arrays.asList(ChatColor.YELLOW + "Click to Buy", ChatColor.YELLOW + "Price: " + ChatColor.GREEN + "$" + IM.getPrice(armor)));
        } else {
            meta.setDisplayName(ChatColor.GREEN + "Chainmail Chestplate");
            meta.setLore(Arrays.asList(ChatColor.YELLOW + "Click to Equip"));
        }
        armor.setItemMeta(meta);
        inv.setItem(4, armor);

        armor = new ItemStack(Material.CHAINMAIL_LEGGINGS, 1);
        meta = armor.getItemMeta();
        if (!IM.playerOwnsItem(uuid, armor)) {
            meta.setDisplayName(ChatColor.RED + "Chainmail Leggings");
            meta.setLore(Arrays.asList(ChatColor.YELLOW + "Click to Buy", ChatColor.YELLOW + "Price: " + ChatColor.GREEN + "$" + IM.getPrice(armor)));
        } else {
            meta.setDisplayName(ChatColor.GREEN + "Chainmail Leggings");
            meta.setLore(Arrays.asList(ChatColor.YELLOW + "Click to Equip"));
        }
        armor.setItemMeta(meta);
        inv.setItem(5, armor);

        armor = new ItemStack(Material.TURTLE_HELMET, 1);
        meta = armor.getItemMeta();
        if (!IM.playerOwnsItem(uuid, armor)) {
            meta.setDisplayName(ChatColor.RED + "Turtle Helmet");
            meta.setLore(Arrays.asList(ChatColor.YELLOW + "Click to Buy", ChatColor.YELLOW + "Price: " + ChatColor.GREEN + "$" + IM.getPrice(armor)));
        } else {
            meta.setDisplayName(ChatColor.GREEN + "Turtle Helmet");
            meta.setLore(Arrays.asList(ChatColor.YELLOW + "Click to Equip"));
        }
        armor.setItemMeta(meta);
        inv.setItem(6, armor);

        p.openInventory(inv);
    }

    @EventHandler
    public void menuListener(InventoryClickEvent e) {
        if (e.getClickedInventory() == null)
            return;
        if (e.getClickedInventory().getSize() != 9)
            return;
        if (!e.getClickedInventory().getTitle().equalsIgnoreCase(ChatColor.GOLD + "Shop " + ChatColor.BLACK + "|" + ChatColor.GOLD + " Armory"))
            return;
        InventoryAction a = e.getAction();
        if ((a == InventoryAction.UNKNOWN) || (a == InventoryAction.NOTHING) || (e.getCurrentItem() == null) || (e.getCurrentItem().getType() == Material.AIR))
            return;
        e.setCancelled(true);
        ItemsManager IM = new ItemsManager();
        Player p = (Player) e.getWhoClicked();
        String uuid = p.getUniqueId().toString();
        ItemStack item = e.getCurrentItem();

        if (IM.playerOwnsItem(uuid, item))
            IM.equipItem(p, item);
        else {
            if (ProCandrV4.econ.getBalance(p) < Integer.parseInt(IM.getPrice(item))) {
                p.sendMessage(ChatColor.RED + "You do not have enough money for this item!");
                return;
            }
            ProCandrV4.econ.withdrawPlayer(p, Integer.parseInt(IM.getPrice(item)));
            IM.equipItem(p, item);
            new ItemsTable().addItem(p.getUniqueId().toString(), IM.getDBName(item));
            p.closeInventory();
            p.sendMessage(ChatColor.GOLD + "You have bought " + item.getItemMeta().getDisplayName() + ChatColor.GOLD + " for " + ChatColor.GREEN + "$" + IM.getPrice(item));
        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMoveItemIntoGUI(InventoryClickEvent e) {
        if (e.getClickedInventory() == null)
            return;
        if (e.getClickedInventory().getSize() != 27 && e.getClickedInventory().getType() != InventoryType.PLAYER)
            return;
        if (!e.getWhoClicked().getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase(ChatColor.GOLD + "Shop " + ChatColor.BLACK + "|" + ChatColor.GOLD + " Armory"))
            return;
        e.setCancelled(true);
    }
}
