package io.github.mcengine.api.backpack.extension.library;

import org.bukkit.plugin.Plugin;

/**
 * Represents a library for BackPack modules that can be dynamically loaded into the MCEngine.
 * <p>
 * Typically used to support BackPack backend logic or infrastructure without player interaction.
 */
public interface IMCEngineBackPackLibrary {

    /**
     * Called when the BackPack library is loaded by the engine.
     *
     * @param plugin The plugin instance providing context.
     */
    void onLoad(Plugin plugin);

    /**
     * Called when the library is unloaded or disabled by the engine.
     * <p>
     * Implementations should use this method to release any services or dependencies registered during {@link #onLoad(Plugin)}.
     *
     * @param plugin The plugin instance providing context for this module.
     */
    void onDisload(Plugin plugin);

    /**
     * Sets a unique ID for this BackPack library module.
     *
     * @param id The unique ID assigned by the engine.
     */
    void setId(String id);
}
