package me.prosl3nderman.procandrv4.Events;

import me.prosl3nderman.procandrv4.Game;
import me.prosl3nderman.procandrv4.ProCandrV4;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.logging.Level;

public class RightClickJoinSignEvent implements Listener {

    @EventHandler
    public void onPlayerClickJoinSignEvent(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (ProCandrV4.plugin.game.containsKey(p.getName()))
            return;
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (e.getClickedBlock().getType() != Material.WALL_SIGN && e.getClickedBlock().getType() != Material.SIGN)
            return;
        Sign s = (Sign) e.getClickedBlock().getState();
        if (!s.getLine(0).contains(ChatColor.DARK_GREEN + "CANDR"))
            return;
        String map = ChatColor.stripColor(s.getLine(1).split(" ")[1]);
        if (!ProCandrV4.plugin.games.containsKey(map)) {
            new Game(map);
            Bukkit.getLogger().log(Level.INFO, "[ProCandr] Starting new game with map " + map + "...");
        }

        if (ProCandrV4.plugin.games.get(map).isFull()) {
            p.sendTitle(ChatColor.RED + "This game is currently full!", "", 0, 100, 40);
            p.sendMessage(ChatColor.RED + "This game is currently full!");
            return;
        }

        ProCandrV4.plugin.games.get(map).addPlayer(p);
    }
}
