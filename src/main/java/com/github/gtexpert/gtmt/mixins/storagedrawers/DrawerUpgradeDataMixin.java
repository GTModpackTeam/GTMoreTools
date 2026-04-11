package com.github.gtexpert.gtmt.mixins.storagedrawers;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.EnumBasicDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.UpgradeData;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;

import com.github.gtexpert.gtmt.integration.storagedrawers.api.IStorageMultiplier;
import com.github.gtexpert.gtmt.integration.storagedrawers.storageupgrades.IGTMaterialStorageUpgrade;

@Mixin(targets = "com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers$DrawerUpgradeData", remap = false)
public abstract class DrawerUpgradeDataMixin extends UpgradeData {

    @Unique
    private static final int MAX_MULTIPLIER = Integer.MAX_VALUE /
            (StorageDrawers.config.getBlockBaseStorage(EnumBasicDrawer.FULL1.getUnlocalizedName()) * 64);

    protected DrawerUpgradeDataMixin() {
        super(0);
    }

    @Shadow(aliases = "this$0")
    private TileEntityDrawers gtmt$outer;

    @Shadow
    public abstract boolean canAddUpgrade(@Nonnull ItemStack upgrade);

    @Unique
    private boolean gtmt$stackCapacityCheck(int stackCapacity) {
        for (int i = 0; i < gtmt$outer.getDrawerCount(); i++) {
            IDrawer drawer = gtmt$outer.getDrawer(i);
            if (!drawer.isEnabled() || drawer.isEmpty()) {
                continue;
            }

            int addedItemCapacity = stackCapacity * drawer.getStoredItemStackSize();
            if (drawer.getMaxCapacity() - addedItemCapacity < drawer.getStoredItemCount()) {
                return false;
            }
        }

        return true;
    }

    @Unique
    private int gtmt$getCustomMultiplier(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return 0;
        }

        if (stack.getItem() instanceof IGTMaterialStorageUpgrade) {
            return ((IGTMaterialStorageUpgrade) stack.getItem()).getStorageMultiplier(stack);
        }

        return 0;
    }

    @Unique
    private long gtmt$getRawTotalMultiplier() {
        return ((IStorageMultiplier) (Object) this).gtmt$getRawStorageMultiplier();
    }

    @Unique
    private boolean gtmt$stackCapacityCheckByEffectiveMultiplier(int effectiveMultiplier) {
        int stackCapacity = effectiveMultiplier * gtmt$outer.getEffectiveDrawerCapacity();

        for (int i = 0; i < gtmt$outer.getDrawerCount(); i++) {
            IDrawer drawer = gtmt$outer.getDrawer(i);
            if (!drawer.isEnabled() || drawer.isEmpty()) {
                continue;
            }

            int addedItemCapacity = stackCapacity * drawer.getStoredItemStackSize();
            if (drawer.getMaxCapacity() - addedItemCapacity < drawer.getStoredItemCount()) {
                return false;
            }
        }

        return true;
    }

    @Inject(method = "canRemoveUpgrade", at = @At("HEAD"), cancellable = true)
    private void gtmt$canRemoveUpgrade(int slot, CallbackInfoReturnable<Boolean> cir) {
        ItemStack upgrade = this.getUpgrade(slot);

        if (upgrade == null || upgrade.isEmpty()) {
            return;
        }

        int currentCustomMultiplier = gtmt$getCustomMultiplier(upgrade);

        if (currentCustomMultiplier <= 0) {
            return;
        }

        long rawBefore = gtmt$getRawTotalMultiplier();
        long rawAfter = rawBefore - (long) currentCustomMultiplier;
        if (rawAfter < 1L) {
            rawAfter = 1L;
        }

        int effBefore = (int) MathHelper.clamp(rawBefore, 1, MAX_MULTIPLIER);
        int effAfter = (int) MathHelper.clamp(rawAfter, 1, MAX_MULTIPLIER);

        if (effBefore == effAfter) {
            cir.setReturnValue(true);
            return;
        }

        cir.setReturnValue(gtmt$stackCapacityCheckByEffectiveMultiplier(effAfter));
    }

    @Inject(method = "canSwapUpgrade", at = @At("HEAD"), cancellable = true)
    private void gtmt$canSwapUpgrade(int slot, @NotNull ItemStack add, CallbackInfoReturnable<Boolean> cir) {
        ItemStack current = this.getUpgrade(slot);
        if (current == null || current.isEmpty()) {
            return;
        }

        int currentCustomMultiplier = gtmt$getCustomMultiplier(current);
        int addCustomMultiplier = gtmt$getCustomMultiplier(add);

        if (currentCustomMultiplier <= 0 && addCustomMultiplier <= 0) {
            return;
        }

        if (!this.canRemoveUpgrade(slot) || !this.canAddUpgrade(add)) {
            cir.setReturnValue(false);
            return;
        }

        if (current.getItem() == ModItems.upgradeOneStack) {
            cir.setReturnValue(true);
            return;
        }

        if (addCustomMultiplier > 0) {
            cir.setReturnValue(true);
            return;
        }

        long rawBefore = gtmt$getRawTotalMultiplier();
        long rawAfter = rawBefore - (long) currentCustomMultiplier + (long) addCustomMultiplier;
        if (rawAfter < 1L) {
            rawAfter = 1L;
        }

        int effBefore = (int) MathHelper.clamp(rawBefore, 1, MAX_MULTIPLIER);
        int effAfter = (int) MathHelper.clamp(rawAfter, 1, MAX_MULTIPLIER);

        if (effBefore == effAfter) {
            cir.setReturnValue(true);
            return;
        }

        cir.setReturnValue(gtmt$stackCapacityCheckByEffectiveMultiplier(effAfter));
    }
}
