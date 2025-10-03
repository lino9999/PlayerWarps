package com.Lino.playerWarps.listeners;

import com.Lino.playerWarps.PlayerWarps;
import com.Lino.playerWarps.models.Warp;
import com.Lino.playerWarps.gui.MainGUI;
import com.Lino.playerWarps.gui.AllWarpsGUI;
import com.Lino.playerWarps.gui.EditWarpsGUI;
import com.Lino.playerWarps.gui.EditWarpGUI;
import com.Lino.playerWarps.gui.InfoGUI;
import com.Lino.playerWarps.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
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
        String viewTitle = event.getView().getTitle();

        if (isMainGUI(viewTitle)) {
            event.setCancelled(true);
            handleMainGUIClick(player, event);
        } else if (isAllWarpsGUI(viewTitle)) {
            event.setCancelled(true);
            handleAllWarpsGUIClick(player, event);
        } else if (isEditWarpsGUI(viewTitle)) {
            event.setCancelled(true);
            handleEditWarpsGUIClick(player, event);
        } else if (isEditWarpGUI(viewTitle)) {
            event.setCancelled(true);
            handleEditWarpGUIClick(player, event);
        } else if (isInfoGUI(viewTitle)) {
            event.setCancelled(true);
            handleInfoGUIClick(player, event);
        }
    }

    private boolean isMainGUI(String title) {
        String clean = ChatColor.stripColor(title);
        String expected = ChatColor.stripColor(ColorUtils.applyGradient(plugin.getConfigManager().getGuiTitle()));
        return clean.equals(expected);
    }

    private boolean isAllWarpsGUI(String title) {
        String clean = ChatColor.stripColor(title);
        String expected = ChatColor.stripColor(ColorUtils.applyGradient(plugin.getConfigManager().getAllWarpsGuiTitle()));
        return clean.contains(expected);
    }

    private boolean isEditWarpsGUI(String title) {
        String clean = ChatColor.stripColor(title);
        String expected = ChatColor.stripColor(ColorUtils.applyGradient(plugin.getConfigManager().getEditWarpsGuiTitle()));
        return clean.contains(expected);
    }

    private boolean isEditWarpGUI(String title) {
        String clean = ChatColor.stripColor(title);
        return clean.startsWith("Edit:");
    }

    private boolean isInfoGUI(String title) {
        String clean = ChatColor.stripColor(title);
        String expected = ChatColor.stripColor(ColorUtils.applyGradient(plugin.getConfigManager().getInfoGuiTitle()));
        return clean.equals(expected);
    }

    private void handleMainGUIClick(Player player, InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        int slot = event.getSlot();
        Material type = item.getType();

        if (type == Material.COMPASS) {
            new AllWarpsGUI(plugin, player, 0).open();
        } else if (type == Material.WRITABLE_BOOK) {
            new EditWarpsGUI(plugin, player, 0).open();
        } else if (type == Material.BOOK) {
            new InfoGUI(plugin, player).open();
        } else if (type == Material.YELLOW_STAINED_GLASS_PANE) {
            return;
        } else if (slot < plugin.getConfigManager().getSponsorSlots()) {
            String warpName = getWarpNameFromLore(item);
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
        Material type = item.getType();

        if (slot == 45 && type == Material.ARROW) {
            new MainGUI(plugin, player, 0).open();
        } else if (slot == 48 && type == Material.ARROW) {
            int currentPage = getCurrentPage(event.getView().getTitle());
            new AllWarpsGUI(plugin, player, Math.max(0, currentPage - 1)).open();
        } else if (slot == 50 && type == Material.ARROW) {
            int currentPage = getCurrentPage(event.getView().getTitle());
            new AllWarpsGUI(plugin, player, currentPage + 1).open();
        } else if (slot < 45) {
            String warpName = getWarpNameFromLore(item);
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
        Material type = item.getType();
        ClickType clickType = event.getClick();

        if (slot == 45 && type == Material.ARROW) {
            new MainGUI(plugin, player, 0).open();
            return;
        } else if (slot == 48 && type == Material.ARROW) {
            int currentPage = getCurrentPage(event.getView().getTitle());
            new EditWarpsGUI(plugin, player, Math.max(0, currentPage - 1)).open();
            return;
        } else if (slot == 50 && type == Material.ARROW) {
            int currentPage = getCurrentPage(event.getView().getTitle());
            new EditWarpsGUI(plugin, player, currentPage + 1).open();
            return;
        }

        if (slot < 45) {
            String warpName = getWarpNameFromLore(item);
            if (warpName != null) {
                Warp warp = plugin.getWarpManager().getWarp(warpName);
                if (warp != null && warp.getOwner().equals(player.getUniqueId())) {
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

                        if (plugin.getEconomy() == null) {
                            player.sendMessage(plugin.getMessageManager().getMessage("economy-not-available"));
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
        Material type = item.getType();
        String viewTitle = event.getView().getTitle();
        String cleanTitle = ChatColor.stripColor(viewTitle);
        String warpName = cleanTitle.replace("Edit:", "").trim();

        Warp warp = plugin.getWarpManager().getWarp(warpName);

        if (warp == null) {
            player.closeInventory();
            player.sendMessage(plugin.getMessageManager().getMessage("warp-not-found"));
            return;
        }

        if (slot == 11 && type == Material.PAINTING) {
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
                    } else {
                        player.sendMessage(plugin.getMessageManager().getMessage("no-item-held"));
                    }
                    editingIcon.remove(player.getUniqueId());
                }
            }, 100L);
        } else if (slot == 13 && type == Material.WRITABLE_BOOK) {
            player.closeInventory();
            editingDescription.put(player.getUniqueId(), warpName);
            player.sendMessage(plugin.getMessageManager().getMessage("type-description-in-chat"));
        } else if (slot == 15 && type == Material.BARRIER) {
            plugin.getWarpManager().removeWarp(warpName);
            plugin.getSponsorManager().removeSponsor(warpName);
            player.sendMessage(plugin.getMessageManager().getMessage("warp-deleted")
                    .replace("{warp}", warpName));
            new EditWarpsGUI(plugin, player, 0).open();
        } else if (slot == 22 && type == Material.ARROW) {
            new EditWarpsGUI(plugin, player, 0).open();
        }
    }

    private void handleInfoGUIClick(Player player, InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        int slot = event.getSlot();

        if (slot == 22 && item.getType() == Material.ARROW) {
            new MainGUI(plugin, player, 0).open();
        }
    }

    private String getWarpNameFromLore(ItemStack item) {
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return null;
        }

        String displayName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
        return displayName.trim();
    }

    private int getCurrentPage(String title) {
        try {
            String clean = ChatColor.stripColor(title);
            String[] parts = clean.split("Page ");
            if (parts.length > 1) {
                String pageStr = parts[1].trim().split(" ")[0];
                return Integer.parseInt(pageStr) - 1;
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }

    public Map<UUID, String> getEditingDescription() {
        return editingDescription;
    }
}