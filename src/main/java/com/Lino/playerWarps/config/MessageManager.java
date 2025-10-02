package com.Lino.playerWarps.config;

import com.Lino.playerWarps.PlayerWarps;
import com.Lino.playerWarps.utils.ColorUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MessageManager {

    private final PlayerWarps plugin;
    private File messagesFile;
    private FileConfiguration messagesConfig;

    public MessageManager(PlayerWarps plugin) {
        this.plugin = plugin;
    }

    public void loadMessages() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public String getMessage(String key) {
        String message = messagesConfig.getString(key, "&cMessage not found: " + key);
        return ColorUtils.applyGradient(message);
    }

    public String getRawMessage(String key) {
        return messagesConfig.getString(key, "Message not found: " + key);
    }
}