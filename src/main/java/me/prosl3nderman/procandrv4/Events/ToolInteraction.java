package me.prosl3nderman.procandrv4.Events;

import me.prosl3nderman.procandrv4.Game;
import me.prosl3nderman.procandrv4.MapsConfig;
import me.prosl3nderman.procandrv4.ProCandrV4;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ToolInteraction implements Listener {

    HashMap<String, String> pressurePlate = new HashMap<>(); //String = player name; String = pressure plate location.

    @EventHandler
    public void onChangeTypeMapSetupTool(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND)
            return;
        Player p = e.getPlayer();
        if (!p.hasPermission("ProCandr.admin"))
            return;
        if (e.getAction() != Action.LEFT_CLICK_AIR && e.getAction() != Action.LEFT_CLICK_BLOCK)
            return;
        if (p.getInventory().getItemInMainHand().getType() != Material.DIAMOND_HOE)
            return;
        if (!e.getItem().hasItemMeta() && !e.getItem().getItemMeta().hasDisplayName() && !e.getItem().getItemMeta().hasLore())
            return;

        //Cell Spawnpoints, Door Locations, and Cop Spawnpoint
        String type = e.getItem().getItemMeta().getDisplayName();
        if (type.equalsIgnoreCase("Cell Spawnpoints"))
            type = "Door Locations";
        else if (type.equalsIgnoreCase("Door Locations"))
            type = "Cop Spawnpoint";
        else if (type.equalsIgnoreCase("Cop Spawnpoint"))
            type = "Lever";
        else if (type.equalsIgnoreCase("Lever"))
            type = "Block Update";
        else if (type.equalsIgnoreCase("Block Update"))
            type = "Engine Sign";
        else if (type.equalsIgnoreCase("Engine Sign"))
            type = "Escape Region Block 1";
        else if (type.equalsIgnoreCase("Escape Region Block 1"))
            type = "Escape Region Block 2";
        else if (type.equalsIgnoreCase("Escape Region Block 2"))
            type = "Join Sign";
        else if (type.equalsIgnoreCase("Join Sign"))
            type = "Staff Access";
        else if (type.equalsIgnoreCase("Staff Access"))
            type = "Dirt And Garbage Spots";
        else if (type.equalsIgnoreCase("Dirt And Garbage Spots"))
            type = "Freezer";
        else if (type.equalsIgnoreCase("Freezer"))
            type = "Food Trays";
        else if (type.equalsIgnoreCase("Food Trays"))
            type = "Furnaces";
        else if (type.equalsIgnoreCase("Furnaces"))
            type = "Book Category";
        else if (type.equalsIgnoreCase("Book Category"))
            type = "Book Refill";
        else if (type.equalsIgnoreCase("Book Refill")) {
            ItemStack tool = p.getInventory().getItemInMainHand();
            ItemMeta meta = tool.getItemMeta();

            MapsConfig MC = new MapsConfig(meta.getLore().get(0));

            type = "Bed Foot";
            if (!MC.getConfig().contains("beds"))
                meta.setLore(Arrays.asList(meta.getLore().get(0), "bed" + 1));
            else
                meta.setLore(Arrays.asList(meta.getLore().get(0), "bed" + (MC.getConfig().getConfigurationSection("beds").getKeys(false).size() + 1)));
            tool.setItemMeta(meta);
            p.sendMessage(ChatColor.GOLD + "Your tool setting has been changed to 'Bed Foot', and the next bed to be set is in the lore of your tool on the 2nd line!");
        } else if (type.equalsIgnoreCase("Bed Foot")) {
            ItemStack tool = p.getInventory().getItemInMainHand();
            ItemMeta meta = tool.getItemMeta();
            MapsConfig MC = new MapsConfig(meta.getLore().get(0));
            if (!MC.getConfig().contains("beds." + meta.getLore().get(1))) {
                type = "Washer/Dryer";
                p.sendMessage(ChatColor.UNDERLINE.RED + "Cannot set 'Bead Head' before 'Bead Foot'! Skipping from 'Bed Head' setting to 'Washer/Dryer'!");
            } else
                type = "Bed Head";
        } else if (type.equalsIgnoreCase("Bed Head"))
            type = "Washer/Dryer";
        else if (type.equalsIgnoreCase("Washer/Dryer"))
            type = "Janitor Door Location";
        else if (type.equalsIgnoreCase("Janitor Door Location"))
            type = "Janitor Door Teleport Destination";
        else if (type.equalsIgnoreCase("Janitor Door Teleport Destination"))
            type = "Librarian Door Location";
        else if (type.equalsIgnoreCase("Librarian Door Location"))
            type = "Librarian Door Teleport Destination";
        else if (type.equalsIgnoreCase("Librarian Door Teleport Destination"))
            type = "Cook Door Location";
        else if (type.equalsIgnoreCase("Cook Door Location"))
            type = "Cook Door Teleport Destination";
        else if (type.equalsIgnoreCase("Cook Door Teleport Destination"))
            type = "Laundryman Door Location";
        else if (type.equalsIgnoreCase("Laundryman Door Location"))
            type = "Laundryman Door Teleport Destination";
        else {
            type = "Cell Spawnpoints";
            ItemStack tool = p.getInventory().getItemInMainHand();
            ItemMeta meta = tool.getItemMeta();
            meta.setLore(Arrays.asList(meta.getLore().get(0)));
            tool.setItemMeta(meta);
        }
        ItemMeta meta = p.getInventory().getItemInMainHand().getItemMeta();
        meta.setDisplayName(type);
        p.getInventory().getItemInMainHand().setItemMeta(meta);
    }

    @EventHandler
    public void onMapSetup(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND)
            return;
        Player p = e.getPlayer();
        if (!p.hasPermission("ProCandr.admin"))
            return;
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (p.getInventory().getItemInMainHand().getType() != Material.DIAMOND_HOE)
            return;
        if (!e.getItem().hasItemMeta() && !e.getItem().getItemMeta().hasDisplayName() && !e.getItem().getItemMeta().hasLore())
            return;

        //Cell Spawnpoints, Door Locations, and Cop Spawnpoint
        String type = e.getItem().getItemMeta().getDisplayName();
        String map = e.getItem().getItemMeta().getLore().get(0);
        MapsConfig MC = new MapsConfig(map);
        if (type.equalsIgnoreCase("Cell Spawnpoints")) {
            if (MC.getConfig().contains("cells.spawnpoints") && MC.getConfig().getStringList("cells.spawnpoints").contains(ProCandrV4.plugin.getStringFLocation(p.getLocation(), true))) {
                p.sendMessage(ChatColor.RED + "Duplicate, not saving...");
                return;
            }
            List<String> spawns = getList("cells.spawnpoints", MC);
            spawns.add(ProCandrV4.plugin.getStringFLocation(p.getLocation(), true));
            MC.getConfig().set("cells.spawnpoints", spawns);
            MC.srConfig();
            p.sendMessage(ChatColor.GOLD + "Cell spawnpoint number " + ChatColor.WHITE + MC.getConfig().getStringList("cells.spawnpoints").size() + ChatColor.GOLD + " has been set!");
        } else if (type.equalsIgnoreCase("Door Locations")) {
            if (MC.getConfig().contains("cells.doors") && MC.getConfig().getStringList("cells.doors").contains(ProCandrV4.plugin.getStringFLocation(p.getLocation(), false))) {
                p.sendMessage(ChatColor.RED + "Duplicate, not saving...");
                return;
            }
            List<String> doors = getList("cells.doors", MC);
            doors.add(ProCandrV4.plugin.getStringFLocation(p.getLocation(), false));
            MC.getConfig().set("cells.doors", doors);
            MC.srConfig();
            p.sendMessage(ChatColor.GOLD + "Cell door number " + ChatColor.WHITE + MC.getConfig().getStringList("cells.doors").size() + ChatColor.GOLD + " has been set!");
        } else if (type.equalsIgnoreCase("Cop Spawnpoint")) {
            MC.getConfig().set("copspawnloc", ProCandrV4.plugin.getStringFLocation(p.getLocation(), true));
            MC.srConfig();
            p.sendMessage(ChatColor.GOLD + "Cop spawnpoint has been set.");
        } else if (type.equalsIgnoreCase("Lever")) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR)
                p.sendMessage(ChatColor.RED + "To add a lever to the list of levers to reset at the end of the round, you must right click with this tool!");
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (e.getClickedBlock().getType() != Material.LEVER) {
                    p.sendMessage(ChatColor.RED + "The right click blocked must be a lever!");
                    return;
                }
                e.setCancelled(true);
                List<String> levers = getList("levers", MC);
                levers.add(ProCandrV4.plugin.getStringFLocation(e.getClickedBlock().getLocation(), false));
                MC.getConfig().set("levers", levers);
                MC.srConfig();
                p.sendMessage(ChatColor.GOLD + "The lever you just right click has been added to the lever list that gets reset every round.");
            }
        } else if (type.equalsIgnoreCase("Block Update")) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR) {
                p.sendMessage(ChatColor.RED + "To add a block that will send updates to redstone around it, right click the ground below it.");
                return;
            }
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                List<String> levers = getList("bupdates", MC);
                levers.add(ProCandrV4.plugin.getStringFLocation(e.getClickedBlock().getLocation().clone().add(0,1,0), false));
                MC.getConfig().set("bupdates", levers);
                MC.srConfig();
                p.sendMessage(ChatColor.GOLD + "The block above what you just right clicked has been added to the block update list that gets sent at the end of every round.");
            }
        } else if (type.equalsIgnoreCase("Engine Sign")) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR) {
                p.sendMessage(ChatColor.RED + "To add the engine sign, aka the winning sign, right click a sign with this tool.");
                return;
            }
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                MC.getConfig().set("engineSign", ProCandrV4.plugin.getStringFLocation(e.getClickedBlock().getLocation(), false));
                MC.srConfig();
                p.sendMessage(ChatColor.GOLD + "The sign you just right clicked is now the engine sign.");
            }
        } else if (type.equalsIgnoreCase("Escape Region Block 1")) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR) {
                p.sendMessage(ChatColor.RED + "To set the first region block, must right click a block and not the air!");
                return;
            }
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                MC.getConfig().set("escapeRegionBlock1", ProCandrV4.plugin.getStringFLocation(e.getClickedBlock().getLocation(), false));
                MC.srConfig();
                p.sendMessage(ChatColor.GOLD + "The first region block has been set to the block you just clicked!");
            }
        } else if (type.equalsIgnoreCase("Escape Region Block 2")) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR) {
                p.sendMessage(ChatColor.RED + "To set the second region block, must right click a block and not the air!");
                return;
            }
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                MC.getConfig().set("escapeRegionBlock2", ProCandrV4.plugin.getStringFLocation(e.getClickedBlock().getLocation(), false));
                MC.srConfig();
                p.sendMessage(ChatColor.GOLD + "The second region block has been set to the block you just clicked!");
            }
        } else if (type.equalsIgnoreCase("Join Sign")) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR) {
                p.sendMessage(ChatColor.RED + "To add the join sign right click a sign with this tool.");
                return;
            }
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                MC.getConfig().set("joinSign", ProCandrV4.plugin.getStringFLocation(e.getClickedBlock().getLocation(), false));
                MC.srConfig();
                p.sendMessage(ChatColor.GOLD + "The sign you just right clicked is now the join sign.");
                new Game(map).updateJoinSign();
            }
        } else if (type.equalsIgnoreCase("Staff Access")) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR) {
                p.sendMessage(ChatColor.RED + "To add a staff access point, right click the pressure plate then right click while standing in the destination spot.");
                return;
            }
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (pressurePlate.containsKey(p.getName())) {
                    MC.getConfig().set("staffAccess." + pressurePlate.get(p.getName()), ProCandrV4.plugin.getStringFLocation(p.getLocation(), true));
                    MC.srConfig();
                    p.sendMessage(ChatColor.GOLD + "The location you are standing in was linked to the pressure plate clicked before.");
                    pressurePlate.remove(p.getName());
                    return;
                }
                pressurePlate.put(p.getName(), ProCandrV4.plugin.getStringFLocation(e.getClickedBlock().getLocation(), false));
                p.sendMessage(ChatColor.GOLD + "The pressure plate you just right clicked has been selected as a staff access point. Now, go to the location it is suppose to take you and right click using the " +
                        "Staff Access setting.");
            }
        } else if (type.equalsIgnoreCase("Dirt And Garbage Spots")) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR) {
                p.sendMessage(ChatColor.RED + "To add a spot for dirt and garbage to spawn, right click the block below the desired spot.");
                return;
            }
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                List<String> levers = getList("dirtAndGarbageSpots", MC);
                levers.add(ProCandrV4.plugin.getStringFLocation(e.getClickedBlock().getLocation().clone().add(0,1,0), false));
                MC.getConfig().set("dirtAndGarbageSpots", levers);
                MC.srConfig();
                p.sendMessage(ChatColor.GOLD + "The block above what you just right clicked has been added as a spot where dirt and garbage spawns.");
            }
        } else if (type.equalsIgnoreCase("Freezer")) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR)
                p.sendMessage(ChatColor.RED + "To add a freezer, right click a sign attached to the freezer (suggest that the freezer be a cauldron with an iron trapdoor on top)");
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (e.getClickedBlock().getType() != Material.SIGN && e.getClickedBlock().getType() != Material.WALL_SIGN) {
                    p.sendMessage(ChatColor.RED + "The right click blocked must be a sign attached to the freezer!");
                    return;
                }
                List<String> freezers = getList("freezers", MC);
                freezers.add(ProCandrV4.plugin.getStringFLocation(e.getClickedBlock().getLocation(), false));
                MC.getConfig().set("freezers", freezers);
                MC.srConfig();
                p.sendMessage(ChatColor.GOLD + "The sign you just right clicked became a freezer for the cook to use.");
                Sign s = (Sign) e.getClickedBlock().getState();
                s.setLine(0, ChatColor.BLUE + "[Freezer]");
                s.setLine(1, "Click Here");
                s.setLine(2, "Have your gloves");
                s.setLine(3, "out!");
                s.update();
            }
        } else if (type.equalsIgnoreCase("Food Trays")) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR)
                p.sendMessage(ChatColor.RED + "To add a freezer, right click an iron trapdoor.");
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (e.getClickedBlock().getType() != Material.IRON_TRAPDOOR) {
                    p.sendMessage(ChatColor.RED + "The right click blocked must be an iron trapdoor!");
                    return;
                }
                List<String> foodTrays = getList("foodTrays", MC);
                foodTrays.add(ProCandrV4.plugin.getStringFLocation(e.getClickedBlock().getLocation(), false));
                MC.getConfig().set("foodTrays", foodTrays);
                MC.srConfig();
                p.sendMessage(ChatColor.GOLD + "The iron trapdoor you just right clicked became a food tray for the cook to use.");
            }
        } else if (type.equalsIgnoreCase("Furnaces")) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR)
                p.sendMessage(ChatColor.RED + "To add a furnace, right click a furnace.");
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (e.getClickedBlock().getType() != Material.FURNACE) {
                    p.sendMessage(ChatColor.RED + "The right click blocked must be a furnace!");
                    return;
                }
                List<String> furnaces = getList("furnaces", MC);
                furnaces.add(ProCandrV4.plugin.getStringFLocation(e.getClickedBlock().getLocation(), false));
                MC.getConfig().set("furnaces", furnaces);
                MC.srConfig();
                p.sendMessage(ChatColor.GOLD + "The furnace you just right clicked became a furnace for the cook to use.");
            }
        } else if (type.equalsIgnoreCase("Book Category")) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR)
                p.sendMessage(ChatColor.RED + "To add a book category, right click a sign. Make sure to label the sign History, Bibliography, Pro-Tips, Magic, Non-Fiction, and Fiction.");
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (e.getClickedBlock().getType() != Material.SIGN && e.getClickedBlock().getType() != Material.WALL_SIGN) {
                    p.sendMessage(ChatColor.RED + "The right clicked block must be a sign!");
                    return;
                }
                List<String> bookCategories = getList("bookCategories", MC);
                bookCategories.add(ProCandrV4.plugin.getStringFLocation(e.getClickedBlock().getLocation(), false));
                MC.getConfig().set("bookCategories", bookCategories);
                MC.srConfig();
                p.sendMessage(ChatColor.GOLD + "The sign you just right clicked became a book category the librarian can use. Make sure to label the sign History, Bibliography, Pro-Tips, Magic, Non-Fiction, and " +
                        "Fiction.");
            }
        } else if (type.equalsIgnoreCase("Book Refill")) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR) {
                p.sendMessage(ChatColor.RED + "To add the book refill sign right click a sign with this tool.");
                return;
            }
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                MC.getConfig().set("bookRefillSign", ProCandrV4.plugin.getStringFLocation(e.getClickedBlock().getLocation(), false));
                MC.srConfig();
                p.sendMessage(ChatColor.GOLD + "The sign you just right clicked is now the book refill sign.");

                Sign s = (Sign) e.getClickedBlock().getState();
                s.setLine(0, ChatColor.BLUE + "[Book Refill]");
                s.setLine(1, "Librarian click");
                s.setLine(2, "here to refill.");
                s.update();
            }
        } else if (type.equalsIgnoreCase("Bed Foot")) {
            if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
                p.sendMessage(ChatColor.RED + "Must click a bed's foot block to add a bed foot.");
                return;
            }
            if (!e.getClickedBlock().getType().toString().contains("_BED")) {
                p.sendMessage(ChatColor.RED + "Must click a bed's foot block to add a bed foot.");
                return;
            }
            if (e.getClickedBlock().getBlockData().getAsString().contains("head")) {
                p.sendMessage(ChatColor.RED + "You must set the foot block first, then the head block afterwards!");
                return;
            }
            String bed = e.getItem().getItemMeta().getLore().get(1);
            MC.getConfig().set("beds." + bed + ".foot.loc", ProCandrV4.plugin.getStringFLocation(e.getClickedBlock().getLocation(), false));
            MC.getConfig().set("beds." + bed + ".foot.blockData", e.getClickedBlock().getBlockData().getAsString());
            MC.srConfig();
            p.sendMessage(ChatColor.GOLD + "The block you just clicked was set as a foot piece, " + ChatColor.UNDERLINE.GOLD + "make sure to set the head piece right after or this will not work!");

            ItemStack tool = p.getInventory().getItemInMainHand();
            ItemMeta meta = tool.getItemMeta();
            meta.setDisplayName("Bed Head");
            tool.setItemMeta(meta);
            p.sendMessage(ChatColor.UNDERLINE.GOLD + "Your setting has been set to 'Bed Head'");
        } else if (type.equalsIgnoreCase("Bed Head")) {
            if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
                p.sendMessage(ChatColor.RED + "Must click a bed's head block to add a bed head.");
                return;
            }
            if (!e.getClickedBlock().getType().toString().contains("_BED")) {
                p.sendMessage(ChatColor.RED + "Must click a bed's head block to add a bed head.");
                return;
            }
            if (e.getClickedBlock().getBlockData().getAsString().contains("foot")) {
                p.sendMessage(ChatColor.RED + "You must set the head block last, the foot should be first! Go back to the last bed you right click and right click the head piece.");
                return;
            }
            String bed = e.getItem().getItemMeta().getLore().get(1);
            MC.getConfig().set("beds." + bed + ".head.loc", ProCandrV4.plugin.getStringFLocation(e.getClickedBlock().getLocation(), false));
            MC.getConfig().set("beds." + bed + ".head.blockData", e.getClickedBlock().getBlockData().getAsString());
            MC.srConfig();
            p.sendMessage(ChatColor.GOLD + "The block you just clicked was set as a head piece!");
            ItemStack tool = p.getInventory().getItemInMainHand();
            ItemMeta meta = tool.getItemMeta();
            meta.setDisplayName("Bed Foot");
            meta.setLore(Arrays.asList(meta.getLore().get(0), "bed" + (MC.getConfig().getConfigurationSection("beds").getKeys(false).size() + 1)));
            tool.setItemMeta(meta);
            p.sendMessage(ChatColor.UNDERLINE.GOLD + "Your tool settings has been changed from 'Bed Head' to 'Bed Foot', and the next bed to be set is in the lore of your tool on the 2nd line!");
        } else if (type.equalsIgnoreCase("Washer/Dryer")) {
            if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
                p.sendMessage(ChatColor.RED + "Must click a furnace that'll be a washer/dryer.");
                return;
            }
            if (e.getClickedBlock().getType() != Material.FURNACE) {
                p.sendMessage(ChatColor.RED + "The washer/dryer should be a furnace.");
                return;
            }
            List<String> washersAndDryers = getList("washersAndDryers", MC);
            washersAndDryers.add(ProCandrV4.plugin.getStringFLocation(e.getClickedBlock().getLocation(), false));
            MC.getConfig().set("washersAndDryers", washersAndDryers);
            MC.srConfig();
            p.sendMessage(ChatColor.GOLD + "The furnace you just clicked became a washer/dryer for the laundryman to use!");
        } else if (type.equalsIgnoreCase("Janitor Door Location"))  {
            if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
                p.sendMessage(ChatColor.RED + "Must click a door that'll be the janitor door location.");
                return;
            }
            if (e.getClickedBlock().getType() != Material.IRON_DOOR) {
                p.sendMessage(ChatColor.RED + "The janitor door should be a door 4head.");
                return;
            }
            MC.getConfig().set("jobDoors.janitor.door", ProCandrV4.plugin.getStringFLocation(e.getClickedBlock().getLocation(), false));
            MC.srConfig();
            p.sendMessage(ChatColor.GOLD + "The door you just clicked became the janitor door for the janitor to use!");
        } else if (type.equalsIgnoreCase("Janitor Door Teleport Destination")) {
            MC.getConfig().set("jobDoors.janitor.destination", ProCandrV4.plugin.getStringFLocation(p.getLocation(), true));
            MC.srConfig();
            p.sendMessage(ChatColor.GOLD + "The janitor door teleport destination has been set to your location!");
        } else if (type.equalsIgnoreCase("Librarian Door Location"))  {
            if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
                p.sendMessage(ChatColor.RED + "Must click a door that'll be the librarian door location.");
                return;
            }
            if (e.getClickedBlock().getType() != Material.IRON_DOOR) {
                p.sendMessage(ChatColor.RED + "The librarian door should be a door 4head.");
                return;
            }
            MC.getConfig().set("jobDoors.librarian.door", ProCandrV4.plugin.getStringFLocation(e.getClickedBlock().getLocation(), false));
            MC.srConfig();
            p.sendMessage(ChatColor.GOLD + "The door you just clicked became the librarian door for the librarian to use!");
        } else if (type.equalsIgnoreCase("Librarian Door Teleport Destination")) {
            MC.getConfig().set("jobDoors.librarian.destination", ProCandrV4.plugin.getStringFLocation(p.getLocation(), true));
            MC.srConfig();
            p.sendMessage(ChatColor.GOLD + "The librarian door teleport destination has been set to your location!");
        } else if (type.equalsIgnoreCase("Cook Door Location"))  {
            if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
                p.sendMessage(ChatColor.RED + "Must click a door that'll be the cook door location.");
                return;
            }
            if (e.getClickedBlock().getType() != Material.IRON_DOOR) {
                p.sendMessage(ChatColor.RED + "The cook door should be a door 4head.");
                return;
            }
            MC.getConfig().set("jobDoors.cook.door", ProCandrV4.plugin.getStringFLocation(e.getClickedBlock().getLocation(), false));
            MC.srConfig();
            p.sendMessage(ChatColor.GOLD + "The door you just clicked became the cook door for the cook to use!");
        } else if (type.equalsIgnoreCase("Cook Door Teleport Destination")) {
            MC.getConfig().set("jobDoors.cook.destination", ProCandrV4.plugin.getStringFLocation(p.getLocation(), true));
            MC.srConfig();
            p.sendMessage(ChatColor.GOLD + "The cook door teleport destination has been set to your location!");
        } else if (type.equalsIgnoreCase("Laundryman Door Location"))  {
            if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
                p.sendMessage(ChatColor.RED + "Must click a door that'll be the laundryman door location.");
                return;
            }
            if (e.getClickedBlock().getType() != Material.IRON_DOOR) {
                p.sendMessage(ChatColor.RED + "The laundryman door should be a door 4head.");
                return;
            }
            MC.getConfig().set("jobDoors.laundryman.door", ProCandrV4.plugin.getStringFLocation(e.getClickedBlock().getLocation(), false));
            MC.srConfig();
            p.sendMessage(ChatColor.GOLD + "The door you just clicked became the laundryman door for the laundryman to use!");
        } else if (type.equalsIgnoreCase("Laundryman Door Teleport Destination")) {
            MC.getConfig().set("jobDoors.laundryman.destination", ProCandrV4.plugin.getStringFLocation(p.getLocation(), true));
            MC.srConfig();
            p.sendMessage(ChatColor.GOLD + "The laundryman door teleport destination has been set to your location!");
        }
    }



    @EventHandler
    public void onChangeTypeCameraRegionTool(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPermission("ProCandr.admin"))
            return;
        if (e.getAction() != Action.LEFT_CLICK_AIR && e.getAction() != Action.LEFT_CLICK_BLOCK)
            return;
        if (p.getInventory().getItemInMainHand().getType() != Material.GOLDEN_HOE)
            return;
        if (!e.getItem().hasItemMeta() && !e.getItem().getItemMeta().hasDisplayName() && !e.getItem().getItemMeta().hasLore())
            return;

        //Cell Spawnpoints, Door Locations, and Cop Spawnpoint
        String type = e.getItem().getItemMeta().getDisplayName();
        if (type.equalsIgnoreCase("Camera Region Block 1"))
            type = "Camera Region Block 2";
        else
            type = "Camera Region Block 1";
        ItemMeta meta = p.getInventory().getItemInMainHand().getItemMeta();
        meta.setDisplayName(type);
        p.getInventory().getItemInMainHand().setItemMeta(meta);
    }

    @EventHandler
    public void onSetLocCameraRegion(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPermission("ProCandr.admin"))
            return;
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (p.getInventory().getItemInMainHand().getType() != Material.GOLDEN_HOE)
            return;
        if (!e.getItem().hasItemMeta() && !e.getItem().getItemMeta().hasDisplayName() && !e.getItem().getItemMeta().hasLore())
            return;

        //Cell Spawnpoints, Door Locations, and Cop Spawnpoint
        String type = e.getItem().getItemMeta().getDisplayName();
        String cameraNum = e.getItem().getItemMeta().getLore().get(0);
        String map = e.getItem().getItemMeta().getLore().get(1);
        MapsConfig MC = new MapsConfig(map);
        if (type.equalsIgnoreCase("Camera Region Block 1")) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR) {
                p.sendMessage(ChatColor.RED + "To set the first region block, you must right click a block and not the air!");
                return;
            }
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                MC.getConfig().set("cameras." + cameraNum + ".regionBlock1", ProCandrV4.plugin.getStringFLocation(e.getClickedBlock().getLocation(), false));
                MC.srConfig();
                p.sendMessage(ChatColor.GOLD + "The first region block has been set to the block you just clicked!");
            }
        } else if (type.equalsIgnoreCase("Camera Region Block 2")) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR) {
                p.sendMessage(ChatColor.RED + "To set the second region block, you must right click a block and not the air!");
                return;
            }
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                MC.getConfig().set("cameras." + cameraNum + ".regionBlock2", ProCandrV4.plugin.getStringFLocation(e.getClickedBlock().getLocation(), false));
                MC.srConfig();
                p.sendMessage(ChatColor.GOLD + "The second region block has been set to the block you just clicked!");
            }
        }
    }

    private List<String> getList(String path, MapsConfig MC) {
        if (!MC.getConfig().contains(path))
            return new ArrayList<String>();
        return MC.getConfig().getStringList(path);
    }
}
