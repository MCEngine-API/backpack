package io.github.mcengine.api.backpack.extension.addon;

import org.bukkit.plugin.Plugin;

/**
 * Represents a BackPack-based AddOn module that can be dynamically loaded into the MCEngine.
 * <p>
 * Implement this interface to integrate BackPack-related features into the plugin
 * via the AddOn extension system.
 */
public interface IMCEngineBackPackAddOn {

    /**
     * Called when the BackPack AddOn is loaded by the engine.
     *
     * @param plugin The plugin instance providing context.
     */
    void onLoad(Plugin plugin);

    /**
     * Called when the AddOn is unloaded or disabled by the engine.
     * <p>
     * Use this method to release resources, unregister listeners,
     * or perform any necessary cleanup.
     *
     * @param plugin The plugin instance providing context.
     */
    void onDisload(Plugin plugin);

    /**
     * Sets a unique ID for this BackPack AddOn instance.
     *
     * @param id The unique ID assigned by the engine.
     */
    void setId(String id);
}
