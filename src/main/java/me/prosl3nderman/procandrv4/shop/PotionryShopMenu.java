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
import org.bukkit.potion.Potion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PotionryShopMenu implements Listener {

    public void openMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Shop " + ChatColor.BLACK + "|" + ChatColor.GOLD + " Potionry");

        for (int i = 0; i < 27; i++)
            inv.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1));

        PotionsManager PM = new PotionsManager();
        String uuid = p.getUniqueId().toString();

        List<Integer> ints = Arrays.asList(5,2,21, 11,12,6, 15,3,14, 4,13, 22,20);
        List<ItemStack> potions = PM.getAllPotions();
        int order = 0;
        for (int slot : ints) {
            ItemStack pot = potions.get(order);
            ItemMeta meta = pot.getItemMeta();
            List<String> lore = new ArrayList<>();
            if (!PM.playerOwnsPotion(uuid, pot)) {
                lore.add(ChatColor.RED + "Click to Buy");
                lore.add(ChatColor.YELLOW + "Price: " + ChatColor.GREEN + "$" + PM.getPrice(pot));
            } else
                lore.add(ChatColor.GREEN + "Click to Equip");
            meta.setLore(lore);
            pot.setItemMeta(meta);

            inv.setItem(slot, pot);
            order++;
        }

        p.openInventory(inv);
    }

    @EventHandler
    public void menuListener(InventoryClickEvent e) {
        if (e.getClickedInventory() == null)
            return;
        if (e.getClickedInventory().getSize() != 27)
            return;
        if (!e.getClickedInventory().getTitle().equalsIgnoreCase(ChatColor.GOLD + "Shop " + ChatColor.BLACK + "|" + ChatColor.GOLD + " Potionry"))
            return;
        InventoryAction a = e.getAction();
        if ((a == InventoryAction.UNKNOWN) || (a == InventoryAction.NOTHING) || (e.getCurrentItem() == null) || (e.getCurrentItem().getType() == Material.AIR))
            return;
        e.setCancelled(true);
        PotionsManager PM = new PotionsManager();
        Player p = (Player) e.getWhoClicked();
        String uuid = p.getUniqueId().toString();
        ItemStack potion = e.getCurrentItem();
        ItemMeta meta = potion.getItemMeta();
        meta.setLore(null);
        potion.setItemMeta(meta);

        if (PM.playerOwnsPotion(uuid, potion))
            PM.equipPotion(p, potion);
        else {
            if (ProCandrV4.econ.getBalance(p) < Integer.parseInt(PM.getPrice(potion))) {
                p.sendMessage(ChatColor.RED + "You do not have enough money for this potion!");
                return;
            }
            ProCandrV4.econ.withdrawPlayer(p, Integer.parseInt(PM.getPrice(potion)));
            PM.equipPotion(p, potion);
            new ItemsTable().addPotion(p.getUniqueId().toString(), PM.getDBName(potion));
            p.closeInventory();
            p.sendMessage(ChatColor.GOLD + "You have bought " + potion.getItemMeta().getDisplayName() + ChatColor.GOLD + " for " + ChatColor.GREEN + "$" + PM.getPrice(potion));
        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMoveItemIntoGUI(InventoryClickEvent e) {
        if (e.getClickedInventory() == null)
            return;
        if (e.getClickedInventory().getSize() != 27 && e.getClickedInventory().getType() != InventoryType.PLAYER)
            return;
        if (!e.getWhoClicked().getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase(ChatColor.GOLD + "Shop " + ChatColor.BLACK + "|" + ChatColor.GOLD + " Potionry"))
            return;
        e.setCancelled(true);
    }
}
