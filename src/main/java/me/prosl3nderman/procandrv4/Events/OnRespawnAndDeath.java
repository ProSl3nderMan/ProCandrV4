package me.prosl3nderman.procandrv4.Events;

import me.prosl3nderman.procandrv4.Game;
import me.prosl3nderman.procandrv4.ProCandrV4;
import me.prosl3nderman.procandrv4.shop.PotionsManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class OnRespawnAndDeath implements Listener {

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        if (!ProCandrV4.plugin.game.containsKey(p.getName())) {
            p.teleport(ProCandrV4.plugin.getSpawn());
            return;
        }
        Game game = ProCandrV4.plugin.games.get(ProCandrV4.plugin.game.get(p.getName()));
        e.setRespawnLocation(game.getSpawn(p));
        p.setGameMode(GameMode.ADVENTURE);
        if (game.hadUsedPotion(p)) {
            PotionsManager PM = new PotionsManager();
            PM.equipPotion(p, game.getUsedPotion(p));
            game.removeUsedPotion(p);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        e.setKeepInventory(true);
    }
}
