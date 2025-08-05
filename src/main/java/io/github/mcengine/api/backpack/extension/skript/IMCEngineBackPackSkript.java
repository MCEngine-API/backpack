package io.github.mcengine.api.backpack.extension.skript;

import org.bukkit.plugin.Plugin;

/**
 * Represents a skript-based BackPack module that can be dynamically loaded into the MCEngine.
 * <p>
 * These modules typically encapsulate custom scripted BackPack behavior and are used to extend game logic.
 */
public interface IMCEngineBackPackSkript {

    /**
     * Called when the BackPack skript module is loaded by the engine.
     *
     * @param plugin The plugin instance providing context.
     */
    void onLoad(Plugin plugin);

    /**
     * Called when the skript is unloaded or disabled by the engine.
     * <p>
     * This method should be used to clean up event handlers, memory, or tasks
     * created during {@link #onLoad(Plugin)}.
     *
     * @param plugin The plugin instance providing context for this module.
     */
    void onDisload(Plugin plugin);

    /**
     * Sets a unique ID for this BackPack skript module.
     *
     * @param id The unique ID assigned by the engine.
     */
    void setId(String id);
}
