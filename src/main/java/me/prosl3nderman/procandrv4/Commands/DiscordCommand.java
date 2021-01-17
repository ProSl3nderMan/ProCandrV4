package me.prosl3nderman.procandrv4.Commands;

import me.prosl3nderman.procandrv4.ProCandrV4;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DiscordCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Must be done by a player.");
            return true;
        }
        Player p = (Player) commandSender;

        if (label.contains("forum"))
            p.sendMessage(ChatColor.GOLD + "We do not have a forums, here's a link to our discord: " + ChatColor.WHITE + ProCandrV4.plugin.getConfig().getString("discord"));
        else
            p.sendMessage(ChatColor.GOLD + "Here's a link to our discord: " + ChatColor.WHITE + ProCandrV4.plugin.getConfig().getString("discord"));
        return true;
    }
}
