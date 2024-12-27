package io.github.mcengine;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Base64;
import java.util.UUID;
import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class MCEngineBackPack extends JavaPlugin implements Listener {

    private static final String BACKPACK_KEY = "backpack";
    private static final String SIZE_KEY = "backpack_size";
    private static final String BACKPACK_TITLE = "Backpack";

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("MCEngineBackPack has been enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("MCEngineBackPack has been disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can execute this command.");
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("givebackpack")) {
            if (args.length < 2) {
                player.sendMessage("§eUsage: /givebackpack <head id> <size>");
                return true;
            }

            String texture = args[0];
            int size;
            try {
                size = Integer.parseInt(args[1]) * 9;
            } catch (NumberFormatException e) {
                player.sendMessage("§cInvalid size. Please provide a valid number.");
                return true;
            }

            if (size < 1 || size > 6) {
                player.sendMessage("§cSize must be 1-6(Row).");
                return true;
            }

            ItemStack backpack = createBackpack(texture, size);
            player.getInventory().addItem(backpack);
            player.sendMessage("§aBackpack given!");
            return true;
        }

        return false;
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        // check if player right-clicked with a backpack
        if (event.getAction().toString().contains("RIGHT") && event.hasItem() && isBackpack(event.getItem())) {
            // open backpack inventory
            Inventory backpack = getBackpack(event.getItem());
            event.getPlayer().openInventory(backpack);
        }
    }

    private boolean isBackpack(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        return data.has(new NamespacedKey(this, BACKPACK_KEY), PersistentDataType.STRING);
    }

    private Inventory getBackpack(ItemStack headItem) {
        ItemMeta meta = headItem.getItemMeta();
        if (meta == null) return Bukkit.createInventory(null, 27, BACKPACK_TITLE);

        PersistentDataContainer data = meta.getPersistentDataContainer();
        String serializedData = data.get(new NamespacedKey(this, BACKPACK_KEY), PersistentDataType.STRING);
        int size = data.getOrDefault(new NamespacedKey(this, SIZE_KEY), PersistentDataType.INTEGER, 9);

        Inventory backpack = Bukkit.createInventory(null, size, BACKPACK_TITLE);
        if (serializedData != null) {
            ItemStack[] items = deserializeInventory(serializedData, size);
            backpack.setContents(items);
        }
        addGrayGlass(backpack, size);
        return backpack;
    }

    private void saveBackpack(ItemStack headItem, Inventory inventory) {
        ItemMeta meta = headItem.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer data = meta.getPersistentDataContainer();
        String serializedData = serializeInventory(inventory.getContents());
        data.set(new NamespacedKey(this, BACKPACK_KEY), PersistentDataType.STRING, serializedData);

        headItem.setItemMeta(meta);
    }

    private ItemStack createBackpack(String textureID, int size) {
        HeadDatabaseAPI headDatabaseAPI = new HeadDatabaseAPI();
        ItemStack head;
        try {
            // Fetch the head from HeadDatabase using the textureID
            head = headDatabaseAPI.getItemHead(textureID);
        } catch (NullPointerException e) {
            // If the textureID is invalid, fall back to a default player head
            getLogger().warning("Invalid HeadDatabase texture ID: " + textureID + ". Using default player head.");
            head = new ItemStack(Material.PLAYER_HEAD);
        }
    
        ItemMeta meta = head.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6Backpack");
            PersistentDataContainer data = meta.getPersistentDataContainer();
    
            // Add a unique identifier to prevent stacking
            String uniqueID = UUID.randomUUID().toString();
            data.set(new NamespacedKey(this, BACKPACK_KEY), PersistentDataType.STRING, "");
            data.set(new NamespacedKey(this, SIZE_KEY), PersistentDataType.INTEGER, size);
            data.set(new NamespacedKey(this, "unique_id"), PersistentDataType.STRING, uniqueID);

            head.setItemMeta(meta);
        }
    
        return head;
    }

    private void addGrayGlass(Inventory inventory, int size) {
        ItemStack grayGlass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = grayGlass.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§8");
            grayGlass.setItemMeta(meta);
        }
        for (int i = size; i < inventory.getSize(); i++) {
            inventory.setItem(i, grayGlass);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        // check if item is a backpack
        if (isBackpack(item)) {
            // cancel event
            event.setCancelled(true);
            // send message
            event.getPlayer().sendMessage("§cYou cannot place a backpack!");
        }
    }


    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals(BACKPACK_TITLE)) {
            Player player = (Player) event.getPlayer();
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (isBackpack(itemInHand)) {
                saveBackpack(itemInHand, event.getInventory());
            }
        }
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
            if (isBackpack(clickedItem)) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage("§cYou cannot interact with a backpack!");
            }

            // Prevent placing backpacks inside backpacks
            if (cursorItem != null && isBackpack(cursorItem)) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage("§cYou cannot place a backpack inside another backpack!");
            }

            // Prevent shift-clicking backpacks into backpack inventory
            if (event.getClick().isShiftClick() && isBackpack(clickedItem)) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage("§cYou cannot place a backpack inside another backpack!");
            }
        }
    }

    private String serializeInventory(ItemStack[] items) {
        try {
            StringBuilder serialized = new StringBuilder();
            for (ItemStack item : items) {
                if (item == null) {
                    serialized.append("null;");
                } else {
                    YamlConfiguration config = new YamlConfiguration();
                    config.set("item", item);
                    serialized.append(config.saveToString()).append(";");
                }
            }
            return serialized.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    
    private ItemStack[] deserializeInventory(String data, int size) {
        try {
            String[] serializedItems = data.split(";");
            ItemStack[] items = new ItemStack[size];
            for (int i = 0; i < serializedItems.length && i < size; i++) {
                if (!serializedItems[i].equals("null")) {
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(new StringReader(serializedItems[i]));
                    items[i] = config.getItemStack("item");
                }
            }
            return items;
        } catch (Exception e) {
            e.printStackTrace();
            return new ItemStack[size];
        }
    }
}
