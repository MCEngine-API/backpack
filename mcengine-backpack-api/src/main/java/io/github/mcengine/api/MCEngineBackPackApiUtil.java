package io.github.mcengine.api;

import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.logging.Logger;

public class MCEngineBackPackApiUtil {
    private final JavaPlugin plugin;
    private static final String BACKPACK_KEY = "backpack_key";
    private static final String SIZE_KEY = "size_key";

    public MCEngineBackPackApiUtil(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public ItemStack createBackpack(String texture, int size) {
        HeadDatabaseAPI headDatabaseAPI = new HeadDatabaseAPI();
        ItemStack head;

        try {
            if (headDatabaseAPI != null && texture != null && !texture.isEmpty()) {
                head = headDatabaseAPI.getItemHead(texture);
            } else {
                throw new NullPointerException("Invalid texture or HeadDatabaseAPI instance.");
            }
        } catch (NullPointerException e) {
            Logger logger = plugin.getLogger();
            logger.warning("Invalid HeadDatabase ID or texture: " + texture + ". Using default player head.");
            head = new ItemStack(Material.PLAYER_HEAD);
        }

        ItemMeta meta = head.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6Backpack");
            PersistentDataContainer data = meta.getPersistentDataContainer();

            // Add a unique identifier to prevent stacking
            String uniqueID = UUID.randomUUID().toString();
            data.set(new NamespacedKey(plugin, BACKPACK_KEY), PersistentDataType.STRING, "backpack");
            data.set(new NamespacedKey(plugin, SIZE_KEY), PersistentDataType.INTEGER, size);
            data.set(new NamespacedKey(plugin, "unique_id"), PersistentDataType.STRING, uniqueID);

            head.setItemMeta(meta);
        }

        return head;
    }
}
