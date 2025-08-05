package io.github.mcengine.api.backpack.extension.api;

import org.bukkit.plugin.Plugin;

/**
 * Represents a core BackPack API module that can be dynamically loaded into the MCEngine.
 * <p>
 * Implement this interface to provide BackPack capabilities to the core system.
 */
public interface IMCEngineBackPackAPI {

    /**
     * Called when the BackPack API module is loaded by the engine.
     *
     * @param plugin The plugin instance providing context.
     */
    void onLoad(Plugin plugin);

    /**
     * Called when the API module is unloaded or disabled by the engine.
     * <p>
     * Use this method to deregister any resources, cancel tasks,
     * or clean up systems related to this module.
     *
     * @param plugin The plugin instance providing the context.
     */
    void onDisload(Plugin plugin);

    /**
     * Sets a unique ID for this BackPack API module.
     *
     * @param id The unique ID assigned by the engine.
     */
    void setId(String id);
}
