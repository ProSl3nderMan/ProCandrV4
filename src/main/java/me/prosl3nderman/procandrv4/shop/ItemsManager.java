package me.prosl3nderman.procandrv4.shop;

import me.prosl3nderman.procandrv4.ItemsConfig;
import me.prosl3nderman.procandrv4.ProCandrV4;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class ItemsManager {

    private HashMap<String, String> items = new HashMap<>();
    //             material name/value, database name

    public ItemsManager() {
        items.put("LEATHER_HELMET", "lhelm");
        items.put("LEATHER_CHESTPLATE", "lchestplate");
        items.put("LEATHER_LEGGINGS", "lleggings");
        items.put("LEATHER_BOOTS", "lboots");
        items.put("CHAINMAIL_CHESTPLATE", "cchestplate");
        items.put("CHAINMAIL_LEGGINGS", "cleggings");
        items.put("TURTLE_HELMET", "thelm");
        items.put("WOODEN_PICKAXE", "wpic");
        items.put("WOODEN_SHOVEL", "wshovel");
        items.put("STONE_PICKAXE", "spic");
        items.put("STONE_SHOVEL", "sshovel");
    }

    public String getDBName(ItemStack item) {
        return items.get(item.getType().toString());
    }

    public String getMaterialName(String DBName) {
        for (String key : items.keySet()) {
            if (items.get(key).equalsIgnoreCase(DBName))
                return key;
        }
        return null;
    }

    public Boolean playerOwnsItem(String uuid, ItemStack item) {
        ItemsConfig IC = new ItemsConfig();
        if (!IC.getConfig().contains(uuid + ".items"))
            return false;

        String DBName = items.get(item.getType().toString());
        if (IC.getConfig().getStringList(uuid + ".items").contains(DBName))
            return true;
        return false;
    }

    public String getPrice(ItemStack item) {
        return ProCandrV4.plugin.getConfig().getString("storePrices.items." + getDBName(item));
    }

    public void equipItem(Player p, ItemStack item) {
        ItemStack newItem = new ItemStack(item.getType(), 1);
        if (newItem.getType().toString().contains("HELMET")) {
            p.getInventory().setHelmet(newItem);
            return;
        }
        if (newItem.getType().toString().contains("CHESTPLATE")) {
            p.getInventory().setChestplate(newItem);
            return;
        }
        if (newItem.getType().toString().contains("LEGGINGS")) {
            p.getInventory().setLeggings(newItem);
            return;
        }
        if (newItem.getType().toString().contains("BOOTS")) {
            p.getInventory().setBoots(newItem);
            return;
        }
        p.getInventory().remove(Material.WOODEN_SHOVEL);
        p.getInventory().remove(Material.WOODEN_PICKAXE);
        p.getInventory().remove(Material.STONE_SHOVEL);
        p.getInventory().remove(Material.STONE_PICKAXE);
        p.getInventory().addItem(item);
    }
}
