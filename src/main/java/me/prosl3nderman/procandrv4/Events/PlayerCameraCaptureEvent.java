package me.prosl3nderman.procandrv4.Events;

import me.prosl3nderman.procandrv4.Game;
import me.prosl3nderman.procandrv4.ProCandrV4;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerCameraCaptureEvent implements Listener {

    @EventHandler
    public void onCameraSpotPlayer(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (!ProCandrV4.plugin.game.containsKey(p.getName()))
            return;
        Game game = ProCandrV4.plugin.games.get(ProCandrV4.plugin.game.get(p.getName()));
        if (game.playerIsCop(p))
            return;
        if (game.isAlreadySpottedByCamera(p))
            return;
        if (game.isPlayerInCameraView(p) == false)
            return;
        game.cameraSpottedPlayer(p);
    }
}
