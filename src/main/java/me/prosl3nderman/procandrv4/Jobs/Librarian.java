package me.prosl3nderman.procandrv4.Jobs;

import me.prosl3nderman.procandrv4.Game;
import me.prosl3nderman.procandrv4.MapsConfig;
import me.prosl3nderman.procandrv4.ProCandrV4;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Librarian extends Job {

    private Player p;
    private String map;
    private Boolean completed;

    public Librarian(Player p) {
        this.p = p;
        map = ProCandrV4.plugin.game.get(p.getName());
    }

    @Override
    public void setupJob() {
        ProCandrV4.plugin.games.get(map).setJob(p, this);

        p.getInventory().addItem(getBook("George Washington"));
        p.getInventory().addItem(getBook("Lightning Arc"));
        p.getInventory().addItem(getBook("Mineplex"));
        p.getInventory().addItem(getBook("God"));
        p.getInventory().addItem(getBook("Don't dig straight down!"));

        FileConfiguration cfg = ProCandrV4.plugin.getConfig();
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("messages.librarianRobbers.firstLine")) + " " + ChatColor.translateAlternateColorCodes('&', cfg.getString("messages.librarianRobbers.secondLine")));
        p.sendTitle(ChatColor.translateAlternateColorCodes('&', cfg.getString("titles.librarianRobbers.firstLine")), ChatColor.translateAlternateColorCodes('&', cfg.getString("titles.librarianRobbers.secondLine")), 0, 100, 40);
        /*
        p.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "You have been given the job of the Librarian! Make sure to fill up your exp bar by sorting the books in your book bag to get some extra money!");
        p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.ITALIC + "To sort the books, go to the library and right click the right category (sign) the book fits into. Right click the book cart to get 5 more " +
                "books.");
                */
    }

    @Override
    public void resetAndRemoveJob() { //resets the player's job and removes them
        ProCandrV4.plugin.games.get(map).removeJob(p);
    }

    @Override
    public void handleEvent(PlayerInteractEvent e) { //OnLibrarianEvents
        if (e.getHand() == EquipmentSlot.OFF_HAND)
            return;
        if ((e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.LEFT_CLICK_BLOCK) || (e.getClickedBlock().getType() != Material.SIGN && e.getClickedBlock().getType() != Material.WALL_SIGN))
            return;
        if (signIsBookCategory(e.getClickedBlock().getLocation()) == true) { //sign is book category, if not then proceed to doing refilling.
            if (e.getItem() == null || (e.getItem() != null && e.getItem().getType() != Material.WRITTEN_BOOK)) {
                p.sendMessage(ChatColor.RED + "You must hold a book in your hand to put a book in this category!");
                return;
            }
            Sign s = (Sign) e.getClickedBlock().getState();
            String category = s.getLine(0);
            String bookName = ChatColor.stripColor(e.getItem().getItemMeta().getDisplayName());
            if (bookName.equalsIgnoreCase("George Washington") || bookName.equalsIgnoreCase("Thomas Jefferson")) {
                if (!category.equalsIgnoreCase("Bibliography"))
                    p.sendMessage(ChatColor.RED + "Incorrect! This book is about someone.");
                else
                    takeBookAndGiveExp(p, e);
                return;
            }
            if (bookName.equalsIgnoreCase("Lightning Arc") || bookName.equalsIgnoreCase("Fire Spiral")) {
                if (!category.equalsIgnoreCase("Magic"))
                    p.sendMessage(ChatColor.RED + "Incorrect! This book is about a spell.");
                else
                    takeBookAndGiveExp(p, e);
                return;
            }
            if (bookName.equalsIgnoreCase("Mineplex") || bookName.equalsIgnoreCase("Cubecraft")) {
                if (!category.equalsIgnoreCase("History"))
                    p.sendMessage(ChatColor.RED + "Incorrect! This book is about a server that is long gone.");
                else
                    takeBookAndGiveExp(p, e);
                return;
            }
            if (bookName.equalsIgnoreCase("God") || bookName.equalsIgnoreCase("The earth is flat")) {
                if (!category.equalsIgnoreCase("Fiction") && !category.equalsIgnoreCase("Non-fiction")) {
                    if (bookName.equalsIgnoreCase("God"))
                        p.sendMessage(ChatColor.RED + "Incorrect! This book is about God being real.");
                    else
                        p.sendMessage(ChatColor.RED + "Incorrect! This book is about the earth being flat.");
                } else {
                    if (bookName.equalsIgnoreCase("God")) {
                        if (category.equalsIgnoreCase("Fiction"))
                            p.sendMessage(ChatColor.GOLD + "Hmmmm, interesting choice...");
                        else
                            p.sendMessage(ChatColor.GOLD + "Good choice :)");
                    } else {
                        if (category.equalsIgnoreCase("Fiction"))
                            p.sendMessage(ChatColor.GOLD + "Good choice :)");
                        else
                            p.sendMessage(ChatColor.GOLD + "Hmmmm, interesting choice...");
                    }
                    takeBookAndGiveExp(p, e);
                }
                return;
            }
            if (bookName.equalsIgnoreCase("Don't dig straight down!") || bookName.equalsIgnoreCase("Check out our discord!")) {
                if (!category.equalsIgnoreCase("Pro-Tips")) {
                    if (bookName.equalsIgnoreCase("Don't dig straight down!"))
                        p.sendMessage(ChatColor.RED + "Incorrect! This book is what you shouldn't do in Minecraft.");
                    else
                        p.sendMessage(ChatColor.RED + "Incorrect! This book is about suggesting you check out our discord.");
                } else
                    takeBookAndGiveExp(p, e);
                return;
            }
            return;
        }
        if (signIsBookCategoryNRefill(e.getClickedBlock().getLocation()) == false)
            return;
    }

    private void takeBookAndGiveExp(Player p, PlayerInteractEvent e) {
        p.getInventory().setItemInMainHand(null);
        p.sendMessage(ChatColor.GOLD + "Correct!");
        p.setLevel(p.getLevel() + 1);
        if (p.getLevel() == 5) {
            p.sendMessage(ChatColor.GOLD + "You have reached your librarian goal of 5!");
            p.setLevel(0);

            ItemStack key = new ItemStack(Material.TRIPWIRE_HOOK, 1);
            ItemMeta meta = key.getItemMeta();
            meta.setDisplayName(ChatColor.RED + "Red Key");
            meta.setLore(Arrays.asList(ChatColor.RED + "Use this key at the red door somewhere in the prison!"));
            key.setItemMeta(meta);

            p.getInventory().addItem(key);
            p.sendMessage(ChatColor.GOLD + "You have been given the red key to the red door somewhere in the prison!");
        }
    }

    private ItemStack getBook(String name) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
        ItemMeta meta = book.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + name);
        meta.setLore(Arrays.asList("Sort this book in the library by", "right clicking the correct genre sign."));
        book.setItemMeta(meta);
        return book;
    }

    private Boolean signIsBookCategory(Location loc) {
        if (ProCandrV4.plugin.getList("bookCategories", new MapsConfig(map).getConfig()).contains(ProCandrV4.plugin.getStringFLocation(loc, false)))
            return true;
        return false;
    }

    private Boolean signIsBookCategoryNRefill(Location loc) {
        FileConfiguration MC = new MapsConfig(map).getConfig();
        //if (MC.getString("bookRefillSign").equalsIgnoreCase(ProCandrV4.plugin.getStringFLocation(loc, false)))
            //return true;
        return false;
    }
}
