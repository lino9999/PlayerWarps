package com.Lino.playerWarps.gui;

import com.Lino.playerWarps.PlayerWarps;
import com.Lino.playerWarps.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class InfoGUI {

    private final PlayerWarps plugin;
    private final Player player;
    private Inventory inventory;

    public InfoGUI(PlayerWarps plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        createInventory();
    }

    private void createInventory() {
        String title = ColorUtils.applyGradient(plugin.getConfigManager().getInfoGuiTitle());
        inventory = Bukkit.createInventory(null, 27, title);

        addInfoItems();
        addBackButton();
    }

    private void addInfoItems() {
        ItemStack createInfo = new ItemStack(Material.ENDER_PEARL);
        ItemMeta createMeta = createInfo.getItemMeta();
        createMeta.setDisplayName(ColorUtils.applyGradient(plugin.getMessageManager().getRawMessage("info-create-title")));

        List<String> createLore = new ArrayList<>();
        String createLoreText = plugin.getMessageManager().getRawMessage("info-create-lore")
                .replace("{max}", String.valueOf(plugin.getConfigManager().getMaxWarpsPerPlayer()));
        for (String line : createLoreText.split("\\|")) {
            createLore.add(ColorUtils.applyGradient("&7" + line));
        }
        createMeta.setLore(createLore);
        createInfo.setItemMeta(createMeta);
        inventory.setItem(10, createInfo);

        ItemStack editInfo = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta editMeta = editInfo.getItemMeta();
        editMeta.setDisplayName(ColorUtils.applyGradient(plugin.getMessageManager().getRawMessage("info-edit-title")));

        List<String> editLore = new ArrayList<>();
        String editLoreText = plugin.getMessageManager().getRawMessage("info-edit-lore");
        for (String line : editLoreText.split("\\|")) {
            editLore.add(ColorUtils.applyGradient("&7" + line));
        }
        editMeta.setLore(editLore);
        editInfo.setItemMeta(editMeta);
        inventory.setItem(12, editInfo);

        ItemStack sponsorInfo = new ItemStack(Material.GOLD_INGOT);
        ItemMeta sponsorMeta = sponsorInfo.getItemMeta();
        sponsorMeta.setDisplayName(ColorUtils.applyGradient(plugin.getMessageManager().getRawMessage("info-sponsor-title")));

        List<String> sponsorLore = new ArrayList<>();
        String sponsorLoreText = plugin.getMessageManager().getRawMessage("info-sponsor-lore")
                .replace("{price}", String.valueOf(plugin.getConfigManager().getSponsorPrice()))
                .replace("{duration}", String.valueOf(plugin.getConfigManager().getSponsorDurationHours()))
                .replace("{slots}", String.valueOf(plugin.getConfigManager().getSponsorSlots()));
        for (String line : sponsorLoreText.split("\\|")) {
            sponsorLore.add(ColorUtils.applyGradient("&7" + line));
        }
        sponsorMeta.setLore(sponsorLore);
        sponsorInfo.setItemMeta(sponsorMeta);
        inventory.setItem(14, sponsorInfo);

        ItemStack tipsInfo = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta tipsMeta = tipsInfo.getItemMeta();
        tipsMeta.setDisplayName(ColorUtils.applyGradient(plugin.getMessageManager().getRawMessage("info-tips-title")));

        List<String> tipsLore = new ArrayList<>();
        String tipsLoreText = plugin.getMessageManager().getRawMessage("info-tips-lore")
                .replace("{max}", String.valueOf(plugin.getConfigManager().getMaxWarpsPerPlayer()));
        for (String line : tipsLoreText.split("\\|")) {
            tipsLore.add(ColorUtils.applyGradient("&7" + line));
        }
        tipsMeta.setLore(tipsLore);
        tipsInfo.setItemMeta(tipsMeta);
        inventory.setItem(16, tipsInfo);
    }

    private void addBackButton() {
        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName(ColorUtils.applyGradient(plugin.getMessageManager().getRawMessage("back-button")));
        backButton.setItemMeta(backMeta);
        inventory.setItem(22, backButton);
    }

    public void open() {
        player.openInventory(inventory);
    }

    public Inventory getInventory() {
        return inventory;
    }
}