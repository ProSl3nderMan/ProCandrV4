package me.prosl3nderman.procandrv4.Commands;

import me.prosl3nderman.procandrv4.ProCandrV4;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to do this command!");
            return true;
        }
        Player p = (Player) sender;
        if (!ProCandrV4.plugin.game.containsKey(p.getName())) {
            p.sendMessage(ChatColor.RED + "You aren't in a game! Do /join to get into a game.");
            return true;
        }
        ProCandrV4.plugin.games.get(ProCandrV4.plugin.game.get(p.getName())).removePlayer(p);
        return true;
    }
}
