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

public class AllWarpsGUI {

    private final PlayerWarps plugin;
    private final Player player;
    private final int page;
    private Inventory inventory;
    private static final int ITEMS_PER_PAGE = 45;

    public AllWarpsGUI(PlayerWarps plugin, Player player, int page) {
        this.plugin = plugin;
        this.player = player;
        this.page = page;
        createInventory();
    }

    private void createInventory() {
        String title = ColorUtils.applyGradient(plugin.getConfigManager().getAllWarpsGuiTitle() + " - Page " + (page + 1));
        inventory = Bukkit.createInventory(null, 54, title);

        List<Warp> allWarps = plugin.getWarpManager().getAllWarps();
        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, allWarps.size());

        for (int i = startIndex; i < endIndex; i++) {
            Warp warp = allWarps.get(i);
            ItemStack item = createWarpItem(warp);
            inventory.setItem(i - startIndex, item);
        }

        addNavigationButtons(allWarps.size());
    }

    private ItemStack createWarpItem(Warp warp) {
        ItemStack item = new ItemStack(warp.getIcon());
        ItemMeta meta = item.getItemMeta();

        String displayName = ColorUtils.applyGradient(plugin.getMessageManager().getRawMessage("warp-display-name")
                .replace("{warp}", warp.getName()));
        meta.setDisplayName(displayName);

        List<String> lore = new ArrayList<>();
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

    private void addNavigationButtons(int totalWarps) {
        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName(ColorUtils.applyGradient(plugin.getMessageManager().getRawMessage("back-button")));
        backButton.setItemMeta(backMeta);
        inventory.setItem(45, backButton);

        int totalPages = (int) Math.ceil((double) totalWarps / ITEMS_PER_PAGE);

        if (page > 0) {
            ItemStack previousPage = new ItemStack(Material.ARROW);
            ItemMeta previousMeta = previousPage.getItemMeta();
            previousMeta.setDisplayName(ColorUtils.applyGradient(plugin.getMessageManager().getRawMessage("previous-page")));
            previousPage.setItemMeta(previousMeta);
            inventory.setItem(48, previousPage);
        }

        if (page < totalPages - 1) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextPage.getItemMeta();
            nextMeta.setDisplayName(ColorUtils.applyGradient(plugin.getMessageManager().getRawMessage("next-page")));
            nextPage.setItemMeta(nextMeta);
            inventory.setItem(50, nextPage);
        }
    }

    public void open() {
        player.openInventory(inventory);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getPage() {
        return page;
    }
}