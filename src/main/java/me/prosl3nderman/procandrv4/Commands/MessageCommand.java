package me.prosl3nderman.procandrv4.Commands;

import me.prosl3nderman.procandrv4.ProCandrV4;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Must be sent by a player.");
            return true;
        }
        Player p = (Player) commandSender;

        if (args.length == 0 || args.length == 1) {
            p.sendMessage(ChatColor.RED + "Correct usage: " + ChatColor.WHITE + command + " <player name> <message>");
            return true;
        }
        if (Bukkit.getPlayer(args[0]) == null) {
            p.sendMessage(ChatColor.RED + "The player " + ChatColor.WHITE + args[0] + ChatColor.RED + " does not exist.");
            return true;
        }
        if (!Bukkit.getPlayer(args[0]).isOnline()) {
            p.sendMessage(ChatColor.RED + "The player " + ChatColor.WHITE + args[0] + ChatColor.RED + " is not online.");
            return true;
        }
        Player sentTo = Bukkit.getPlayer(args[0]);

        String msg = "";
        for (int i = 1; i < args.length; i++)
            msg = msg + " " + args[i];

        if (msg.contains("uploadfiles.io/85pkg")) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "ipban " + p.getName() + " Advertising some stupid minecraft file.");
            return false;
        }

        p.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "me" + ChatColor.GRAY + " -> " + ChatColor.LIGHT_PURPLE + sentTo.getName() + ChatColor.GRAY + "] " + ChatColor.RESET + msg);
        sentTo.sendMessage(ChatColor.GRAY + "[" + ChatColor.LIGHT_PURPLE + p.getName() + ChatColor.GRAY + " -> " + ChatColor.GOLD + "me" + ChatColor.GRAY + "] " + ChatColor.RESET + msg);

        if (ProCandrV4.plugin.reply.containsKey(sentTo.getName()))
            ProCandrV4.plugin.reply.remove(sentTo.getName());
        ProCandrV4.plugin.reply.put(sentTo.getName(), p.getName());
        return true;
    }
}
