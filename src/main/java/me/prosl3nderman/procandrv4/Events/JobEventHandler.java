package me.prosl3nderman.procandrv4.Events;

import me.prosl3nderman.procandrv4.Jobs.Job;
import me.prosl3nderman.procandrv4.ProCandrV4;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;

public class JobEventHandler implements Listener {

    @EventHandler
    public void handleJobEvents(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND)
            return;
        Player p = e.getPlayer();
        if (!ProCandrV4.plugin.game.containsKey(p.getName()))
            return;
        Job job = ProCandrV4.plugin.games.get(ProCandrV4.plugin.game.get(p.getName())).getJob(p);
        if (job == null)
            return;
        job.handleEvent(e);
    }

    @EventHandler
    public void onPlayerMoveRawOrCookedChicken(InventoryClickEvent e) {
        if (e.getCurrentItem() == null || (e.getCurrentItem().getType() != Material.CHICKEN && e.getCurrentItem().getType() != Material.COOKED_CHICKEN))
            return;
        Player p = (Player) e.getWhoClicked();
        if (!ProCandrV4.plugin.game.containsKey(p.getName()))
            return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMoveCleanOrUncleanSheets(InventoryClickEvent e) {
        if (e.getCurrentItem() == null || (e.getCurrentItem().getType() != Material.RED_CONCRETE_POWDER && e.getCurrentItem().getType() != Material.RED_CONCRETE))
            return;
        Player p = (Player) e.getWhoClicked();
        if (!ProCandrV4.plugin.game.containsKey(p.getName()))
            return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onRightClickBed(PlayerBedEnterEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        if (!ProCandrV4.plugin.game.containsKey(p.getName()))
            return;
        Job job = ProCandrV4.plugin.games.get(ProCandrV4.plugin.game.get(p.getName())).getJob(p);
        if (job == null)
            return;
        job.onPlayerRespawn(e);
    }
}
