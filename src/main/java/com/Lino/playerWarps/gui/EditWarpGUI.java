package com.Lino.playerWarps.gui;

import com.Lino.playerWarps.PlayerWarps;
import com.Lino.playerWarps.models.Warp;
import com.Lino.playerWarps.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class EditWarpGUI {

    private final PlayerWarps plugin;
    private final Player player;
    private final Warp warp;
    private Inventory inventory;

    public EditWarpGUI(PlayerWarps plugin, Player player, Warp warp) {
        this.plugin = plugin;
        this.player = player;
        this.warp = warp;
        createInventory();
    }

    private void createInventory() {
        String title = ColorUtils.applyGradient("Edit: " + warp.getName());
        inventory = Bukkit.createInventory(null, 27, title);

        addIconButton();
        addDescriptionButton();
        addDeleteButton();
        addBackButton();
    }

    private void addIconButton() {
        ItemStack item = new ItemStack(Material.PAINTING);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ColorUtils.applyGradient(plugin.getMessageManager().getRawMessage("change-icon-button")));

        List<String> lore = new ArrayList<>();
        lore.add(ColorUtils.applyGradient(plugin.getMessageManager().getRawMessage("change-icon-desc")));
        meta.setLore(lore);

        item.setItemMeta(meta);
        inventory.setItem(11, item);
    }

    private void addDescriptionButton() {
        ItemStack item = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ColorUtils.applyGradient(plugin.getMessageManager().getRawMessage("change-description-button")));

        List<String> lore = new ArrayList<>();
        lore.add(ColorUtils.applyGradient(plugin.getMessageManager().getRawMessage("change-description-desc")));
        meta.setLore(lore);

        item.setItemMeta(meta);
        inventory.setItem(13, item);
    }

    private void addDeleteButton() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ColorUtils.applyGradient(plugin.getMessageManager().getRawMessage("delete-warp-button")));

        List<String> lore = new ArrayList<>();
        lore.add(ColorUtils.applyGradient(plugin.getMessageManager().getRawMessage("delete-warp-desc")));
        meta.setLore(lore);

        item.setItemMeta(meta);
        inventory.setItem(15, item);
    }

    private void addBackButton() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ColorUtils.applyGradient(plugin.getMessageManager().getRawMessage("back-button")));
        item.setItemMeta(meta);
        inventory.setItem(22, item);
    }

    public void open() {
        player.openInventory(inventory);
    }

    public Inventory getInventory() {
        return inventory;
    }
}