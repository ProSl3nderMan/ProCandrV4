package me.prosl3nderman.procandrv4.Events;

import me.prosl3nderman.procandrv4.Game;
import me.prosl3nderman.procandrv4.ProCandrV4;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class StaffAccessEvent implements Listener {


    @EventHandler
    public void pressurePlate(PlayerInteractEvent e) {
        if (e.getAction() != Action.PHYSICAL)
            return;
        Block b = e.getClickedBlock();
        if (b.getType() != Material.LIGHT_WEIGHTED_PRESSURE_PLATE)
            return;
        Player p = e.getPlayer();
        if (!ProCandrV4.plugin.game.containsKey(p.getName()))
            return;
        Game game = ProCandrV4.plugin.games.get(ProCandrV4.plugin.game.get(p.getName()));
        if (!game.playerIsCop(p)) {
            p.sendMessage(ChatColor.RED + "You must be a cop to use the staff access!");
            return;
        }
        game.staffAccess(p, ProCandrV4.plugin.getStringFLocation(b.getLocation(), false));
    }
}
