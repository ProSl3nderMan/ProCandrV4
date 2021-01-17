package me.prosl3nderman.procandrv4.Events;

import me.prosl3nderman.procandrv4.Game;
import me.prosl3nderman.procandrv4.ProCandrV4;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class EngineInteraction implements Listener {

    @EventHandler
    public void engineStart(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!ProCandrV4.plugin.game.containsKey(p.getName()))
            return;
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (e.getClickedBlock().getType() != Material.WALL_SIGN && e.getClickedBlock().getType() != Material.SIGN)
            return;
        Game game = ProCandrV4.plugin.games.get(ProCandrV4.plugin.game.get(p.getName()));
        if (game.isEngineSign(e.getClickedBlock().getLocation()) == false)
            return;
        if (game.playerIsCop(p)) {
            p.sendMessage(ChatColor.RED + "You must be a robber to start the engines!");
            return;
        }
        if (game.engineIsOn()) {
            p.sendMessage(ChatColor.RED + "Engine is already on!");
            return;
        }
        game.startEngines(p);
    }
}
