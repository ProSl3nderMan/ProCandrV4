package me.prosl3nderman.procandrv4.Commands;

import me.prosl3nderman.procandrv4.Game;
import me.prosl3nderman.procandrv4.ProCandrV4;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class JoinCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to do this command!");
            return true;
        }
        Player p = (Player) sender;

        if (args.length == 0) {
            sendMaps(p);
            return true;
        }

        String map = args[0];
        if (!ProCandrV4.plugin.getConfig().getStringList("maps").contains(map)) {
            p.sendMessage(ChatColor.RED + "This map does not exist.");
            sendMaps(p);
            return true;
        }
        if (!ProCandrV4.plugin.games.containsKey(map)) {
            new Game(map);
            Bukkit.getLogger().log(Level.INFO, "[ProCandr] Starting new game with map " + map + "...");
        }

        if (ProCandrV4.plugin.games.get(map).isFull()) {
            p.sendMessage(ChatColor.RED + "This game is currently full!");
            return true;
        }

        if (ProCandrV4.plugin.game.containsKey(p.getName())) {
            p.sendMessage(ChatColor.RED + "You are already in a game! Do " + ChatColor.WHITE + "/leave" + ChatColor.RED + " to leave the game.");
            return true;
        }

        if (args.length > 1 && p.hasPermission("ProCandr.joinWithJob")) {
            ProCandrV4.plugin.games.get(map).addPlayer(p, args[1]);
            return true;
        }
        ProCandrV4.plugin.games.get(map).addPlayer(p);
        return true;
    }

    private void sendMaps(Player p) {
        String maps = ChatColor.LIGHT_PURPLE + "";
        for (String s : ProCandrV4.plugin.getConfig().getStringList("maps"))
            maps = maps + ChatColor.DARK_PURPLE + ", " + ChatColor.LIGHT_PURPLE + s;
        maps = maps.replaceFirst(", ", "");
        p.sendMessage(ChatColor.DARK_PURPLE + "List of games: " + maps);
    }
}
