package io.github.mcengine.api.backpack.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.StringReader;

public class MCEngineBackPackApiListenerUtil {

    private static final String BACKPACK_TITLE = "Backpack";
    private static final String BACKPACK_KEY = "backpack_data";
    private static final String SIZE_KEY = "backpack_size";

    private final JavaPlugin plugin;

    public MCEngineBackPackApiListenerUtil(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public static ItemStack[] deserializeInventory(String data, int size) {
        if (data == null || size <= 0) {
            throw new IllegalArgumentException("Data cannot be null and size must be positive.");
        }

        ItemStack[] items = new ItemStack[size];
        try {
            String[] serializedItems = data.split(";");
            for (int i = 0; i < serializedItems.length && i < size; i++) {
                if (!"null".equals(serializedItems[i])) {
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(new StringReader(serializedItems[i]));
                    items[i] = config.getItemStack("item");
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("Error deserializing inventory: " + e.getMessage());
            e.printStackTrace();
        }
        return items;
    }

    public Inventory getBackpack(JavaPlugin plugin, ItemStack headItem) {
        if (headItem == null || !headItem.hasItemMeta()) {
            return Bukkit.createInventory(null, 27, BACKPACK_TITLE);
        }

        ItemMeta meta = headItem.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        String serializedData = data.get(new NamespacedKey(plugin, BACKPACK_KEY), PersistentDataType.STRING);
        Integer size = data.get(new NamespacedKey(plugin, SIZE_KEY), PersistentDataType.INTEGER);

        if (size == null) {
            size = 27; // Default size
        }

        Inventory backpack = Bukkit.createInventory(null, size, BACKPACK_TITLE);
        if (serializedData != null) {
            ItemStack[] items = deserializeInventory(serializedData, size);
            backpack.setContents(items);
        }
        return backpack;
    }

    public boolean isBackpack(JavaPlugin plugin, ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

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

    public static String serializeInventory(ItemStack[] items) {
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
    }
}
