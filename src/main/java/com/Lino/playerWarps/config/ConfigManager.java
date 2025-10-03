package com.Lino.playerWarps.config;

import com.Lino.playerWarps.PlayerWarps;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private final PlayerWarps plugin;
    private FileConfiguration config;

    public ConfigManager(PlayerWarps plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public int getMaxWarpsPerPlayer() {
        return config.getInt("max-warps-per-player", 3);
    }

    public int getSponsorSlots() {
        return config.getInt("sponsor-slots", 9);
    }

    public double getSponsorPrice() {
        return config.getDouble("sponsor-price", 10000.0);
    }

    public int getSponsorDurationHours() {
        return config.getInt("sponsor-duration-hours", 168);
    }

    public String getGuiTitle() {
        return config.getString("gui-title", "Player Warps");
    }

    public String getAllWarpsGuiTitle() {
        return config.getString("all-warps-gui-title", "All Warps");
    }

    public String getEditWarpsGuiTitle() {
        return config.getString("edit-warps-gui-title", "Edit Your Warps");
    }

    public String getInfoGuiTitle() {
        return config.getString("info-gui-title", "Information & Help");
    }

    public int getGuiSize() {
        return config.getInt("gui-size", 54);
    }
}