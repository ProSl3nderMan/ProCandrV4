package me.prosl3nderman.procandrv4.Commands;

import me.prosl3nderman.procandrv4.Database.StatsTable;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to do this command!");
            return true;
        }
        Player p = (Player) sender;
        if (args.length == 0) {
            new StatsTable().showStats(p, p.getName());
            return true;
        }
        new StatsTable().showStats(p, args[0]);
        return true;
    }
}
