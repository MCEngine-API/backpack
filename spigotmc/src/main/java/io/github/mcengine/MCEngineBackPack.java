package io.github.mcengine;

import io.github.mcengine.api.backpack.command.MCEngineBackPackApiCommand;
import io.github.mcengine.api.backpack.listener.MCEngineBackPackApiListener;
import org.bukkit.plugin.java.JavaPlugin;

public class MCEngineBackPack extends JavaPlugin {

    @Override
    public void onEnable() {
        // Register the "backpack" command and its executor
        if (getCommand("backpack") != null) {
            getCommand("backpack").setExecutor(new MCEngineBackPackApiCommand());
        } else {
            getLogger().warning("Command 'backpack' could not be found in plugin.yml!");
        }

        // Register event listeners
        getServer().getPluginManager().registerEvents(new MCEngineBackPackApiListener(this), this);

        getLogger().info("MCEngineBackPack has been enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("MCEngineBackPack has been disabled.");
    }
}
