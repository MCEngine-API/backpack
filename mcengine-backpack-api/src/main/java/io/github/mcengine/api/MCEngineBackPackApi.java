package io.github.mcengine.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
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
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class MCEngineBackPackApi {

    private final JavaPlugin plugin;

    // NamespacedKey constants
    private final NamespacedKey BACKPACK_KEY;
    private final NamespacedKey BACKPACK_DATA_KEY;
    private final NamespacedKey SIZE_KEY;
    private final NamespacedKey UNIQUE_ID_KEY;

    public MCEngineBackPackApi(JavaPlugin plugin) {
        this.plugin = plugin;
        this.BACKPACK_KEY = new NamespacedKey("mcengine", "backpack");
        this.BACKPACK_DATA_KEY = new NamespacedKey("mcengine", "backpack_data");
        this.SIZE_KEY = new NamespacedKey("mcengine", "backpack_size");
        this.UNIQUE_ID_KEY = new NamespacedKey("mcengine", "unique_id");
    }

    public ItemStack getBackpack(String backpackName, String textureID, int size) {
        ItemStack head;
        try {
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

            data.set(BACKPACK_KEY, PersistentDataType.STRING, "backpack");
            data.set(SIZE_KEY, PersistentDataType.INTEGER, size);
            data.set(UNIQUE_ID_KEY, PersistentDataType.STRING, UUID.randomUUID().toString());

            head.setItemMeta(meta);
        }

        return head;
    }

    public Inventory openBackpack(ItemStack headItem) {
        ItemMeta meta = headItem.getItemMeta();
        if (meta == null) return Bukkit.createInventory(null, 27, "Default Backpack");

        PersistentDataContainer data = meta.getPersistentDataContainer();
        String serializedData = data.get(BACKPACK_DATA_KEY, PersistentDataType.STRING);
        int size = data.getOrDefault(SIZE_KEY, PersistentDataType.INTEGER, 9);

        Inventory backpack = Bukkit.createInventory(null, size, meta.getDisplayName());
        if (serializedData != null && !serializedData.isEmpty()) {
            ItemStack[] items = deserializeInventory(serializedData, size);
            backpack.setContents(items);
        }
        return backpack;
    }

    public boolean isBackpack(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
        return data.has(BACKPACK_KEY, PersistentDataType.STRING);
    }

    public void saveBackpack(ItemStack headItem, Inventory inventory) {
        ItemMeta meta = headItem.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer data = meta.getPersistentDataContainer();
        String serializedData = serializeInventory(inventory.getContents());
        if (serializedData != null && !serializedData.isEmpty()) {
            data.set(BACKPACK_DATA_KEY, PersistentDataType.STRING, serializedData);
        }

        headItem.setItemMeta(meta);
    }

    public String serializeInventory(ItemStack[] items) {
        StringBuilder serialized = new StringBuilder();
        try {
            for (ItemStack item : items) {
                if (item == null) {
                    serialized.append("null;");
                } else {
                    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                         BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {

                        dataOutput.writeObject(item);
                        serialized.append(Base64.getEncoder().encodeToString(outputStream.toByteArray())).append(";");
                    }
                }
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Serialization error: " + e.getMessage());
            return null;
        }
        return serialized.toString();
    }

    public ItemStack[] deserializeInventory(String data, int size) {
        ItemStack[] items = new ItemStack[size];
        try {
            if (data == null || data.isEmpty()) {
                plugin.getLogger().info("Serialized data is empty. Returning default empty inventory.");
                return items;
            }

            String[] serializedItems = data.split(";", -1);
            for (int i = 0; i < serializedItems.length && i < size; i++) {
                if (!"null".equals(serializedItems[i]) && !serializedItems[i].isEmpty()) {
                    try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(serializedItems[i]));
                         BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {

                        items[i] = (ItemStack) dataInput.readObject();
                    } catch (Exception e) {
                        plugin.getLogger().warning("Error deserializing item at index " + i + ": " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Deserialization error: " + e.getMessage());
        }
        return items;
    }
}
