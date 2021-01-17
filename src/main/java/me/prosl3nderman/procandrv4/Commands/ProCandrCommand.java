package me.prosl3nderman.procandrv4.Commands;

import com.google.common.collect.Lists;
import me.prosl3nderman.procandrv4.Database.StatsTable;
import me.prosl3nderman.procandrv4.Game;
import me.prosl3nderman.procandrv4.MapsConfig;
import me.prosl3nderman.procandrv4.ProCandrV4;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ProCandrCommand implements CommandExecutor, TabCompleter {

    private String[] commands0 = { "create","delete","tool","maplist","openDoors","closeDoors","setSpawn","reload","teleport","tp","spawn","camera","spawnDirtAndGarbage","setDirtHead","setGarbageHead" };

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to do this command.");
            return null;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("ProCandr.admin")) {
            p.sendMessage(ChatColor.RED + "Must be an admin to use this command!");
            return null;
        }
        List<String> fList = Lists.newArrayList();
        if (args.length == 1) {
            for (String s : commands0) {
                if (s.toLowerCase().startsWith(args[0].toLowerCase()))
                    fList.add(s.toLowerCase());
            }
            return fList;
        }
        return Arrays.asList(commands0);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to do this command.");
            return true;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("ProCandr.admin")) {
            p.sendMessage(ChatColor.RED + "Must be an admin to use this command!");
            return true;
        }

        if (args.length == 0) {
            p.sendMessage(ChatColor.GOLD + "/procandr create <map>: " + ChatColor.YELLOW + "Creates a new map in your current world, then gives you the setup tool.");
            p.sendMessage(ChatColor.GOLD + "/procandr delete <map>: " + ChatColor.YELLOW + "Deletes the specified map.");
            p.sendMessage(ChatColor.GOLD + "/procandr tool <map>: " + ChatColor.YELLOW + "Gives you the setup tool for a map.");
            p.sendMessage(ChatColor.GOLD + "/procandr camera add <map>: " + ChatColor.YELLOW + "Gives you a tool to setup a new camera.");
            p.sendMessage(ChatColor.GOLD + "/procandr camera list <map>: " + ChatColor.YELLOW + "Lists out all the cameras in the given map.");
            p.sendMessage(ChatColor.GOLD + "/procandr camera edit <camera> <map>: " + ChatColor.YELLOW + "Gives you the tool to edit the given camera in the given map.");
            p.sendMessage(ChatColor.GOLD + "/procandr camera remove <map> <camera>: " + ChatColor.YELLOW + "Removes the specified camera.");
            p.sendMessage(ChatColor.GOLD + "/procandr bed add <map>: " + ChatColor.YELLOW + "Gives you a tool to setup a new bed.");
            p.sendMessage(ChatColor.GOLD + "/procandr maplist: " + ChatColor.YELLOW + "Gives you the list of maps on this server that have been created.");
            p.sendMessage(ChatColor.GOLD + "/procandr openDoors <map>: " + ChatColor.YELLOW + "Opens the doors, but does not close them! Make sure to do /procandr closeDoors after you open them.");
            p.sendMessage(ChatColor.GOLD + "/procandr closeDoors <map>: " + ChatColor.YELLOW + "Closes the doors.");
            p.sendMessage(ChatColor.GOLD + "/procandr setSpawn: " + ChatColor.YELLOW + "Sets the spawn point.");
            p.sendMessage(ChatColor.GOLD + "/procandr reload: " + ChatColor.YELLOW + "Reloads the configs.");
            p.sendMessage(ChatColor.GOLD + "/procandr teleport <map>: " + ChatColor.YELLOW + "Teleports to the map. Alias: tp");
            p.sendMessage(ChatColor.GOLD + "/procandr spawn: " + ChatColor.YELLOW + "Teleport to the spawn.");
            p.sendMessage(ChatColor.GOLD + "/procandr spawnDirtAndGarbage <map>: " + ChatColor.YELLOW + "Spawns dirt and garbage in all designated spots of the given map.");
            p.sendMessage(ChatColor.GOLD + "/procandr despawnDirtAndGarbage <map>: " + ChatColor.YELLOW + "Despawns dirt and garbage in all designated spots of the given map.");
            return true;
        }

        if (args[0].equalsIgnoreCase("test")) {
            p.sendMessage(new StatsTable().getLastOn(p.getName()));
            return true;
        }

        if (args[0].equalsIgnoreCase("getBlockData")) {
            p.sendMessage(p.getTargetBlock(5).getBlockData().getAsString());
            return true;
        }

        if (args[0].equalsIgnoreCase("setDirtHead") || args[0].equalsIgnoreCase("setGarbageHead")) {
            if (args.length == 1) {
                p.sendMessage(ChatColor.RED + "Correct usage: " + ChatColor.WHITE + "/procandr " + args[0] + " <skin-id>");
                return true;
            }
            ProCandrV4.plugin.getConfig().set(args[0].split("t")[1], args[1]);
            ProCandrV4.plugin.srConfig();
            p.sendMessage(ChatColor.GOLD + "The " + ChatColor.WHITE + args[0].split("t")[1] + ChatColor.GOLD + " has been set to the skin-id " + ChatColor.WHITE + args[1] + ChatColor.GOLD + "!");
            return true;
        }

        if (args[0].equalsIgnoreCase("spawnDirtAndGarbage")) {
            if (args.length == 1) {
                p.sendMessage(ChatColor.RED + "Correct usage: " + ChatColor.WHITE + "/procandr spawnDirtAndGarbage <map>");
                return true;
            }
            String map = args[1];
            if (!ProCandrV4.plugin.getConfig().getStringList("maps").contains(map)) {
                p.sendMessage(ChatColor.RED + "The map " + map + " does not exist! Do " + ChatColor.WHITE + "/procandr create " + map + ChatColor.RED + " to create this map.");
                return true;
            }
            for (String locString : new MapsConfig(map).getConfig().getStringList("dirtAndGarbageSpots")) {
                Location loc = ProCandrV4.plugin.getLocationFString(locString, false);
                loc.getBlock().setType(Material.MOVING_PISTON);
            }
            new BukkitRunnable() {
                public void run() {
                    for (String locString : new MapsConfig(map).getConfig().getStringList("dirtAndGarbageSpots")) {
                        Location loc = ProCandrV4.plugin.getLocationFString(locString, false);
                        if (new Random().nextInt(10) > 5)
                            p.sendBlockChange(loc, Material.GRASS.createBlockData());
                        else
                            p.sendBlockChange(loc, Material.DEAD_BUSH.createBlockData());
                    }
                }
            }.runTaskLater(ProCandrV4.plugin, 10L);
            p.sendMessage(ChatColor.GOLD + "Dirt and garbage have been spawned in all the designated spots on the map " + ChatColor.WHITE + map + ChatColor.GOLD + "!");
            return true;
        }

        if (args[0].equalsIgnoreCase("despawnDirtAndGarbage")) {
            if (args.length == 1) {
                p.sendMessage(ChatColor.RED + "Correct usage: " + ChatColor.WHITE + "/procandr despawnDirtAndGarbage <map>");
                return true;
            }
            String map = args[1];
            if (!ProCandrV4.plugin.getConfig().getStringList("maps").contains(map)) {
                p.sendMessage(ChatColor.RED + "The map " + map + " does not exist! Do " + ChatColor.WHITE + "/procandr create " + map + ChatColor.RED + " to create this map.");
                return true;
            }
            for (String locString : new MapsConfig(map).getConfig().getStringList("dirtAndGarbageSpots")) {
                Location loc = ProCandrV4.plugin.getLocationFString(locString, false);
                loc.getBlock().setType(Material.AIR);
                p.sendBlockChange(loc, Material.AIR.createBlockData());
            }
            p.sendMessage(ChatColor.GOLD + "Dirt and garbage have been despawned in all the designated spots on the map " + ChatColor.WHITE + map + ChatColor.GOLD + "!");
            return true;
        }

        if (args[0].equalsIgnoreCase("spawn")) {
            p.teleport(ProCandrV4.plugin.getSpawn());
            return true;
        }

        if (args[0].equalsIgnoreCase("teleport") || args[0].equalsIgnoreCase("tp")) {
            if (args.length == 1) {
                p.sendMessage(ChatColor.RED + "Correct usage: " + ChatColor.WHITE + "/procandr " + args[0] + " <map>");
                return true;
            }
            String map = args[1];
            if (!ProCandrV4.plugin.getConfig().getStringList("maps").contains(map)) {
                p.sendMessage(ChatColor.RED + "The map " + map + " does not exist! Do " + ChatColor.WHITE + "/procandr create " + map + ChatColor.RED + " to create this map.");
                return true;
            }
            MapsConfig MC = new MapsConfig(map);
            p.teleport(ProCandrV4.plugin.getLocationFString(MC.getConfig().getString("copspawnloc"), true));
            p.sendMessage(ChatColor.GOLD + "You have been teleported to the map " + ChatColor.WHITE + map + ChatColor.GOLD + "!");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            ProCandrV4.plugin.saveDefaultConfig();
            ProCandrV4.plugin.reloadConfig();
            ProCandrV4.plugin.reloadMaps();
            p.sendMessage(ChatColor.GOLD + "The " + ChatColor.WHITE + "config.yml" + ChatColor.GOLD + " and " + ChatColor.WHITE + "maps.yml" + ChatColor.GOLD + " have been reloaded.");
            return true;
        }

        if (args[0].equalsIgnoreCase("setSpawn")) {
            ProCandrV4.plugin.getConfig().set("spawn", ProCandrV4.plugin.getStringFLocation(p.getLocation(), true));
            ProCandrV4.plugin.srConfig();
            p.sendMessage(ChatColor.GOLD + "Set the spawn to your location.");
            return true;
        }

        if (args[0].equalsIgnoreCase("openDoors") || args[0].equalsIgnoreCase("closeDoors")) {
            if (args.length == 1) {
                if (args[0].contains("open"))
                    p.sendMessage(ChatColor.RED + "Correct usage: " + ChatColor.WHITE + "/procandr openDoors <map>");
                else
                    p.sendMessage(ChatColor.RED + "Correct usage: " + ChatColor.WHITE + "/procandr closeDoors <map>");
                return true;
            }
            String map = args[1];
            if (!ProCandrV4.plugin.getConfig().getStringList("maps").contains(map)) {
                p.sendMessage(ChatColor.RED + "The map " + map + " does not exist! Do " + ChatColor.WHITE + "/procandr create " + map + ChatColor.RED + " to create this map.");
                return true;
            }
            Boolean notARealGame = false;
            if (!ProCandrV4.plugin.games.containsKey(map)) {
                new Game(map);
                notARealGame = true;
            }

            if (args[0].contains("open")) {
                ProCandrV4.plugin.games.get(map).openDoors();
                p.sendMessage(ChatColor.GOLD + "Doors have been opened on map " + ChatColor.YELLOW + map + ChatColor.GOLD + "!");
            } else {
                ProCandrV4.plugin.games.get(map).closeDoors();
                p.sendMessage(ChatColor.GOLD + "Doors have been closed on map " + ChatColor.YELLOW + map + ChatColor.GOLD + "!");
            }
            if (notARealGame)
                ProCandrV4.plugin.games.remove(map);
            return true;
        }

        if (args[0].equalsIgnoreCase("create")) {
            if (args.length == 1) {
                p.sendMessage(ChatColor.RED + "Correct usage: " + ChatColor.WHITE + "/procandr create <map>");
                return true;
            }
            String map = args[1];
            if (ProCandrV4.plugin.getConfig().getStringList("maps").contains(map)) {
                p.sendMessage(ChatColor.RED + "The map " + ChatColor.WHITE + map + ChatColor.RED + " already exists! To get the tool for that map, do " + ChatColor.WHITE + "/procandr tool " + map
                        + ChatColor.RED + ". To delete the original map, do " + ChatColor.WHITE + "/procandr delete " + map + ChatColor.RED + ".");
                return true;
            }
            MapsConfig MC = new MapsConfig(map);
            MC.getConfig().set("world", p.getWorld().getName());
            MC.srConfig();
            List<String> maps = ProCandrV4.plugin.getList("maps", ProCandrV4.plugin.getConfig());
            maps.add(map);
            ProCandrV4.plugin.getConfig().set("maps", maps);
            ProCandrV4.plugin.srConfig();
            ItemStack tool = new ItemStack(Material.DIAMOND_HOE, 1);
            ItemMeta meta = tool.getItemMeta();
            meta.setDisplayName("Cell Spawnpoints");
            meta.setLore(Arrays.asList(map));
            tool.setItemMeta(meta);
            p.getInventory().addItem(tool);
            p.sendMessage(ChatColor.GOLD + "The map " + ChatColor.WHITE + map + ChatColor.GOLD + " has now been created!");
            p.sendMessage(ChatColor.GOLD + "To use the tool, right click while standing at the location of the cell spawn point, door location, and cop spawn point. When adding a Lever to a list of levers to reset,"+
                    " right click the lever with the tool. When adding a block update block, right click the block below what you wish to send the block update to. When setting the engine sign, right click the sign."+
                    " Left click to cycle through the different types, " + ChatColor.WHITE + "Cell Spawnpoints, Door Locations, Cop Spawnpoint, Lever, Block Update, Engine Sign, Escape Region Block 1, Escape" +
                    " Region Block 2, Join Sign, Staff Access, Dirt And Garbage Spots, Freezer, Food Trays, Furnaces, Book Category, Book Refill, Bed Foot, Bed Head, Washer/Dryer, Janitor Door Location, Janitor Door"+
                    " Teleport Destination, Librarian Door Location, Librarian Door Teleport Destination, Cook Door Location, Cook Door Teleport Destination, Laundryman Door Location, Laundryman Door Teleport" +
                    " Destination" + ChatColor.GOLD + ".");
            return true;
        }

        if (args[0].equalsIgnoreCase("bed")) {
            if (args.length < 3) {
                p.sendMessage(ChatColor.RED + "Correct usage: ");
                p.sendMessage(ChatColor.GOLD + "/procandr bed add <map>: " + ChatColor.YELLOW + "Gives you a tool to setup a new bed.");
                return true;
            }
            if (!args[1].equalsIgnoreCase("add")) {
                p.sendMessage(ChatColor.RED + "Correct usage: ");
                p.sendMessage(ChatColor.GOLD + "/procandr bed add <map>: " + ChatColor.YELLOW + "Gives you a tool to setup a new bed.");
                return true;
            }
            String map = args[2];
            if (!ProCandrV4.plugin.getConfig().getStringList("maps").contains(map)) {
                p.sendMessage(ChatColor.RED + "The map " + map + " does not exist! Do " + ChatColor.WHITE + "/procandr create " + map + ChatColor.RED + " to create this map.");
                return true;
            }
            MapsConfig MC = new MapsConfig(map);
            if (args[1].equalsIgnoreCase("add")) {
                String bedNum = "1";
                if (MC.getConfig().contains("beds"))
                    bedNum = "" + (MC.getConfig().getConfigurationSection("beds").getKeys(false).size() + 1);
                ItemStack tool = new ItemStack(Material.STONE_HOE, 1);
                ItemMeta meta = tool.getItemMeta();
                meta.setDisplayName("Bed Part HEAD");
                meta.setLore(Arrays.asList(bedNum, map));
                tool.setItemMeta(meta);
                p.getInventory().addItem(tool);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("camera")) {
            if (args.length < 3) {
                p.sendMessage(ChatColor.RED + "Correct usage: ");
                p.sendMessage(ChatColor.GOLD + "/procandr camera add <map>: " + ChatColor.YELLOW + "Gives you a tool to setup a new camera.");
                p.sendMessage(ChatColor.GOLD + "/procandr camera list <map>: " + ChatColor.YELLOW + "Lists out all the cameras in the given map.");
                p.sendMessage(ChatColor.GOLD + "/procandr camera edit <map> <camera>: " + ChatColor.YELLOW + "Gives you the tool to edit the given camera in the given map.");
                p.sendMessage(ChatColor.GOLD + "/procandr camera remove <map> <camera>: " + ChatColor.YELLOW + "Removes the specified camera.");
                return true;
            }
            if (!args[1].equalsIgnoreCase("add") && !args[1].equalsIgnoreCase("list") && !args[1].equalsIgnoreCase("edit") && !args[1].equalsIgnoreCase("remove")) {
                p.sendMessage(ChatColor.RED + "Correct usage: ");
                p.sendMessage(ChatColor.GOLD + "/procandr camera add <map>: " + ChatColor.YELLOW + "Gives you a tool to setup a new camera.");
                p.sendMessage(ChatColor.GOLD + "/procandr camera list <map>: " + ChatColor.YELLOW + "Lists out all the cameras in the given map.");
                p.sendMessage(ChatColor.GOLD + "/procandr camera edit <map> <camera>: " + ChatColor.YELLOW + "Gives you the tool to edit the given camera in the given map.");
                p.sendMessage(ChatColor.GOLD + "/procandr camera remove <map> <camera>: " + ChatColor.YELLOW + "Removes the specified camera.");
                return true;
            }
            String map = args[2];
            if (!ProCandrV4.plugin.getConfig().getStringList("maps").contains(map)) {
                p.sendMessage(ChatColor.RED + "The map " + map + " does not exist! Do " + ChatColor.WHITE + "/procandr create " + map + ChatColor.RED + " to create this map.");
                return true;
            }
            MapsConfig MC = new MapsConfig(map);
            if (args[1].equalsIgnoreCase("add")) {
                String cameraNum = "1";
                if (MC.getConfig().contains("cameras"))
                    cameraNum = "" + (MC.getConfig().getConfigurationSection("cameras").getKeys(false).size() + 1);
                ItemStack tool = new ItemStack(Material.GOLDEN_HOE, 1);
                ItemMeta meta = tool.getItemMeta();
                meta.setDisplayName("Camera Region Block 1");
                meta.setLore(Arrays.asList(cameraNum, map));
                tool.setItemMeta(meta);
                p.getInventory().addItem(tool);
                p.sendMessage(ChatColor.GOLD + "The camera " + ChatColor.WHITE + cameraNum + ChatColor.GOLD + " in map " + ChatColor.WHITE + map + ChatColor.GOLD + " has been created!");
                p.sendMessage(ChatColor.GOLD + "To use the tool, right click the 1st block to set the first corner of the camera region. Left click to navigate through the two settings. The two settings are " +
                        ChatColor.WHITE + "Camera Region Block 1 and Camera Region Block 2" + ChatColor.GOLD + ".");
            }
            if (args[1].equalsIgnoreCase("list")) {
                String cameras = "";
                for (String camera : MC.getConfig().getConfigurationSection("cameras").getKeys(false))
                    cameras = cameras + ChatColor.GOLD + ", " + ChatColor.WHITE + camera;
                cameras = cameras.replaceFirst(ChatColor.GOLD + ", ", "");
                p.sendMessage(ChatColor.GOLD + "List of cameras in map " + ChatColor.WHITE + map + ChatColor.GOLD + ": " + cameras + ChatColor.GOLD + ".");
                return true;
            }
            if (args.length < 4) {
                p.sendMessage(ChatColor.RED + "Correct usage: ");
                p.sendMessage(ChatColor.GOLD + "/procandr camera add <map>: " + ChatColor.YELLOW + "Gives you a tool to setup a new camera.");
                p.sendMessage(ChatColor.GOLD + "/procandr camera list <map>: " + ChatColor.YELLOW + "Lists out all the cameras in the given map.");
                p.sendMessage(ChatColor.GOLD + "/procandr camera edit <map> <camera>: " + ChatColor.YELLOW + "Gives you the tool to edit the given camera in the given map.");
                p.sendMessage(ChatColor.GOLD + "/procandr camera remove <map> <camera>: " + ChatColor.YELLOW + "Removes the specified camera.");
                return true;
            }
            List<String> cameras = new ArrayList<>();
            for (String camera : MC.getConfig().getConfigurationSection("cameras").getKeys(false))
                cameras.add(camera);
            String camera = args[3];
            if (!cameras.contains(camera)) {
                p.sendMessage(ChatColor.RED + "The camera " + ChatColor.WHITE + camera + ChatColor.RED + " does not exist. Do " + ChatColor.WHITE + "/procandr camera list" + map + ChatColor.RED + " for a list of " +
                        "cameras.");
                return true;
            }
            if (args[1].equalsIgnoreCase("edit")) {
                String cameraNum = camera;
                ItemStack tool = new ItemStack(Material.GOLDEN_HOE, 1);
                ItemMeta meta = tool.getItemMeta();
                meta.setDisplayName("Camera Region Block 1");
                meta.setLore(Arrays.asList(cameraNum, map));
                tool.setItemMeta(meta);
                p.getInventory().addItem(tool);
                p.sendMessage(ChatColor.GOLD + "The camera " + ChatColor.WHITE + cameraNum + ChatColor.GOLD + " in map " + ChatColor.WHITE + map + ChatColor.GOLD + " has been created!");
                p.sendMessage(ChatColor.GOLD + "To use the tool, right click the 1st block to set the first corner of the camera region. Left click to navigate through the two settings. The two settings are " +
                        ChatColor.WHITE + "Camera Region Block 1 and Camera Region Block 2" + ChatColor.GOLD + ".");
                return true;
            }
            if (args[1].equalsIgnoreCase("remove")) {
                MC.getConfig().set("cameras." + camera, null);
                p.sendMessage(ChatColor.GOLD + "The camera " + ChatColor.WHITE + camera + ChatColor.GOLD + " was removed from the map " + ChatColor.WHITE + map + ChatColor.GOLD + "!");
                return true;
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("delete")) {
            if (args.length == 1) {
                p.sendMessage(ChatColor.RED + "Correct usage: " + ChatColor.WHITE + "/procandr delete <map>");
                return true;
            }
            String map = args[1];
            if (!ProCandrV4.plugin.getConfig().getStringList("maps").contains(map)) {
                p.sendMessage(ChatColor.RED + "The map " + map + " does not exist! Do " + ChatColor.WHITE + "/procandr maplist " + ChatColor.RED + " to get a list of maps.");
                return true;
            }
            new MapsConfig(map).delete();
            List<String> maps = ProCandrV4.plugin.getList("maps", ProCandrV4.plugin.getConfig());
            maps.remove(map);
            ProCandrV4.plugin.getConfig().set("maps", maps);
            ProCandrV4.plugin.srConfig();
            p.sendMessage(ChatColor.GOLD + "You have deleted the map " + ChatColor.WHITE + map + ChatColor.GOLD + "!");
            return true;
        }

        if (args[0].equalsIgnoreCase("tool")) {
            if (args.length == 1) {
                p.sendMessage(ChatColor.RED + "Correct usage: " + ChatColor.WHITE + "/procandr tool <map>");
                return true;
            }
            String map = args[1];
            if (!ProCandrV4.plugin.getConfig().getStringList("maps").contains(map)) {
                p.sendMessage(ChatColor.RED + "The map " + map + " does not exist! Do " + ChatColor.WHITE + "/procandr create " + map + ChatColor.RED + " to create this map and get the tool.");
                return true;
            }
            ItemStack tool = new ItemStack(Material.DIAMOND_HOE, 1);
            ItemMeta meta = tool.getItemMeta();
            meta.setDisplayName("Cell Spawnpoints");
            meta.setLore(Arrays.asList(map));
            tool.setItemMeta(meta);
            p.getInventory().addItem(tool);
            p.sendMessage(ChatColor.GOLD + "To use the tool, right click while standing at the location of the cell spawn point, door location, and cop spawn point. When adding a Lever to a list of levers to reset,"+
                    " right click the lever with the tool. When adding a block update block, right click the block below what you wish to send the block update to. When setting the engine sign, right click the sign."+
                    " Left click to cycle through the different types, " + ChatColor.WHITE + "Cell Spawnpoints, Door Locations, Cop Spawnpoint, Lever, Block Update, Engine Sign, Escape Region Block 1, Escape" +
                    " Region Block 2, Join Sign, Staff Access, Dirt And Garbage Spots, Freezer, Food Trays, Furnaces, Book Category, Book Refill, Bed Foot, Bed Head, Washer/Dryer, Janitor Door Location, Janitor Door"+
                    " Teleport Destination, Librarian Door Location, Librarian Door Teleport Destination, Cook Door Location, Cook Door Teleport Destination, Laundryman Door Location, Laundryman Door Teleport" +
                    " Destination" + ChatColor.GOLD + ".");
            return true;
        }

        if (args[0].equalsIgnoreCase("maplist")) {
            String maps = ChatColor.YELLOW + "";
            for (String s : ProCandrV4.plugin.getConfig().getStringList("maps"))
                maps = maps + ChatColor.GOLD + ", " + ChatColor.YELLOW + s;
            maps = maps.replaceFirst(", ", "");
            p.sendMessage(ChatColor.GOLD + "All Candr maps on this server: " + maps);
            return true;
        }
        return true;
    }
}
