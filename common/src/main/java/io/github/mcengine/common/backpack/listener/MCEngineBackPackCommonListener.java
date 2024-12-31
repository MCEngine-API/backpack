package io.github.mcengine.common.backpack.listener;

import io.github.mcengine.api.MCEngineBackPackApi;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class MCEngineBackPackCommonListener implements Listener {

    private final MCEngineBackPackApi backPackApi;

    public MCEngineBackPackCommonListener(JavaPlugin plugin) {
        this.backPackApi = new MCEngineBackPackApi(plugin);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (backPackApi.isBackpack(item)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cYou cannot place a backpack!");
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (backPackApi.isBackpack(itemInHand)) {
            backPackApi.saveBackpack(itemInHand, inventory);
            player.sendMessage("§aBackpack saved successfully!");
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) return;

        // Check if the clicked inventory is associated with a backpack
        Player player = (Player) event.getWhoClicked();
        if (backPackApi.isBackpack(player.getInventory().getItemInMainHand())) {
            ItemStack clickedItem = event.getCurrentItem();
            ItemStack cursorItem = event.getCursor();

            // Prevent placing backpacks inside backpacks
            if (clickedItem != null && backPackApi.isBackpack(clickedItem)) {
                event.setCancelled(true);
                player.sendMessage("§cYou cannot interact with a backpack!");
                return;
            }

            if (cursorItem != null && backPackApi.isBackpack(cursorItem)) {
                event.setCancelled(true);
                player.sendMessage("§cYou cannot place a backpack inside another backpack!");
                return;
            }

            if (event.getClick().isShiftClick() && backPackApi.isBackpack(clickedItem)) {
                event.setCancelled(true);
                player.sendMessage("§cYou cannot place a backpack inside another backpack!");
            }
        }
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        if (event.getAction().toString().contains("RIGHT") && event.hasItem() && backPackApi.isBackpack(event.getItem())) {
            event.setCancelled(true); // Cancel the default right-click behavior
            Player player = event.getPlayer();
            Inventory backpack = backPackApi.getBackpack(event.getItem());
            player.openInventory(backpack);
            player.sendMessage("§aOpening your backpack!");
        }
    }
}
