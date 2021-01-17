package me.prosl3nderman.procandrv4.shop;

import me.prosl3nderman.procandrv4.Database.ItemsTable;
import me.prosl3nderman.procandrv4.ProCandrV4;
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

public class WeaponryShopMenu implements Listener {

    public void openMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Shop " + ChatColor.BLACK + "|" + ChatColor.GOLD + " Weaponry");

        for (int i = 0; i < 9; i++)
            inv.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1));

        ItemsManager IM = new ItemsManager();
        String uuid = p.getUniqueId().toString();

        ItemStack weapon = new ItemStack(Material.WOODEN_PICKAXE, 1);
        ItemMeta meta = weapon.getItemMeta();
        if (!IM.playerOwnsItem(uuid, weapon)) {
            meta.setDisplayName(ChatColor.RED + "Wooden Pickaxe");
            meta.setLore(Arrays.asList(ChatColor.YELLOW + "Click to Buy", ChatColor.YELLOW + "Price: " + ChatColor.GREEN + "$" + IM.getPrice(weapon)));
        } else {
            meta.setDisplayName(ChatColor.GREEN + "Wooden Pickaxe");
            meta.setLore(Arrays.asList(ChatColor.YELLOW + "Click to Equip"));
        }
        weapon.setItemMeta(meta);
        inv.setItem(0, weapon);

        weapon = new ItemStack(Material.WOODEN_SHOVEL, 1);
        meta = weapon.getItemMeta();
        if (!IM.playerOwnsItem(uuid, weapon)) {
            meta.setDisplayName(ChatColor.RED + "Wooden Shovel");
            meta.setLore(Arrays.asList(ChatColor.YELLOW + "Click to Buy", ChatColor.YELLOW + "Price: " + ChatColor.GREEN + "$" + IM.getPrice(weapon)));
        } else {
            meta.setDisplayName(ChatColor.GREEN + "Wooden Shovel");
            meta.setLore(Arrays.asList(ChatColor.YELLOW + "Click to Equip"));
        }
        weapon.setItemMeta(meta);
        inv.setItem(1, weapon);

        weapon = new ItemStack(Material.STONE_PICKAXE, 1);
        meta = weapon.getItemMeta();
        if (!IM.playerOwnsItem(uuid, weapon)) {
            meta.setDisplayName(ChatColor.RED + "Stone Pickaxe");
            meta.setLore(Arrays.asList(ChatColor.YELLOW + "Click to Buy", ChatColor.YELLOW + "Price: " + ChatColor.GREEN + "$" + IM.getPrice(weapon)));
        } else {
            meta.setDisplayName(ChatColor.GREEN + "Stone Pickaxe");
            meta.setLore(Arrays.asList(ChatColor.YELLOW + "Click to Equip"));
        }
        weapon.setItemMeta(meta);
        inv.setItem(2, weapon);

        weapon = new ItemStack(Material.STONE_SHOVEL, 1);
        meta = weapon.getItemMeta();
        if (!IM.playerOwnsItem(uuid, weapon)) {
            meta.setDisplayName(ChatColor.RED + "Stone Shovel");
            meta.setLore(Arrays.asList(ChatColor.YELLOW + "Click to Buy", ChatColor.YELLOW + "Price: " + ChatColor.GREEN + "$" + IM.getPrice(weapon)));
        } else {
            meta.setDisplayName(ChatColor.GREEN + "Stone Shovel");
            meta.setLore(Arrays.asList(ChatColor.YELLOW + "Click to Equip"));
        }
        weapon.setItemMeta(meta);
        inv.setItem(3, weapon);

        p.openInventory(inv);
    }

    @EventHandler
    public void menuListener(InventoryClickEvent e) {
        if (e.getClickedInventory() == null)
            return;
        if (e.getClickedInventory().getSize() != 9)
            return;
        if (!e.getClickedInventory().getTitle().equalsIgnoreCase(ChatColor.GOLD + "Shop " + ChatColor.BLACK + "|" + ChatColor.GOLD + " Weaponry"))
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
        if (!e.getWhoClicked().getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase(ChatColor.GOLD + "Shop " + ChatColor.BLACK + "|" + ChatColor.GOLD + " Weaponry"))
            return;
        e.setCancelled(true);
    }
}
