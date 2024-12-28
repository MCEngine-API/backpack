package io.github.mcengine.api.backpack.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class MCEngineBackPackApiListener implements Listener {
    private final JavaPlugin plugin;
    private final MCEngineBackPackApiListenerUtil backPackApiListenerUtil;

    private static final String BACKPACK_TITLE = "Backpack";

    public MCEngineBackPackApiListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.backPackApiListenerUtil = new MCEngineBackPackApiListenerUtil(plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(BACKPACK_TITLE)) {
            ItemStack clickedItem = event.getCurrentItem();
            ItemStack cursorItem = event.getCursor();

            // Prevent interaction with gray glass panes
            if (clickedItem != null && clickedItem.getType() == Material.GRAY_STAINED_GLASS_PANE) {
                event.setCancelled(true);
            }

            // Prevent clicking backpacks
            if (backPackApiListenerUtil.isBackpack(plugin, clickedItem)) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage("§cYou cannot interact with a backpack!");
            }

            // Prevent placing backpacks inside backpacks
            if (cursorItem != null && backPackApiListenerUtil.isBackpack(plugin, cursorItem)) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage("§cYou cannot place a backpack inside another backpack!");
            }

            // Prevent shift-clicking backpacks into backpack inventory
            if (event.getClick().isShiftClick() && backPackApiListenerUtil.isBackpack(plugin, clickedItem)) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage("§cYou cannot place a backpack inside another backpack!");
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals(BACKPACK_TITLE)) {
            Player player = (Player) event.getPlayer();
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (backPackApiListenerUtil.isBackpack(plugin, itemInHand)) {
                backPackApiListenerUtil.saveBackpack(itemInHand, event.getInventory());
            }
        }
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        // Check if the player right-clicked with a backpack
        if (event.getAction().toString().contains("RIGHT") && event.hasItem() && backPackApiListenerUtil.isBackpack(plugin, event.getItem())) {
            // Open the backpack inventory
            Inventory backpack = backPackApiListenerUtil.getBackpack(plugin, event.getItem());
            if (backpack != null) {
                event.getPlayer().openInventory(backpack);
            }
        }
    }
}
