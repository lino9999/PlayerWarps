package com.Lino.playerWarps.config;

import com.Lino.playerWarps.PlayerWarps;
import com.Lino.playerWarps.utils.ColorUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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

    public void reloadMessages() {
        if (messagesFile == null) {
            messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public String getMessage(String key) {
        String message = messagesConfig.getString(key);
        if (message == null) {
            return ColorUtils.applyGradient("&cMessage not found: " + key);
        }
        return ColorUtils.applyGradient(message);
    }

    public String getMessage(String key, Map<String, String> replacements) {
        String message = messagesConfig.getString(key);
        if (message == null) {
            return ColorUtils.applyGradient("&cMessage not found: " + key);
        }

        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            message = message.replace(entry.getKey(), entry.getValue());
        }

        return ColorUtils.applyGradient(message);
    }

    public String getRawMessage(String key) {
        String message = messagesConfig.getString(key);
        if (message == null) {
            return "Message not found: " + key;
        }
        return message;
    }
}