package me.prosl3nderman.procandrv4;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.logging.Level;

public class MapsConfig {
    private FileConfiguration customConfig = null;
    private File customConfigFile = null;
    private String dir = ProCandrV4.plugin.getDataFolder() + File.separator + "maps";
    private String mapName;

    public MapsConfig(String map) {
        File directory = new File(dir);
        if (! directory.exists())
            directory.mkdir();
        mapName = map;
    }

    public void reloadConfig() {
        if (customConfigFile == null) {

            customConfigFile = new File(dir, mapName + ".yml");
        }
        if (!customConfigFile.exists()) {
            try {
                customConfigFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
    }

    public FileConfiguration getConfig() {
        if (customConfig == null) {
            reloadConfig();
        }
        return customConfig;
    }

    public void saveConfig() {
        if (customConfig == null || customConfigFile == null) {
            return;
        }
        try {
            getConfig().save(customConfigFile);
        } catch (IOException ex) {
            ProCandrV4.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);
        }
    }

    public void srConfig() {
        saveConfig();
        reloadConfig();
    }

    public void delete() {
        if (customConfig == null)
            customConfigFile = new File(dir, mapName + ".yml");
        if (customConfigFile.exists()) {
            customConfigFile.delete();
            Bukkit.getLogger().log(Level.INFO, "[ProCandrV4] The file /procandrv4/maps/" + mapName + ".yml has been deleted.");
        } else
            Bukkit.getLogger().log(Level.WARNING, "[ProCandrV4] Error! No file /procandrv4/maps/" + mapName + ".yml, skipping deletion.");
    }
}
