package com.Lino.playerWarps.gui;

import com.Lino.playerWarps.PlayerWarps;
import com.Lino.playerWarps.models.Warp;
import com.Lino.playerWarps.models.Sponsor;
import com.Lino.playerWarps.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MainGUI {

    private final PlayerWarps plugin;
    private final Player player;
    private final int page;
    private Inventory inventory;

    public MainGUI(PlayerWarps plugin, Player player, int page) {
        this.plugin = plugin;
        this.player = player;
        this.page = page;
        createInventory();
    }

    private void createInventory() {
        String title = ColorUtils.applyGradient(plugin.getConfigManager().getGuiTitle());
        int sponsorSlots = plugin.getConfigManager().getSponsorSlots();
        int guiSize = 27;

        if (sponsorSlots > 9) {
            guiSize = 36;
        }
        if (sponsorSlots > 18) {
            guiSize = 45;
        }

        inventory = Bukkit.createInventory(null, guiSize, title);

        addSponsoredWarps();
        addNavigationButtons();
    }

    private void addSponsoredWarps() {
        List<Sponsor> sponsors = plugin.getSponsorManager().getActiveSponsors();
        int sponsorSlots = plugin.getConfigManager().getSponsorSlots();

        for (int i = 0; i < sponsorSlots && i < sponsors.size(); i++) {
            Sponsor sponsor = sponsors.get(i);
            Warp warp = plugin.getWarpManager().getWarp(sponsor.getWarpName());

            if (warp != null) {
                ItemStack item = createWarpItem(warp, sponsor);
                inventory.setItem(i, item);
            }
        }

        for (int i = sponsors.size(); i < sponsorSlots; i++) {
            ItemStack emptySlot = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
            ItemMeta meta = emptySlot.getItemMeta();
            meta.setDisplayName(ColorUtils.applyGradient(plugin.getMessageManager().getRawMessage("sponsor-slot-available")));

            List<String> lore = new ArrayList<>();
            lore.add(ColorUtils.applyGradient(plugin.getMessageManager().getRawMessage("sponsor-slot-price")
                    .replace("{price}", String.valueOf(plugin.getConfigManager().getSponsorPrice()))));
            meta.setLore(lore);

            emptySlot.setItemMeta(meta);
            inventory.setItem(i, emptySlot);
        }
    }

    private void addNavigationButtons() {
        int guiSize = inventory.getSize();
        int lastRow = guiSize - 9;

        ItemStack editWarps = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta editWarpsMeta = editWarps.getItemMeta();
        editWarpsMeta.setDisplayName(ColorUtils.applyGradient(plugin.getMessageManager().getRawMessage("edit-warps-button")));
        editWarps.setItemMeta(editWarpsMeta);
        inventory.setItem(lastRow + 2, editWarps);

        ItemStack allWarps = new ItemStack(Material.COMPASS);
        ItemMeta allWarpsMeta = allWarps.getItemMeta();
        allWarpsMeta.setDisplayName(ColorUtils.applyGradient(plugin.getMessageManager().getRawMessage("all-warps-button")));
        allWarps.setItemMeta(allWarpsMeta);
        inventory.setItem(lastRow + 4, allWarps);

        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName(ColorUtils.applyGradient(plugin.getMessageManager().getRawMessage("info-button")));

        List<String> infoLore = new ArrayList<>();
        infoLore.add(ColorUtils.applyGradient(plugin.getMessageManager().getRawMessage("info-button-desc")));
        infoMeta.setLore(infoLore);

        info.setItemMeta(infoMeta);
        inventory.setItem(lastRow + 6, info);
    }

    private ItemStack createWarpItem(Warp warp, Sponsor sponsor) {
        ItemStack item = new ItemStack(warp.getIcon());
        ItemMeta meta = item.getItemMeta();

        String displayName = ColorUtils.applyGradient(plugin.getMessageManager().getRawMessage("warp-display-name")
                .replace("{warp}", warp.getName()));
        meta.setDisplayName(displayName);

        List<String> lore = new ArrayList<>();

        lore.add(ColorUtils.applyGradient(plugin.getMessageManager().getRawMessage("sponsored-tag")));
        lore.add("");

        long timeRemaining = sponsor.getTimeRemaining();
        String timeFormatted = formatTime(timeRemaining);
        lore.add(ColorUtils.applyGradient(plugin.getMessageManager().getRawMessage("sponsor-time-remaining")
                .replace("{time}", timeFormatted)));
        lore.add("");

        lore.add(ColorUtils.applyGradient(plugin.getMessageManager().getRawMessage("warp-owner")
                .replace("{owner}", Bukkit.getOfflinePlayer(warp.getOwner()).getName())));

        if (!warp.getDescription().isEmpty()) {
            lore.add("");
            lore.add(ColorUtils.applyGradient(warp.getDescription()));
        }

        lore.add("");
        lore.add(ColorUtils.applyGradient(plugin.getMessageManager().getRawMessage("click-to-teleport")));

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + "d " + (hours % 24) + "h";
        } else if (hours > 0) {
            return hours + "h " + (minutes % 60) + "m";
        } else if (minutes > 0) {
            return minutes + "m " + (seconds % 60) + "s";
        } else {
            return seconds + "s";
        }
    }

    public void open() {
        player.openInventory(inventory);
    }

    public Inventory getInventory() {
        return inventory;
    }
}