package me.prosl3nderman.procandrv4.Jobs;

import me.prosl3nderman.procandrv4.Furnace;
import me.prosl3nderman.procandrv4.Game;
import me.prosl3nderman.procandrv4.MapsConfig;
import me.prosl3nderman.procandrv4.ProCandrV4;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.logging.Level;

public class Laundryman extends Job {

    private List<Location> emptiedBeds;
    private Player p;
    private String map;
    private HashMap<Location, Furnace> furnaces = new HashMap<>();

    public Laundryman(Player p) {
        emptiedBeds = new ArrayList<>();
        this.p = p;
        map = ProCandrV4.plugin.game.get(p.getName());
    }

    @Override
    public void setupJob() {
        ProCandrV4.plugin.games.get(map).setJob(p, this);

        ItemStack sheers = new ItemStack(Material.SHEARS, 1);
        ItemMeta meta = sheers.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Pliers");
        meta.setLore(Arrays.asList("Step 1) Take sheets from beds", "Step 2) Put sheets into washer/dryer in the laundryroom (the furnaces)", "Step 3) Take sheets out of washer/dryer", "Step 4) Put sheets back on beds."));
        sheers.setItemMeta(meta);

        p.getInventory().addItem(sheers);
        
        FileConfiguration cfg = ProCandrV4.plugin.getConfig();
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("messages.laundrymanRobbers.firstLine")) + " " + ChatColor.translateAlternateColorCodes('&', cfg.getString("messages.laundrymanRobbers.secondLine")));
        p.sendTitle(ChatColor.translateAlternateColorCodes('&', cfg.getString("titles.laundrymanRobbers.firstLine")), ChatColor.translateAlternateColorCodes('&', cfg.getString("titles.laundrymanRobbers.secondLine")), 0, 100, 40);
        /*
        p.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "You have been given the job of the Laundryman! Make sure to fill up your exp bar by washing bed sheets to get some extra money!");
        p.sendMessage(ChatColor.DARK_GREEN + "" + ChatColor.ITALIC + "To get sheets off of beds, have your pliers out (shears) and right click the beds to retrieved the dirty sheets, you can only take 5 at a " +
                "time. Then, take them to the laundry room to get them cleaned. Right click the cleaning machine (does washing and drying at the same time) to wash the sheets, then take them out once they are " +
                "done. Finally, return them to the beds you plied them from and right click the beds with the clean sheets.");
                */
    }

    @Override
    public void resetAndRemoveJob() { //resets the player's job and removes them
        ProCandrV4.plugin.games.get(map).removeJob(p);
        resetFurnaces();
        resetAllWhiteBedsToRedBeds();
    }

    @Override
    public void handleEvent(PlayerInteractEvent e) { //LaundryManInteractWithShears
        if ((e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK) && e.getClickedBlock().getType() == Material.FURNACE) {
            if (p.getInventory().getItemInMainHand() == null || p.getInventory().getItemInMainHand().getType() != Material.SHEARS) {
                Furnace furn = getFurnace(e.getClickedBlock().getLocation());
                if (furnaceIsWashingAndDryerMachine(e.getClickedBlock().getLocation()) == false)
                    return;
                if (furn.getSmelting() != null) {
                    p.sendMessage(ChatColor.RED + "Still washing and drying!");
                    furn.setLit(p, true);
                    return;
                }
                if (furn.getResult() != null) {
                    p.sendMessage(ChatColor.RED + "Must use shears to grab clean sheets.");
                    return;
                }
                p.sendMessage(ChatColor.RED + "Must use shears to interact with the washing and drying machine!");
            }
        }
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || (p.getInventory().getItemInMainHand() != null && p.getInventory().getItemInMainHand().getType() != Material.SHEARS))
            return;
        if (e.getClickedBlock().getType() != Material.RED_BED && e.getClickedBlock().getType() != Material.WHITE_BED && e.getClickedBlock().getType() != Material.FURNACE) {
            p.sendMessage(ChatColor.RED + "You can only take sheets from beds!");
            return;
        }
        e.setCancelled(true);
        Block bed = e.getClickedBlock();
        ItemStack dirtySheets = new ItemStack(Material.RED_CONCRETE_POWDER, 1);
        ItemMeta meta = dirtySheets.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Dirty Sheets");
        dirtySheets.setItemMeta(meta);
        ItemStack cleanSheets = new ItemStack(Material.RED_CONCRETE, 1);
        meta = cleanSheets.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Clean Sheets");
        cleanSheets.setItemMeta(meta);

        if (bed.getType().toString().contains("_BED")) {
            boolean bedHasNoSheets = bedIsEmpty(e.getClickedBlock().getLocation());
            if (bedHasNoSheets) {
                if (p.getInventory().getItem(8) == null || p.getInventory().getItem(8).getType() != Material.RED_CONCRETE) {
                    changeBedSheetsToWhite(e.getClickedBlock());
                    p.sendMessage(ChatColor.RED + "You do not have any clean sheets in your inventory! Make sure to go to the laundry room to clean dirty sheets.");
                    return;
                }
                String bDataFirst = changeBedSheetsToRed(e.getClickedBlock());
                if (bDataFirst.equalsIgnoreCase("none")) {
                    p.sendMessage(ChatColor.RED + "These are clean sheets!!");
                    return;
                }
                p.getInventory().getItem(8).setAmount(p.getInventory().getItem(8).getAmount() - 1);
                giveExp();
                return;
            }

            if (p.getInventory().getItem(7) == null) {
                p.getInventory().setItem(7, dirtySheets);
                changeBedSheetsToWhite(e.getClickedBlock());
                return;
            }
            if (p.getInventory().getItem(7).getType() == Material.RED_CONCRETE_POWDER) {
                if (p.getInventory().getItem(7).getAmount() >= 5) {
                    p.sendMessage(ChatColor.RED + "You cannot hold more than 5 dirty sheets at a time, clean your dirty sheets in the laundry room!");
                    return;
                }
                p.getInventory().getItem(7).setAmount(p.getInventory().getItem(7).getAmount() + 1);
                changeBedSheetsToWhite(e.getClickedBlock());
                return;
            }
            if (p.getInventory().getItem(7) != null) {
                ItemStack oldItem = p.getInventory().getItem(7);
                p.getInventory().setItem(7, dirtySheets);
                p.getInventory().addItem(oldItem);
                changeBedSheetsToWhite(e.getClickedBlock());
                return;
            }
            return;
        }

        Furnace furn = getFurnace(e.getClickedBlock().getLocation());
        if (furnaceIsWashingAndDryerMachine(e.getClickedBlock().getLocation()) == false) {
            p.sendMessage(ChatColor.RED + "This is not a washing/dryer machine, washing/dryer machines are located in the laundry room!");
            return;
        }
        if (furn.getSmelting() != null) {
            p.sendMessage(ChatColor.RED + "There's still a dirty sheet in this washer/dryer!");
            furn.setLit(p, true);
            return;
        }
        if (furn.getResult() != null) {
            furn.clearInventory(p);
            removeFurnace(furn.getLocation());
            if (p.getInventory().contains(dirtySheets)) {
                furn = getFurnace(e.getClickedBlock().getLocation());
                putDirtySheetsIn(furn);
                furn.setLit(p, true);
            }
            if (p.getInventory().getItem(8) == null) {
                p.getInventory().setItem(8, cleanSheets);
                return;
            }
            if (p.getInventory().getItem(8) != null && p.getInventory().getItem(8).getType() != Material.RED_CONCRETE) {
                ItemStack oldItem = p.getInventory().getItem(8);
                p.getInventory().setItem(8, cleanSheets);
                p.getInventory().addItem(oldItem);
                return;
            }
            p.getInventory().getItem(8).setAmount(p.getInventory().getItem(8).getAmount() + 1);
            return;
        }
        if (p.getInventory().getItem(7) == null || p.getInventory().getItem(7).getType() != Material.RED_CONCRETE_POWDER) {
            p.sendMessage(ChatColor.RED + "You do not have anymore dirty sheets to put in!");
            return;
        }
        putDirtySheetsIn(furn);
    }

    private void putDirtySheetsIn(Furnace furn) {
        ItemStack dirtySheets = new ItemStack(Material.RED_CONCRETE_POWDER, 1);
        ItemMeta meta = dirtySheets.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Dirty Sheets");
        dirtySheets.setItemMeta(meta);
        ItemStack cleanSheets = new ItemStack(Material.RED_CONCRETE, 1);
        meta = cleanSheets.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Clean Sheets");
        cleanSheets.setItemMeta(meta);
        p.getInventory().getItem(7).setAmount(p.getInventory().getItem(7).getAmount() - 1);

        furn.setSmelting(p, dirtySheets, cleanSheets, 200L);
    }

    private void giveExp() {
        p.setLevel(p.getLevel() + 1);
        if (p.getLevel() == 5) {
            p.sendMessage(ChatColor.GOLD + "You have reached your sheet cleaning goal of 5!");
            p.setLevel(0);
            p.getInventory().remove(Material.SHEARS);

            ItemStack key = new ItemStack(Material.TRIPWIRE_HOOK, 1);
            ItemMeta meta = key.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + "Green Key");
            meta.setLore(Arrays.asList(ChatColor.GREEN + "Use this key at the green door somewhere in the prison!"));
            key.setItemMeta(meta);

            p.getInventory().addItem(key);
            p.sendMessage(ChatColor.GOLD + "You have been given the green key to the green door somewhere in the prison!");
        }
    }

    private Boolean furnaceIsWashingAndDryerMachine(Location loc) {
        FileConfiguration MC = new MapsConfig(map).getConfig();
        if (ProCandrV4.plugin.getList("washersAndDryers", MC).contains(ProCandrV4.plugin.getStringFLocation(loc, false)))
            return true;
        return false;
    }

    private void addEmptiedBed(Location loc1, Location loc2) {
        emptiedBeds.add(loc1);
        emptiedBeds.add(loc2);
    }

    private void removeEmptiedBed(Location loc1, Location loc2) {
        if (emptiedBeds.contains(loc1))
            emptiedBeds.remove(loc1);
        if (emptiedBeds.contains(loc2))
            emptiedBeds.remove(loc2);
    }

    private Boolean bedIsEmpty(Location loc) {
        if (emptiedBeds.contains(loc))
            return true;
        return false;
    }

    private String changeBedSheetsToRed(Block clickedBlock) {
        MapsConfig MC = new MapsConfig(map);
        String bData = "none";
        for (String bed : MC.getConfig().getConfigurationSection("beds").getKeys(false)) {
            for (String bedpart : MC.getConfig().getConfigurationSection("beds." + bed).getKeys(false)) {
                String locS = ProCandrV4.plugin.getStringFLocation(clickedBlock.getLocation(), false);
                if (MC.getConfig().getString("beds." + bed + "." + bedpart + ".loc").equalsIgnoreCase(locS)) {
                    bData = "bdata is good";
                    String bDataFirst = MC.getConfig().getString("beds." + bed + "." + bedpart + ".blockData");
                    String bDataSecond;
                    Location firstLoc = ProCandrV4.plugin.getLocationFString(MC.getConfig().getString("beds." + bed + "." + bedpart + ".loc"), false);
                    Location secondLoc;
                    if (bedpart.equalsIgnoreCase("foot")) {
                        bDataSecond = MC.getConfig().getString("beds." + bed + ".head.blockData");
                        secondLoc = ProCandrV4.plugin.getLocationFString(MC.getConfig().getString("beds." + bed + ".head.loc"), false);
                    } else {
                        bDataSecond = MC.getConfig().getString("beds." + bed + ".foot.blockData");
                        secondLoc = ProCandrV4.plugin.getLocationFString(MC.getConfig().getString("beds." + bed + ".foot.loc"), false);
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            p.sendBlockChange(firstLoc, Bukkit.createBlockData(bDataFirst));
                            p.sendBlockChange(secondLoc, Bukkit.createBlockData(bDataSecond));
                            removeEmptiedBed(firstLoc, secondLoc);
                        }
                    }.runTaskLater(ProCandrV4.plugin, 2L);
                    break;
                }
            }
        }
        return bData;
    }

    private String changeBedSheetsToWhite(Block clickedBlock) {
        MapsConfig MC = new MapsConfig(map);
        String bData = "none";
        for (String bed : MC.getConfig().getConfigurationSection("beds").getKeys(false)) {
            for (String bedpart : MC.getConfig().getConfigurationSection("beds." + bed).getKeys(false)) {
                String locS = ProCandrV4.plugin.getStringFLocation(clickedBlock.getLocation(), false);
                if (MC.getConfig().getString("beds." + bed + "." + bedpart + ".loc").equalsIgnoreCase(locS)) {
                    bData = "bdata is good";
                    String bDataFirst = MC.getConfig().getString("beds." + bed + "." + bedpart + ".blockData").replaceFirst("red_", "white_");
                    String bDataSecond;
                    Location firstLoc = ProCandrV4.plugin.getLocationFString(MC.getConfig().getString("beds." + bed + "." + bedpart + ".loc"), false);
                    Location secondLoc;
                    if (bedpart.equalsIgnoreCase("foot")) {
                        bDataSecond = MC.getConfig().getString("beds." + bed + ".head.blockData").replaceFirst("red_", "white_");
                        secondLoc = ProCandrV4.plugin.getLocationFString(MC.getConfig().getString("beds." + bed + ".head.loc"), false);
                    } else {
                        bDataSecond = MC.getConfig().getString("beds." + bed + ".foot.blockData").replaceFirst("red_", "white_");
                        secondLoc = ProCandrV4.plugin.getLocationFString(MC.getConfig().getString("beds." + bed + ".foot.loc"), false);
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            p.sendBlockChange(firstLoc, Bukkit.createBlockData(bDataFirst));
                            p.sendBlockChange(secondLoc, Bukkit.createBlockData(bDataSecond));
                            addEmptiedBed(firstLoc, secondLoc);
                        }
                    }.runTaskLater(ProCandrV4.plugin, 2L);
                    break;
                }
            }
        }
        return bData;
    }

    private void resetAllWhiteBedsToRedBeds() {
        MapsConfig MC = new MapsConfig(map);
        if (emptiedBeds.size() == 0)
            return;
        List<String> bedsToChangeBack = new ArrayList<>();
        for (String bed : MC.getConfig().getConfigurationSection("beds").getKeys(false)) {
            for (String bedpart : MC.getConfig().getConfigurationSection("beds." + bed).getKeys(false)) {
                if (emptiedBeds.contains(ProCandrV4.plugin.getLocationFString(MC.getConfig().getString("beds." + bed + "." + bedpart + ".loc"), false))) {
                    String bDataFirst = MC.getConfig().getString("beds." + bed + "." + bedpart + ".blockData");
                    Location firstLoc = ProCandrV4.plugin.getLocationFString(MC.getConfig().getString("beds." + bed + "." + bedpart + ".loc"), false);
                    bedsToChangeBack.add(ProCandrV4.plugin.getStringFLocation(firstLoc, false) + "|" + bDataFirst);
                }
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (p != null && p.isOnline()) {
                    for (String itemToSplit : bedsToChangeBack) {
                        if (itemToSplit.contains("|") && itemToSplit.split("|").length == 2) {
                            Location loc = ProCandrV4.plugin.getLocationFString(itemToSplit.split("|")[0], false);
                            String bData = itemToSplit.split("|")[1];
                            p.sendBlockChange(loc, Bukkit.createBlockData(bData));
                        }
                    }
                }
                if (p != null)
                    emptiedBeds.remove(p.getName());
            }
        }.runTaskLater(ProCandrV4.plugin, 2L);
    }

    private void resetFurnaces() {
        MapsConfig MC = new MapsConfig(map);
        for (String furnace : MC.getConfig().getStringList("washersAndDryers")) {
            Location key = ProCandrV4.plugin.getLocationFString(furnace, false);
            if (furnaces.containsKey(key)) {
                if (p != null && p.isOnline())
                    furnaces.get(key).setLit(p, false);
                furnaces.remove(key);
            }
        }
    }

    private Furnace getFurnace(Location loc) {
        if (furnaces.containsKey(loc))
            return furnaces.get(loc);
        furnaces.put(loc, new Furnace(loc));
        return furnaces.get(loc);
    }

    private void removeFurnace(Location loc) {
        if (furnaces.containsKey(loc))
            furnaces.remove(loc);
    }
}
