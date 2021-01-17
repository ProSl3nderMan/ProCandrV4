package me.prosl3nderman.procandrv4;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.logging.Level;

public class ItemsConfig {
    private FileConfiguration customConfig = null;
    private File customConfigFile = null;
    private String dir = ProCandrV4.plugin.getDataFolder() + "";

    public void reloadConfig() {
        if (customConfigFile == null) {
            customConfigFile = new File(dir, "items.yml");
        }
        if (!customConfigFile.exists()) {
            ProCandrV4.plugin.saveResource("items.yml", false);
        }
        customConfig = YamlConfiguration.loadConfiguration(customConfigFile);

        Reader defConfigStream = null;
        try {
            defConfigStream = new InputStreamReader(ProCandrV4.plugin.getResource("items.yml"), "UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            customConfig.setDefaults(defConfig);
        }
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
}
