package me.prosl3nderman.procandrv4.Events;

import me.prosl3nderman.procandrv4.Game;
import me.prosl3nderman.procandrv4.ProCandrV4;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class OnLeverInteract implements Listener {

    @EventHandler
    public void onUseLeverAsCop(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (!ProCandrV4.plugin.game.containsKey(e.getPlayer().getName()))
            return;
        Player p = e.getPlayer();
        Game game = ProCandrV4.plugin.games.get(ProCandrV4.plugin.game.get(p.getName()));
        if (!game.playerIsCop(p))
            return;
        if (game.copsTaskActive())
            game.setLeversUsed(true);
    }
}
