package com.Lino.playerWarps.listeners;

import com.Lino.playerWarps.PlayerWarps;
import com.Lino.playerWarps.models.Warp;
import com.Lino.playerWarps.gui.MainGUI;
import com.Lino.playerWarps.gui.AllWarpsGUI;
import com.Lino.playerWarps.gui.EditWarpsGUI;
import com.Lino.playerWarps.gui.EditWarpGUI;
import com.Lino.playerWarps.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIListener implements Listener {

    private final PlayerWarps plugin;
    private final Map<UUID, String> editingDescription;
    private final Map<UUID, String> editingIcon;

    public GUIListener(PlayerWarps plugin) {
        this.plugin = plugin;
        this.editingDescription = new HashMap<>();
        this.editingIcon = new HashMap<>();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();

        if (title.contains(ColorUtils.applyGradient(plugin.getConfigManager().getGuiTitle()))) {
            event.setCancelled(true);
            handleMainGUIClick(player, event);
        } else if (title.contains(ColorUtils.applyGradient(plugin.getConfigManager().getAllWarpsGuiTitle()))) {
            event.setCancelled(true);
            handleAllWarpsGUIClick(player, event);
        } else if (title.contains(ColorUtils.applyGradient(plugin.getConfigManager().getEditWarpsGuiTitle()))) {
            event.setCancelled(true);
            handleEditWarpsGUIClick(player, event);
        } else if (title.contains(ColorUtils.applyGradient("Edit:"))) {
            event.setCancelled(true);
            handleEditWarpGUIClick(player, event);
        }
    }

    private void handleMainGUIClick(Player player, InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        if (item.getType() == Material.COMPASS) {
            new AllWarpsGUI(plugin, player, 0).open();
        } else if (item.getType() == Material.WRITABLE_BOOK) {
            new EditWarpsGUI(plugin, player, 0).open();
        } else if (item.getType() == Material.YELLOW_STAINED_GLASS_PANE) {
            return;
        } else {
            String warpName = extractWarpName(item);
            if (warpName != null) {
                Warp warp = plugin.getWarpManager().getWarp(warpName);
                if (warp != null) {
                    player.teleport(warp.getLocation());
                    player.sendMessage(plugin.getMessageManager().getMessage("teleported-to-warp")
                            .replace("{warp}", warpName));
                    player.closeInventory();
                }
            }
        }
    }

    private void handleAllWarpsGUIClick(Player player, InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        int slot = event.getSlot();

        if (slot == 45) {
            new MainGUI(plugin, player, 0).open();
        } else if (slot == 48) {
            AllWarpsGUI gui = new AllWarpsGUI(plugin, player, Math.max(0, getCurrentPage(event.getView().getTitle()) - 1));
            gui.open();
        } else if (slot == 50) {
            AllWarpsGUI gui = new AllWarpsGUI(plugin, player, getCurrentPage(event.getView().getTitle()) + 1);
            gui.open();
        } else {
            String warpName = extractWarpName(item);
            if (warpName != null) {
                Warp warp = plugin.getWarpManager().getWarp(warpName);
                if (warp != null) {
                    player.teleport(warp.getLocation());
                    player.sendMessage(plugin.getMessageManager().getMessage("teleported-to-warp")
                            .replace("{warp}", warpName));
                    player.closeInventory();
                }
            }
        }
    }

    private void handleEditWarpsGUIClick(Player player, InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        int slot = event.getSlot();

        if (slot == 45) {
            new MainGUI(plugin, player, 0).open();
        } else if (slot == 48) {
            EditWarpsGUI gui = new EditWarpsGUI(plugin, player, Math.max(0, getCurrentPage(event.getView().getTitle()) - 1));
            gui.open();
        } else if (slot == 50) {
            EditWarpsGUI gui = new EditWarpsGUI(plugin, player, getCurrentPage(event.getView().getTitle()) + 1);
            gui.open();
        } else {
            String warpName = extractWarpName(item);
            if (warpName != null) {
                Warp warp = plugin.getWarpManager().getWarp(warpName);
                if (warp != null && warp.getOwner().equals(player.getUniqueId())) {
                    ClickType clickType = event.getClick();

                    if (clickType == ClickType.LEFT) {
                        new EditWarpGUI(plugin, player, warp).open();
                    } else if (clickType == ClickType.RIGHT) {
                        plugin.getWarpManager().removeWarp(warpName);
                        plugin.getSponsorManager().removeSponsor(warpName);
                        player.sendMessage(plugin.getMessageManager().getMessage("warp-deleted")
                                .replace("{warp}", warpName));
                        new EditWarpsGUI(plugin, player, 0).open();
                    } else if (clickType == ClickType.SHIFT_LEFT) {
                        if (plugin.getSponsorManager().isSponsored(warpName)) {
                            player.sendMessage(plugin.getMessageManager().getMessage("warp-already-sponsored"));
                            return;
                        }

                        if (plugin.getSponsorManager().getAvailableSlots() <= 0) {
                            player.sendMessage(plugin.getMessageManager().getMessage("no-sponsor-slots"));
                            return;
                        }

                        double price = plugin.getConfigManager().getSponsorPrice();
                        if (plugin.getEconomy().getBalance(player) < price) {
                            player.sendMessage(plugin.getMessageManager().getMessage("not-enough-money"));
                            return;
                        }

                        plugin.getEconomy().withdrawPlayer(player, price);
                        plugin.getSponsorManager().addSponsor(warpName, plugin.getConfigManager().getSponsorDurationHours());
                        player.sendMessage(plugin.getMessageManager().getMessage("warp-sponsored")
                                .replace("{warp}", warpName));
                        new EditWarpsGUI(plugin, player, 0).open();
                    }
                }
            }
        }
    }

    private void handleEditWarpGUIClick(Player player, InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        int slot = event.getSlot();
        String title = event.getView().getTitle();
        String warpName = title.replace(ColorUtils.applyGradient("Edit: "), "");
        Warp warp = plugin.getWarpManager().getWarp(warpName);

        if (warp == null) {
            return;
        }

        if (slot == 11) {
            player.closeInventory();
            editingIcon.put(player.getUniqueId(), warpName);
            player.sendMessage(plugin.getMessageManager().getMessage("hold-item-to-change-icon"));

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (editingIcon.containsKey(player.getUniqueId())) {
                    ItemStack heldItem = player.getInventory().getItemInMainHand();
                    if (heldItem != null && heldItem.getType() != Material.AIR) {
                        warp.setIcon(heldItem.getType());
                        plugin.getWarpManager().saveWarps();
                        player.sendMessage(plugin.getMessageManager().getMessage("icon-changed"));
                    }
                    editingIcon.remove(player.getUniqueId());
                }
            }, 100L);
        } else if (slot == 13) {
            player.closeInventory();
            editingDescription.put(player.getUniqueId(), warpName);
            player.sendMessage(plugin.getMessageManager().getMessage("type-description-in-chat"));
        } else if (slot == 15) {
            plugin.getWarpManager().removeWarp(warpName);
            plugin.getSponsorManager().removeSponsor(warpName);
            player.sendMessage(plugin.getMessageManager().getMessage("warp-deleted")
                    .replace("{warp}", warpName));
            new EditWarpsGUI(plugin, player, 0).open();
        } else if (slot == 22) {
            new EditWarpsGUI(plugin, player, 0).open();
        }
    }

    private String extractWarpName(ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            String displayName = item.getItemMeta().getDisplayName();
            String prefix = ColorUtils.applyGradient(plugin.getMessageManager().getRawMessage("warp-display-name").replace("{warp}", ""));
            return displayName.replace(prefix, "").replaceAll("§[0-9a-fk-or]", "");
        }
        return null;
    }

    private int getCurrentPage(String title) {
        try {
            String[] parts = title.split("Page ");
            if (parts.length > 1) {
                return Integer.parseInt(parts[1].replaceAll("§[0-9a-fk-or]", "").trim()) - 1;
            }
        } catch (NumberFormatException e) {
            return 0;
        }
        return 0;
    }

    public Map<UUID, String> getEditingDescription() {
        return editingDescription;
    }
}