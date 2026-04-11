package com.github.gtexpert.gtmt.mixins.storagedrawers;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.EnumBasicDrawer;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.UpgradeData;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.EnumUpgradeStorage;

import com.github.gtexpert.gtmt.integration.storagedrawers.api.IStorageMultiplier;
import com.github.gtexpert.gtmt.integration.storagedrawers.storageupgrades.IGTMaterialStorageUpgrade;

@Mixin(value = UpgradeData.class, remap = false)
public abstract class UpgradeDataMixin implements IStorageMultiplier {

    @Shadow
    private ItemStack[] upgrades;
    @Shadow
    private int storageMultiplier;
    @Unique
    private long gtmt$rawStorageMultiplier = 1;
    @Unique
    private static final int MAX_MULTIPLIER = Integer.MAX_VALUE /
            (StorageDrawers.config.getBlockBaseStorage(EnumBasicDrawer.FULL1.getUnlocalizedName()) * 64);

    @Override
    public long gtmt$getRawStorageMultiplier() {
        return gtmt$rawStorageMultiplier;
    }

    @Unique
    private int gtmt$getMultiplierOf(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return 0;
        }

        if (stack.getItem() == ModItems.upgradeStorage) {
            int level = EnumUpgradeStorage.byMetadata(stack.getMetadata()).getLevel();
            return StorageDrawers.config.getStorageUpgradeMultiplier(level);
        }

        if (stack.getItem() instanceof IGTMaterialStorageUpgrade upgrade) {
            return upgrade.getStorageMultiplier(stack);
        }

        return 0;
    }

    @Unique
    private int gtmt$clampEffectiveMultiplier(long raw) {
        if (raw <= 1L) {
            return 1;
        }
        if (raw >= MAX_MULTIPLIER) {
            return MAX_MULTIPLIER;
        }
        return (int) raw;
    }

    @Inject(method = "syncStorageMultiplier",
            at = @At("HEAD"),
            cancellable = true)
    private void gtmt$syncStorageMultiplier(CallbackInfo ci) {
        ConfigManager config = StorageDrawers.config;
        long raw = 0L;

        for (ItemStack stack : upgrades) {
            raw += gtmt$getMultiplierOf(stack);
        }

        if (raw <= 0) {
            raw = config.getStorageUpgradeMultiplier(1);
        }

        this.gtmt$rawStorageMultiplier = raw;
        this.storageMultiplier = (int) MathHelper.clamp(raw, 1, MAX_MULTIPLIER);
        ci.cancel();
    }
}
