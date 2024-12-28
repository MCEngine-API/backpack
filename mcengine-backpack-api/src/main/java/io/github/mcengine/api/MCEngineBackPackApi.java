package io.github.mcengine.api;

import org.bukkit.inventory.ItemStack;

class MCEngineBackPack {
    private static MCEngineBackPackApiUtil mcengineBackPackApiUtil;

    public ItemStack getBackPack(String texture, int size) {
        return mcengineBackPackApiUtil.createBackpack();
    }
}