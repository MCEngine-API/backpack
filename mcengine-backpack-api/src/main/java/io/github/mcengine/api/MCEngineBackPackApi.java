package io.github.mcengine.api;

import java.io.StringReader;
import java.util.UUID;
import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class MCEngineBackPackApi {

    private final JavaPlugin plugin;
    private static final String BACKPACK_KEY = "backpack";
    private static final String SIZE_KEY = "backpack_size";
    private static final String UNIQUE_ID_KEY = "unique_id";

    public MCEngineBackPackApi(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public ItemStack createBackpack(String backpackName, String textureID, int size) {
        ItemStack head;
        try {
            // Fetch the head from HeadDatabase using the textureID
            HeadDatabaseAPI headDatabaseAPI = new HeadDatabaseAPI();
            head = headDatabaseAPI.getItemHead(textureID);
        } catch (Exception e) {
            plugin.getLogger().warning("Invalid HeadDatabase texture ID: " + textureID + ". Using default player head.");
            head = new ItemStack(Material.PLAYER_HEAD);
        }

        ItemMeta meta = head.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(backpackName);
            PersistentDataContainer data = meta.getPersistentDataContainer();

            // Add unique identifiers and backpack size
            data.set(new NamespacedKey(plugin, BACKPACK_KEY), PersistentDataType.STRING, "backpack");
            data.set(new NamespacedKey(plugin, SIZE_KEY), PersistentDataType.INTEGER, size);
            data.set(new NamespacedKey(plugin, UNIQUE_ID_KEY), PersistentDataType.STRING, UUID.randomUUID().toString());

            head.setItemMeta(meta);
        }

        return head;
    }

    public Inventory getBackpack(ItemStack headItem) {
        ItemMeta meta = headItem.getItemMeta();
        if (meta == null) {
            // Default to a new backpack with a title and a default size
            return Bukkit.createInventory(null, 27, "Default Backpack");
        }
    
        PersistentDataContainer data = meta.getPersistentDataContainer();
        // Fetch the serialized inventory data using BACKPACK_KEY
        String serializedData = data.get(new NamespacedKey(plugin, BACKPACK_KEY), PersistentDataType.STRING);
        int size = data.getOrDefault(new NamespacedKey(plugin, SIZE_KEY), PersistentDataType.INTEGER, 9);
    
        // Create the inventory with the size and the backpack name
        Inventory backpack = Bukkit.createInventory(null, size, meta.getDisplayName());
        if (serializedData != null && !serializedData.isEmpty()) {
            // Deserialize the inventory contents
            ItemStack[] items = deserializeInventory(serializedData, size);
            backpack.setContents(items);
        }
        return backpack;
    }    

    public boolean isBackpack(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        return data.has(new NamespacedKey(plugin, BACKPACK_KEY), PersistentDataType.STRING);
    }

    public void saveBackpack(ItemStack headItem, Inventory inventory) {
        ItemMeta meta = headItem.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer data = meta.getPersistentDataContainer();
        String serializedData = serializeInventory(inventory.getContents());
        data.set(new NamespacedKey(plugin, BACKPACK_KEY), PersistentDataType.STRING, serializedData);

        headItem.setItemMeta(meta);
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
