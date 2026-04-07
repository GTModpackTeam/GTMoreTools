package com.github.gtexpert.gtmt.mixins.storagedrawers;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.UpgradeData;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;

import com.github.gtexpert.gtmt.integration.storagedrawers.storageupgrades.IGTMaterialStorageUpgrade;

@Mixin(targets = "com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers$DrawerUpgradeData", remap = false)
public abstract class DrawerUpgradeDataMixin extends UpgradeData {

    protected DrawerUpgradeDataMixin() {
        super(0); // 実際には使われない
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

    /**
     * 独自 Storage Upgrade の「追加倍率」を返す。
     * 本体 upgradeStorage はここでは触らず、0 を返して vanilla に任せる。
     */
    @Unique
    private int gtmt$getCustomAddedStorageMultiplier(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return 0;
        }

        if (stack.getItem() instanceof IGTMaterialStorageUpgrade) {
            return ((IGTMaterialStorageUpgrade) stack.getItem()).getStorageMultiplier(stack);
        }

        return 0;
    }

    /**
     * 独自 Storage Upgrade の取り外し判定だけを差し替える。
     * vanilla item はそのまま元処理へ流す。
     */
    @Inject(method = "canRemoveUpgrade", at = @At("HEAD"), cancellable = true)
    private void gtmt$canRemoveUpgrade(int slot, CallbackInfoReturnable<Boolean> cir) {
        ItemStack upgrade = this.getUpgrade(slot);
        if (upgrade == null || upgrade.isEmpty()) {
            return;
        }

        int customMultiplier = gtmt$getCustomAddedStorageMultiplier(upgrade);

        if (customMultiplier <= 0) {
            return;
        }

        int effectiveMultipier = this.getStorageMultiplier();
        int removedMultiplier = customMultiplier;

        if (effectiveMultipier == customMultiplier) {
            removedMultiplier--;
        }

        int removedStackCapacity = removedMultiplier * gtmt$outer.getEffectiveDrawerCapacity();

        cir.setReturnValue(gtmt$stackCapacityCheck(removedStackCapacity));
    }

    /**
     * 独自 Storage Upgrade の swap 判定だけを差し替える。
     * vanilla 同士の swap は元処理に任せる。
     */
    @Inject(method = "canSwapUpgrade", at = @At("HEAD"), cancellable = true)
    private void gtmt$canSwapUpgrade(int slot, @NotNull ItemStack add, CallbackInfoReturnable<Boolean> cir) {
        ItemStack current = this.getUpgrade(slot);
        if (current == null || current.isEmpty()) {
            return;
        }

        int currentCustomMulti = gtmt$getCustomAddedStorageMultiplier(current);
        int addCustomMulti = gtmt$getCustomAddedStorageMultiplier(add);

        if (currentCustomMulti <= 0 && addCustomMulti <= 0) {
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

        if (addCustomMulti > 0) {
            cir.setReturnValue(true);
            return;
        }

        if (currentCustomMulti > 0) {
            int currentMultiplier = this.getStorageMultiplier();
            int newMultiplier = currentMultiplier - currentCustomMulti;

            if (newMultiplier <= 0) {
                newMultiplier = 1;
            }

            int baseCapacity = ((AccessorTileEntityDrawers) gtmt$outer)
                    .gtmt$invokeGetEffectiveDrawerCapacity();

            int newCapacity = newMultiplier * baseCapacity;

            cir.setReturnValue(gtmt$stackCapacityCheck(newCapacity));
        }
    }
}
