package me.prosl3nderman.procandrv4.Commands;

import me.prosl3nderman.procandrv4.Game;
import me.prosl3nderman.procandrv4.MapsConfig;
import me.prosl3nderman.procandrv4.ProCandrV4;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;

public class ModCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to do this command!");
            return true;
        }
        Player p = (Player) sender;

        if (!p.hasPermission("ProCandrV4.modperms")) {
            p.sendMessage(ChatColor.RED + "You must be a mod to do this command.");
            return true;
        }

        if (args.length == 0) {
            sendCommandList(p);
            return true;
        }
        if (args[0].equalsIgnoreCase("help")) {
            sendCommandList(p);
            return true;
        }

        if (args[0].equalsIgnoreCase("tp")) {
            if (args.length == 1) {
                p.sendMessage(ChatColor.RED + "Correct usage: " + ChatColor.WHITE + "/mod " + args[0] + " <map>");
                return true;
            }
            if (ProCandrV4.plugin.game.containsKey(p.getName())) {
                p.sendMessage(ChatColor.RED + "You cannot do this command while in game!");
                return true;
            }
            String map = args[1];
            if (!ProCandrV4.plugin.getConfig().getStringList("maps").contains(map)) {
                p.sendMessage(ChatColor.RED + "The map " + map + " does not exist!");
                return true;
            }
            if (!isVanished(p))
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "sv on " + p.getName());

            MapsConfig MC = new MapsConfig(map);
            p.teleport(ProCandrV4.plugin.getLocationFString(MC.getConfig().getString("copspawnloc"), true));
            p.sendMessage(ChatColor.GOLD + "You have been teleported to the map " + ChatColor.WHITE + map + ChatColor.GOLD + "!");
            return true;
        }

        if (args[0].equalsIgnoreCase("spawn")) {
            p.teleport(ProCandrV4.plugin.getSpawn());
            if (isVanished(p))
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "sv off " + p.getName());
            return true;
        }

        if (args[0].equalsIgnoreCase("end")) {
            if (args.length < 2) {
                p.sendMessage(ChatColor.RED + "Incorrect usage! Correct usage: "+ ChatColor.WHITE + "/mod end <map name>");
                return true;
            }
            String map = args[1];
            if (!ProCandrV4.plugin.getConfig().getStringList("maps").contains(map)) {
                p.sendMessage(ChatColor.RED + "The map " + map + " does not exist!");
                return true;
            }
            if (!ProCandrV4.plugin.games.containsKey(map)) {
                p.sendMessage(ChatColor.RED + "This game is not active right now!");
                return true;
            }
            ProCandrV4.plugin.games.get(map).modEndGame(p);
            return true;
        }

        if (args[0].equalsIgnoreCase("kick")) {
            if (args.length < 3) {
                p.sendMessage(ChatColor.RED + "Incorrect usage! Correct usage: " + ChatColor.WHITE + "/mod kick <player name> <reason>");
                return true;
            }
            if (Bukkit.getPlayer(args[1]) == null || !Bukkit.getPlayer(args[1]).isOnline()) {
                p.sendMessage(ChatColor.RED + "The player " + ChatColor.WHITE + args[1] + ChatColor.RED + " does not exist, is not online, or is mistyped!");
                return true;
            }
            Player kickee = Bukkit.getPlayer(args[1]);
            if (!ProCandrV4.plugin.game.containsKey(kickee.getName())) {
                p.sendMessage(ChatColor.RED + "The player " + ChatColor.WHITE + args[1] + ChatColor.RED + " is not in a game!");
                return true;
            }
            String reason = args[2];
            if (args.length > 3) {
                for (int i = 3; i < args.length; i++) {
                    reason = reason + " " + args[i];
                }
            }
            ProCandrV4.plugin.games.get(ProCandrV4.plugin.game.get(p.getName())).kickPlayer(kickee, p, reason);
            p.sendMessage(ChatColor.GOLD + "The player " + ChatColor.WHITE + kickee.getName() + ChatColor.GOLD + " was kicked for the given reason '" + ChatColor.WHITE + reason + ChatColor.GOLD + "'!");
            return true;
        }

        if (args[0].equalsIgnoreCase("opencell")) {
            if (args.length < 2) {
                p.sendMessage(ChatColor.RED + "Incorrect usage! Correct usage: "+ ChatColor.WHITE + "/mod opencell <map name>");
                return true;
            }
            String map = args[1];
            if (!ProCandrV4.plugin.getConfig().getStringList("maps").contains(map)) {
                p.sendMessage(ChatColor.RED + "The map " + map + " does not exist!");
                return true;
            }
            if (!ProCandrV4.plugin.games.containsKey(map)) {
                p.sendMessage(ChatColor.RED + "This game is not active right now!");
                return true;
            }
            ProCandrV4.plugin.games.get(map).openCellCommand(p, true);
            return true;
        }

        if (args[0].equalsIgnoreCase("unvanish")) { //sv off <player name>
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "sv off " + p.getName());
            return true;
        }

        if (args[0].equalsIgnoreCase("vanish")) { //sv on <player name>
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "sv on " + p.getName());
            return true;
        }

        if (args[0].equalsIgnoreCase("copban")) {
            p.sendMessage(ChatColor.RED + "Not yet working, soonTM");
            return true;
        }

        if (args[0].equalsIgnoreCase("fly")) {
            if (p.isFlying()) {
                p.setFlying(false);
                p.setAllowFlight(false);
                p.sendMessage(ChatColor.GOLD + "Fly mode has been deactivated!");
            } else {
                p.setAllowFlight(true);
                p.setFlying(true);
                p.sendMessage(ChatColor.GOLD + "Fly mode has been activated!");
            }
        }
        return true;
    }

    private boolean isVanished(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }

    private void sendCommandList(Player p) {
        p.sendMessage(ChatColor.DARK_RED + "/mod help:" + ChatColor.RED + " Do this command to see all the mod commands.");
        p.sendMessage(ChatColor.DARK_RED + "/mod tp <map name>:" + ChatColor.RED + " Do this command to tp to a game to moderate.");
        p.sendMessage(ChatColor.DARK_RED + "/mod spawn:" + ChatColor.RED + " Do this command to go back to spawn.");
        p.sendMessage(ChatColor.DARK_RED + "/mod end <map name>:" + ChatColor.RED + " Do this command to end a game.");
        p.sendMessage(ChatColor.DARK_RED + "/mod kick <player name> <reason>:" + ChatColor.RED + " Do this command to kick a player from a game.");
        p.sendMessage(ChatColor.DARK_RED + "/mod opencell <map name>:" + ChatColor.RED + " Force open cells broadcast, enabling the time to do /open cell for players.");
        p.sendMessage(ChatColor.DARK_RED + "/mod unvanish:" + ChatColor.RED + " Takes away invisiablity.");
        p.sendMessage(ChatColor.DARK_RED + "/mod vanish:" + ChatColor.RED + " Gives you invisiablity.");
        p.sendMessage(ChatColor.DARK_RED + "/mod copban <player name> <time[1d, 1h]>:" + ChatColor.RED + " Bans a player from joining the cops, not yet working, soonTM.");
        p.sendMessage(ChatColor.DARK_RED + "/mod fly:" + ChatColor.RED + " Enables/disables fly.");
    }
}
