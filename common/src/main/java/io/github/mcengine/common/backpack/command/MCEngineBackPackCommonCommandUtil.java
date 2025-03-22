package io.github.mcengine.common.backpack.command;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class MCEngineBackPackCommonCommandUtil {

    private static final String BASE_PATH = "plugins/MCEngineBackPack";

    /**
     * Loads a YamlConfiguration file for the given backpack name.
     *
     * @param name The name of the backpack.
     * @return The loaded YamlConfiguration, or null if the file does not exist.
     */
    public static YamlConfiguration loadBackpackConfig(String name) {
        // Validate input
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Backpack name is invalid.");
            return null;
        }

        File file = new File(BASE_PATH + "/heads", name + ".yml");
        if (!file.exists()) {
            System.out.println("Backpack configuration file not found: " + file.getAbsolutePath());
            return null;
        }

        return YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Parses the backpack size from a string and validates it.
     *
     * @param sizeStr the size string from the command
     * @param player the player to send error messages to
     * @return the valid size in slots, or -1 if invalid
     */
    public static int parseBackpackSize(String sizeStr, Player player) {
        int size;
        try {
            size = Integer.parseInt(sizeStr) * 9;
        } catch (NumberFormatException e) {
            player.sendMessage("§cInvalid size. Please provide a valid number.");
            return -1;
        }

        if (!isValidSize(size)) {
            player.sendMessage("§cSize must be between 1 and 6 rows (9-54 slots).");
            return -1;
        }

        return size;
    }

    /**
     * Validates if the given size in slots is acceptable.
     *
     * @param size the size in slots (must be multiple of 9)
     * @return true if valid, false otherwise
     */
    public static boolean isValidSize(int size) {
        return size >= 9 && size <= 54;
    }
}
