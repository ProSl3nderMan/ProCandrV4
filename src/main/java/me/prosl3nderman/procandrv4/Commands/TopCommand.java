package me.prosl3nderman.procandrv4.Commands;

import me.prosl3nderman.procandrv4.Database.StatsTable;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to do this command!");
            return true;
        }
        Player p = (Player) sender;
        if (args.length == 0) {
            p.sendMessage(ChatColor.RED + "You must specify which top stats you wish to see! Correct usage: " + ChatColor.WHITE + "/top <games|escapes|hours> [page number(1 default)]");
            return true;
        }
        int pageNumber = 1;
        if (args.length > 1) {
            try {
                pageNumber = Integer.parseInt(args[1]);
            } catch (NumberFormatException nfe) {
                p.sendMessage(ChatColor.RED + "The page number cannot be '" + ChatColor.WHITE + args[1] + ChatColor.RED + "'! Sending /top with default page number 1.");
            }
        }
        if (args[0].equalsIgnoreCase("games")) {
            new StatsTable().getTopGames(p, pageNumber);
            return true;
        }
        if (args[0].equalsIgnoreCase("escapes")) {
            new StatsTable().getTopEscapes(p, pageNumber);
            return true;
        }
        if (args[0].equalsIgnoreCase("hours")) {
            new StatsTable().getTopHours(p, pageNumber);
            return true;
        }
        return true;
    }
}
