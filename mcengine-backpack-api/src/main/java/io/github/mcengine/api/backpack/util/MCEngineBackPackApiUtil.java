package io.github.mcengine.api.backpack.util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.StringReader;

public class MCEngineBackPackApiUtil {
    public static String serializeInventory(ItemStack[] items) {
        if (items == null) return "";

        try {
            StringBuilder serialized = new StringBuilder();
            for (ItemStack item : items) {
                if (item == null) {
                    serialized.append("null;");
                } else {
                    YamlConfiguration config = new YamlConfiguration();
                    config.set("item", item);
                    serialized.append(config.saveToString().replace("\n", "\\n")).append(";");
                }
            }
            return serialized.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static ItemStack[] deserializeInventory(String data, int size) {
        if (data == null || data.isEmpty()) return new ItemStack[size];

        try {
            String[] serializedItems = data.split(";");
            ItemStack[] items = new ItemStack[size];

            for (int i = 0; i < serializedItems.length && i < size; i++) {
                if (!serializedItems[i].equals("null")) {
                    YamlConfiguration config = new YamlConfiguration();
                    config.load(new StringReader(serializedItems[i].replace("\\n", "\n")));
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
