package com.Lino.playerWarps.listeners;

import com.Lino.playerWarps.PlayerWarps;
import com.Lino.playerWarps.gui.EditWarpsGUI;
import com.Lino.playerWarps.models.Warp;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class ChatListener implements Listener {

    private final PlayerWarps plugin;
    private final GUIListener guiListener;

    public ChatListener(PlayerWarps plugin, GUIListener guiListener) {
        this.plugin = plugin;
        this.guiListener = guiListener;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (guiListener.getEditingDescription().containsKey(playerId)) {
            event.setCancelled(true);

            String warpName = guiListener.getEditingDescription().get(playerId);
            String description = event.getMessage();

            Warp warp = plugin.getWarpManager().getWarp(warpName);
            if (warp != null && warp.getOwner().equals(playerId)) {
                warp.setDescription(description);
                plugin.getWarpManager().saveWarps();

                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                player.sendMessage(plugin.getMessageManager().getMessage("description-changed"));

                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    new EditWarpsGUI(plugin, player, 0).open();
                });
            }

            guiListener.getEditingDescription().remove(playerId);
        }
    }
}