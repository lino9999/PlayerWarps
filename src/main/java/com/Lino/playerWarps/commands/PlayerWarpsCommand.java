package com.Lino.playerWarps.commands;

import com.Lino.playerWarps.PlayerWarps;
import com.Lino.playerWarps.models.Warp;
import com.Lino.playerWarps.gui.MainGUI;
import com.Lino.playerWarps.gui.EditWarpsGUI;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerWarpsCommand implements CommandExecutor {

    private final PlayerWarps plugin;

    public PlayerWarpsCommand(PlayerWarps plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            new MainGUI(plugin, player, 0).open();
            return true;
        }

        if (args[0].equalsIgnoreCase("create")) {
            if (args.length < 2) {
                player.sendMessage(plugin.getMessageManager().getMessage("usage-create"));
                return true;
            }

            String warpName = args[1];

            if (plugin.getWarpManager().warpExists(warpName)) {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                player.sendMessage(plugin.getMessageManager().getMessage("warp-already-exists"));
                return true;
            }

            int maxWarps = plugin.getConfigManager().getMaxWarpsPerPlayer();
            int playerWarps = plugin.getWarpManager().getPlayerWarpsCount(player.getUniqueId());

            if (playerWarps >= maxWarps) {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                player.sendMessage(plugin.getMessageManager().getMessage("max-warps-reached")
                        .replace("{max}", String.valueOf(maxWarps)));
                return true;
            }

            Warp warp = new Warp(warpName, player.getUniqueId(), player.getLocation());
            plugin.getWarpManager().addWarp(warp);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

            String message = plugin.getMessageManager().getMessage("warp-created");
            message = message.replace("{warp}", warpName);
            player.sendMessage(message);
            return true;
        }

        if (args[0].equalsIgnoreCase("edit")) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            new EditWarpsGUI(plugin, player, 0).open();
            return true;
        }

        player.sendMessage(plugin.getMessageManager().getMessage("unknown-command"));
        return true;
    }
}