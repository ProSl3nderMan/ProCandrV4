package me.prosl3nderman.procandrv4.Commands;

import me.prosl3nderman.procandrv4.ProCandrV4;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoteCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Must be sent by a player.");
            return true;
        }
        Player p = (Player) commandSender;

        p.sendMessage(ChatColor.DARK_AQUA + "------------ " + ChatColor.AQUA + "Vote Links " + ChatColor.DARK_AQUA + "------------");
        int i = 1;
        for (String link : ProCandrV4.plugin.getConfig().getConfigurationSection("voteLinks").getKeys(false)) {
            p.sendMessage(ChatColor.DARK_AQUA + "" + i + ") " + ChatColor.AQUA + ProCandrV4.plugin.getConfig().getString("voteLinks." + link));
            i++;
        }
        p.sendMessage(ChatColor.DARK_AQUA + "We really appreciate you voting for us!");
        return true;
    }
}
