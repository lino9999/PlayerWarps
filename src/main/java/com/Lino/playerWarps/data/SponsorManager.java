package com.Lino.playerWarps.data;

import com.Lino.playerWarps.PlayerWarps;
import com.Lino.playerWarps.models.Sponsor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SponsorManager {

    private final PlayerWarps plugin;
    private final File sponsorsFile;
    private FileConfiguration sponsorsConfig;
    private final List<Sponsor> sponsors;

    public SponsorManager(PlayerWarps plugin) {
        this.plugin = plugin;
        this.sponsorsFile = new File(plugin.getDataFolder(), "sponsors.yml");
        this.sponsors = new ArrayList<>();
        loadSponsors();
    }

    public void loadSponsors() {
        if (!sponsorsFile.exists()) {
            try {
                sponsorsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        sponsorsConfig = YamlConfiguration.loadConfiguration(sponsorsFile);
        sponsors.clear();

        ConfigurationSection sponsorsSection = sponsorsConfig.getConfigurationSection("sponsors");
        if (sponsorsSection == null) {
            return;
        }

        for (String key : sponsorsSection.getKeys(false)) {
            String warpName = sponsorsSection.getString(key + ".warp");
            long expirationTime = sponsorsSection.getLong(key + ".expiration");

            if (System.currentTimeMillis() < expirationTime) {
                sponsors.add(new Sponsor(warpName, expirationTime));
            }
        }

        saveSponsors();
    }

    public void saveSponsors() {
        sponsorsConfig.set("sponsors", null);

        for (int i = 0; i < sponsors.size(); i++) {
            Sponsor sponsor = sponsors.get(i);
            String path = "sponsors." + i;
            sponsorsConfig.set(path + ".warp", sponsor.getWarpName());
            sponsorsConfig.set(path + ".expiration", sponsor.getExpirationTime());
        }

        try {
            sponsorsConfig.save(sponsorsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addSponsor(String warpName, int hours) {
        long expirationTime = System.currentTimeMillis() + (hours * 3600000L);
        sponsors.add(new Sponsor(warpName, expirationTime));
        saveSponsors();
    }

    public void removeSponsor(String warpName) {
        sponsors.removeIf(sponsor -> sponsor.getWarpName().equalsIgnoreCase(warpName));
        saveSponsors();
    }

    public boolean isSponsored(String warpName) {
        return sponsors.stream().anyMatch(sponsor -> sponsor.getWarpName().equalsIgnoreCase(warpName));
    }

    public List<Sponsor> getActiveSponsors() {
        sponsors.removeIf(sponsor -> sponsor.isExpired());
        saveSponsors();
        return new ArrayList<>(sponsors);
    }

    public int getAvailableSlots() {
        int maxSlots = plugin.getConfigManager().getSponsorSlots();
        return maxSlots - getActiveSponsors().size();
    }
}