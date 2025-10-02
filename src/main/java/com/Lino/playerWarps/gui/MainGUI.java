package com.Lino.playerWarps.gui;

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
        inventory = Bukkit.createInventory(null, plugin.getConfigManager().getGuiSize(), title);

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
                ItemStack item = createWarpItem(warp, true);
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
        ItemStack allWarps = new ItemStack(Material.COMPASS);
        ItemMeta allWarpsMeta = allWarps.getItemMeta();
        allWarpsMeta.setDisplayName(ColorUtils.applyGradient(plugin.getMessageManager().getRawMessage("all-warps-button")));
        allWarps.setItemMeta(allWarpsMeta);
        inventory.setItem(49, allWarps);

        ItemStack editWarps = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta editWarpsMeta = editWarps.getItemMeta();
        editWarpsMeta.setDisplayName(ColorUtils.applyGradient(plugin.getMessageManager().getRawMessage("edit-warps-button")));
        editWarps.setItemMeta(editWarpsMeta);
        inventory.setItem(45, editWarps);
    }

    private ItemStack createWarpItem(Warp warp, boolean sponsored) {
        ItemStack item = new ItemStack(warp.getIcon());
        ItemMeta meta = item.getItemMeta();

        String displayName = ColorUtils.applyGradient(plugin.getMessageManager().getRawMessage("warp-display-name")
                .replace("{warp}", warp.getName()));
        meta.setDisplayName(displayName);

        List<String> lore = new ArrayList<>();

        if (sponsored) {
            lore.add(ColorUtils.applyGradient(plugin.getMessageManager().getRawMessage("sponsored-tag")));
            lore.add("");
        }

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

    public void open() {
        player.openInventory(inventory);
    }

    public Inventory getInventory() {
        return inventory;
    }
}