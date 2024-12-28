package io.github.mcengine;

import org.bukkit.plugin.java.JavaPlugin;

public class MCEngineBackPack extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("MCEngineBackPack has been enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("MCEngineBackPack has been disabled.");
    }
}
