package io.github.mcengine.api.backpack.extension.agent;

import org.bukkit.plugin.Plugin;

/**
 * Represents a BackPack-based Agent module that can be dynamically loaded into the MCEngine.
 * <p>
 * Implement this interface to integrate backpack-related agents into the system.
 */
public interface IMCEngineBackPackAgent {

    /**
     * Called when the BackPack Agent is loaded by the engine.
     *
     * @param plugin The plugin instance providing context.
     */
    void onLoad(Plugin plugin);

    /**
     * Called when the BackPack Agent is unloaded or disabled by the engine.
     *
     * @param plugin The plugin instance providing context.
     */
    void onDisload(Plugin plugin);

    /**
     * Sets a unique ID for this BackPack Agent instance.
     *
     * @param id The unique ID assigned by the engine.
     */
    void setId(String id);
}
