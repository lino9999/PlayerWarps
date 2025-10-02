package com.Lino.playerWarps.managers;

import com.Lino.playerWarps.PlayerWarps;
import com.Lino.playerWarps.models.Warp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class WarpManager {

    private final PlayerWarps plugin;
    private final File warpsFile;
    private FileConfiguration warpsConfig;
    private final Map<String, Warp> warps;

    public WarpManager(PlayerWarps plugin) {
        this.plugin = plugin;
        this.warpsFile = new File(plugin.getDataFolder(), "warps.yml");
        this.warps = new HashMap<>();
    }

    public void loadWarps() {
        if (!warpsFile.exists()) {
            plugin.saveResource("warps.yml", false);
        }

        warpsConfig = YamlConfiguration.loadConfiguration(warpsFile);

        ConfigurationSection warpsSection = warpsConfig.getConfigurationSection("warps");
        if (warpsSection == null) {
            return;
        }

        for (String key : warpsSection.getKeys(false)) {
            String name = warpsSection.getString(key + ".name");
            UUID owner = UUID.fromString(warpsSection.getString(key + ".owner"));
            String worldName = warpsSection.getString(key + ".location.world");
            double x = warpsSection.getDouble(key + ".location.x");
            double y = warpsSection.getDouble(key + ".location.y");
            double z = warpsSection.getDouble(key + ".location.z");
            float yaw = (float) warpsSection.getDouble(key + ".location.yaw");
            float pitch = (float) warpsSection.getDouble(key + ".location.pitch");

            Location location = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
            Warp warp = new Warp(name, owner, location);

            String description = warpsSection.getString(key + ".description", "");
            warp.setDescription(description);

            String iconName = warpsSection.getString(key + ".icon", "ENDER_PEARL");
            try {
                warp.setIcon(Material.valueOf(iconName));
            } catch (IllegalArgumentException e) {
                warp.setIcon(Material.ENDER_PEARL);
            }

            warps.put(name.toLowerCase(), warp);
        }
    }

    public void saveWarps() {
        warpsConfig.set("warps", null);

        int index = 0;
        for (Warp warp : warps.values()) {
            String path = "warps." + index;
            warpsConfig.set(path + ".name", warp.getName());
            warpsConfig.set(path + ".owner", warp.getOwner().toString());
            warpsConfig.set(path + ".location.world", warp.getLocation().getWorld().getName());
            warpsConfig.set(path + ".location.x", warp.getLocation().getX());
            warpsConfig.set(path + ".location.y", warp.getLocation().getY());
            warpsConfig.set(path + ".location.z", warp.getLocation().getZ());
            warpsConfig.set(path + ".location.yaw", warp.getLocation().getYaw());
            warpsConfig.set(path + ".location.pitch", warp.getLocation().getPitch());
            warpsConfig.set(path + ".description", warp.getDescription());
            warpsConfig.set(path + ".icon", warp.getIcon().name());
            index++;
        }

        try {
            warpsConfig.save(warpsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addWarp(Warp warp) {
        warps.put(warp.getName().toLowerCase(), warp);
        saveWarps();
    }

    public void removeWarp(String name) {
        warps.remove(name.toLowerCase());
        saveWarps();
    }

    public Warp getWarp(String name) {
        return warps.get(name.toLowerCase());
    }

    public boolean warpExists(String name) {
        return warps.containsKey(name.toLowerCase());
    }

    public List<Warp> getAllWarps() {
        return new ArrayList<>(warps.values());
    }

    public List<Warp> getPlayerWarps(UUID owner) {
        return warps.values().stream()
                .filter(warp -> warp.getOwner().equals(owner))
                .collect(Collectors.toList());
    }

    public int getPlayerWarpsCount(UUID owner) {
        return (int) warps.values().stream()
                .filter(warp -> warp.getOwner().equals(owner))
                .count();
    }
}