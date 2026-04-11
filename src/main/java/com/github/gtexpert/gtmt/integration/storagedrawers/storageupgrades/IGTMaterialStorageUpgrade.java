package com.github.gtexpert.gtmt.integration.storagedrawers.storageupgrades;

import net.minecraft.item.ItemStack;

public interface IGTMaterialStorageUpgrade {

    int getStorageMultiplier(ItemStack stack);
}
