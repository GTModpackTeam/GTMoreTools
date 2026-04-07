package com.github.gtexpert.gtmt.mixins.storagedrawers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.jaquadro.minecraft.storagedrawers.inventory.InventoryUpgrade;
import com.jaquadro.minecraft.storagedrawers.inventory.SlotUpgrade;

import com.github.gtexpert.gtmt.integration.storagedrawers.storageupgrades.IGTMaterialStorageUpgrade;

@Mixin(value = SlotUpgrade.class, remap = false)
public abstract class SlotUpgradeMixin extends Slot {

    public SlotUpgradeMixin(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Unique
    private boolean gtmt$isCustomStorageUpgrade(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.getItem() instanceof IGTMaterialStorageUpgrade;
    }

    @Inject(method = "canTakeStack", at = @At("HEAD"), cancellable = true)
    private void gtmt$canTakeStack(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        if (!(inventory instanceof InventoryUpgrade inventoryUpgrade)) {
            return;
        }

        ItemStack stack = this.getStack();
        if (!gtmt$isCustomStorageUpgrade(stack)) {
            return;
        }

        cir.setReturnValue(inventoryUpgrade.canRemoveStorageUpgrade(this.getSlotIndex()));
    }
}
