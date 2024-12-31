package io.github.mcengine.common.backpack.command;

import org.bukkit.configuration.file.YamlConfiguration;

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

        File file = new File(BASE_PATH + "/head", name + ".yml");
        if (!file.exists()) {
            System.out.println("Backpack configuration file not found: " + file.getAbsolutePath());
            return null;
        }

        return YamlConfiguration.loadConfiguration(file);
    }
}
