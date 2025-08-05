package io.github.mcengine.api.backpack.extension.dlc;

import org.bukkit.plugin.Plugin;

/**
 * Represents a BackPack-based DLC module that can be dynamically loaded into the MCEngine.
 * <p>
 * Implement this interface to integrate BackPack-related DLC into the plugin system.
 */
public interface IMCEngineBackPackDLC {

    /**
     * Called when the BackPack DLC is loaded by the engine.
     *
     * @param plugin The plugin instance providing context.
     */
    void onLoad(Plugin plugin);

    /**
     * Called when the DLC is unloaded or disabled by the engine.
     * <p>
     * Use this method to clean up resources or state that should not persist after disabling.
     *
     * @param plugin The plugin instance providing context.
     */
    void onDisload(Plugin plugin);

    /**
     * Sets a unique ID for this BackPack DLC module.
     *
     * @param id The unique ID assigned by the engine.
     */
    void setId(String id);
}
