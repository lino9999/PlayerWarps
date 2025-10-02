package com.Lino.playerWarps;

import com.Lino.playerWarps.commands.PlayerWarpsCommand;
import com.Lino.playerWarps.config.ConfigManager;
import com.Lino.playerWarps.config.MessageManager;
import com.Lino.playerWarps.data.WarpManager;
import com.Lino.playerWarps.data.SponsorManager;
import com.Lino.playerWarps.listeners.GUIListener;
import com.Lino.playerWarps.listeners.ChatListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerWarps extends JavaPlugin {

    private static PlayerWarps instance;
    private Economy economy;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private WarpManager warpManager;
    private SponsorManager sponsorManager;

    @Override
    public void onEnable() {
        instance = this;

        if (!setupEconomy()) {
            getLogger().severe("Vault not found! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        configManager = new ConfigManager(this);
        messageManager = new MessageManager(this);
        warpManager = new WarpManager(this);
        sponsorManager = new SponsorManager(this);

        configManager.loadConfig();
        messageManager.loadMessages();
        warpManager.loadWarps();

        getCommand("pw").setExecutor(new PlayerWarpsCommand(this));

        GUIListener guiListener = new GUIListener(this);
        getServer().getPluginManager().registerEvents(guiListener, this);
        getServer().getPluginManager().registerEvents(new ChatListener(this, guiListener), this);

        getLogger().info("PlayerWarps has been enabled!");
    }

    @Override
    public void onDisable() {
        if (warpManager != null) {
            warpManager.saveWarps();
        }
        getLogger().info("PlayerWarps has been disabled!");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public static PlayerWarps getInstance() {
        return instance;
    }

    public Economy getEconomy() {
        return economy;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }

    public SponsorManager getSponsorManager() {
        return sponsorManager;
    }
}