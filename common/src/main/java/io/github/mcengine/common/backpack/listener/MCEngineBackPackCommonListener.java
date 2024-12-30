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
        if (event.getView().getTitle().equals(MCEngineBackPackApi.BACKPACK_TITLE)) {
            Player player = (Player) event.getPlayer();
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (backPackApi.isBackpack(itemInHand)) {
                backPackApi.saveBackpack(itemInHand, event.getInventory());
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(MCEngineBackPackApi.BACKPACK_TITLE)) {
            ItemStack clickedItem = event.getCurrentItem();
            ItemStack cursorItem = event.getCursor();

            if (clickedItem != null && clickedItem.getType() == Material.GRAY_STAINED_GLASS_PANE) {
                event.setCancelled(true);
            }

            if (backPackApi.isBackpack(clickedItem)) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage("§cYou cannot interact with a backpack!");
            }

            if (cursorItem != null && backPackApi.isBackpack(cursorItem)) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage("§cYou cannot place a backpack inside another backpack!");
            }

            if (event.getClick().isShiftClick() && backPackApi.isBackpack(clickedItem)) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage("§cYou cannot place a backpack inside another backpack!");
            }
        }
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        if (event.getAction().toString().contains("RIGHT") && event.hasItem() && backPackApi.isBackpack(event.getItem())) {
            Inventory backpack = backPackApi.getBackpack(event.getItem());
            event.getPlayer().openInventory(backpack);
        }
    }
}
