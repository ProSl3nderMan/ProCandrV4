package me.prosl3nderman.procandrv4;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Furnace {

    private ItemStack smelting = null;
    private ItemStack results = null;
    private Location loc;

    public Furnace(Location locc) {
        loc = locc;
    }

    public void setSmelting(Player p, ItemStack cooking, ItemStack doneCooking, Long cookTime) {
        smelting = cooking;
        setLit(p, true);
        new BukkitRunnable() {
            public void run() {
                setLit(p, false);
                smelting = null;
                results = doneCooking;
            }
        }.runTaskLater(ProCandrV4.plugin, cookTime);
    }

    public ItemStack getSmelting() {
        return smelting;
    }

    public ItemStack getResult() {
        return results;
    }

    public void setLit(Player p, Boolean lit) {
        new BukkitRunnable() {
            public void run() {
                if (lit)
                    p.sendBlockChange(loc, Bukkit.createBlockData(loc.getBlock().getBlockData().clone().getAsString().replaceFirst("lit=false","lit=true")));
                else
                    p.sendBlockChange(loc, loc.getBlock().getBlockData().clone());
            }
        }.runTaskLater(ProCandrV4.plugin, 2L);
    }

    public void clearInventory(Player p) {
        results = null;
        smelting = null;
    }

    public Location getLocation() {
        return loc;
    }
}
