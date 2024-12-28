package io.github.mcengine.api;

import io.github.mcengine.api.MCEngineBackPackApiUtil;
import org.bukkit.inventory.ItemStack;

public class MCEngineBackPackApi {
    private static MCEngineBackPackApiUtil mcengineBackPackApiUtil;

    public ItemStack getBackPack(String texture, int size) {
        return mcengineBackPackApiUtil.createBackpack(texture, size);
    }
}