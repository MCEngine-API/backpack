package io.github.mcengine;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class MCEngineBackPackUtil {
    private final JavaPlugin plugin;

    public MCEngineBackPackUtil(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void saveResourceIfNotExists(String resourcePath) {
        File resourceFile = new File(plugin.getDataFolder(), resourcePath);
        if (!resourceFile.exists()) {
            plugin.saveResource(resourcePath, false); // The 'false' prevents overwriting
            plugin.getLogger().info("Default resource '" + resourcePath + "' has been saved.");
        } else {
            plugin.getLogger().info("Resource '" + resourcePath + "' already exists. Skipping save.");
        }
    }
}
