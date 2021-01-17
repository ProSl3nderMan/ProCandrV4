package me.prosl3nderman.procandrv4.Events;

import me.prosl3nderman.procandrv4.ProCandrV4;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class OnFallIntoVoid implements Listener {

    @EventHandler
    public void onPlayerFallIntoVoid(PlayerMoveEvent e) {
        if (e.getTo().getY() > -5)
            return;
        Player p = e.getPlayer();
        if (ProCandrV4.plugin.game.containsKey(p.getName()))
            return;
        p.teleport(ProCandrV4.plugin.getLocationFString(ProCandrV4.plugin.getConfig().getString("spawn"), true));
    }
}
