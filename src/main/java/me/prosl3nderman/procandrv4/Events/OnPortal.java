package me.prosl3nderman.procandrv4.Events;

import me.prosl3nderman.procandrv4.ProCandrV4;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

import java.util.List;
import java.util.Random;

public class OnPortal implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPortal(PlayerPortalEvent event) {
        if (event.getCause() == PlayerPortalEvent.TeleportCause.END_PORTAL && !event.isCancelled()) {
            event.setCancelled(true);
            Player player = event.getPlayer();

            List<String> maps = ProCandrV4.plugin.getConfig().getStringList("maps");
            String map = maps.get(new Random().nextInt(maps.size()));
            if (ProCandrV4.plugin.games.size() == 0) {
                int playerSize = 0;
                for (String mapName : ProCandrV4.plugin.games.keySet()) {
                    if (ProCandrV4.plugin.games.get(mapName).getPlayerSize() > playerSize && ProCandrV4.plugin.games.get(mapName).getPlayerSize() < 16) {
                        map = mapName;
                        playerSize = ProCandrV4.plugin.games.get(mapName).getPlayerSize();
                    }
                }
            }

            player.performCommand("join " + map);
        }
    }
}
