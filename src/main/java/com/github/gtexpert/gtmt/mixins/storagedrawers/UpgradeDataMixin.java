package com.github.gtexpert.gtmt.mixins.storagedrawers;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.UpgradeData;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.EnumUpgradeStorage;

import com.github.gtexpert.gtmt.integration.storagedrawers.storageupgrades.IGTMaterialStorageUpgrade;

@Mixin(value = UpgradeData.class, remap = false)
public abstract class UpgradeDataMixin {

    @Shadow
    private ItemStack[] upgrades;
    @Shadow
    private int storageMultiplier;

    @Inject(method = "syncStorageMultiplier",
            at = @At("HEAD"),
            cancellable = true)
    private void gtmt$addCustomStorageMultiplier(CallbackInfo ci) {
        ConfigManager config = StorageDrawers.config;
        int multiplier = 0;

        for (ItemStack stack : upgrades) {
            if (stack == null || stack.isEmpty()) {
                continue;
            }

            if (stack.getItem() == ModItems.upgradeStorage) {
                int level = EnumUpgradeStorage.byMetadata(stack.getMetadata()).getLevel();
                multiplier += config.getStorageUpgradeMultiplier(level);
                continue;
            }

            if (stack.getItem() instanceof IGTMaterialStorageUpgrade gt) {
                int level = gt.getStorageMultiplier(stack);
                if (level > 0) {
                    multiplier += level;
                }
            }
        }

        if (multiplier == 0) {
            multiplier = config.getStorageUpgradeMultiplier(1);
        }

        this.storageMultiplier = multiplier;
        ci.cancel();
    }
}
