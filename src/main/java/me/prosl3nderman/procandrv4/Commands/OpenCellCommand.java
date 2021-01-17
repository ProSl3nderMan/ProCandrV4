package me.prosl3nderman.procandrv4.Commands;

import me.prosl3nderman.procandrv4.ProCandrV4;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenCellCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to do this command!");
            return true;
        }
        Player p = (Player) sender;
        if (!ProCandrV4.plugin.game.containsKey(p.getName())) {
            p.sendMessage(ChatColor.RED + "You must be in a game to do this command! Do " + ChatColor.WHITE + "/join" + ChatColor.RED + " to join a game.");
            return true;
        }

        if (args.length == 0) {
            p.sendMessage(ChatColor.RED + "The command goes like " + ChatColor.WHITE + "'/open cell'" + ChatColor.RED + "! You must type the whole command out for it to work.");
            return true;
        }

        ProCandrV4.plugin.games.get(ProCandrV4.plugin.game.get(p.getName())).openCellCommand(p);
        return true;
    }
}
