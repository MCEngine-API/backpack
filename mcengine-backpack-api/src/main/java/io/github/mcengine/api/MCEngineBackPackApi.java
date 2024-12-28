package io.github.mcengine.api;

import io.github.mcengine.api.MCEngineBackPackApiUtil;
import org.bukkit.inventory.ItemStack;

public class MCEngineBackPackApi {

    private static MCEngineBackPackApiUtil mcengineBackPackApiUtil;

    // Initializes the utility class (ensure this is called during plugin setup)
    public static void initialize(MCEngineBackPackApiUtil apiUtil) {
        mcengineBackPackApiUtil = apiUtil;
    }

    public static ItemStack getBackPack(String texture, int size) {
        if (mcengineBackPackApiUtil == null) {
            throw new IllegalStateException("MCEngineBackPackApiUtil is not initialized.");
        }
        return mcengineBackPackApiUtil.createBackpack(texture, size);
    }
}
