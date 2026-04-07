package com.github.gtexpert.gtmt.mixins.tic;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import gregtech.api.items.toolitem.ItemGTTool;

import slimeknights.tconstruct.library.tools.DualToolHarvestUtils;
import slimeknights.tconstruct.library.tools.TinkerToolCore;

/**
 * Replicates the hand-swap logic from {@code ToolCore.onBlockStartBreak} for GT tools.
 *
 * <p>
 * When the GT main-hand tool cannot harvest a block but the TiC off-hand tool can,
 * this Mixin swaps the items and sets the {@code SwitchedHand} NBT tag so that
 * {@code ToolCore.onBlockDestroyed} automatically swaps them back after the break.
 */
@Mixin(value = ItemGTTool.class, remap = false)
public class MixinItemGTTool {

    @Inject(method = "onBlockStartBreak", at = @At("HEAD"), cancellable = true)
    private void gtDualToolHarvest(ItemStack stack, BlockPos pos, EntityPlayer player,
                                   CallbackInfoReturnable<Boolean> cir) {
        ItemStack offhand = player.getHeldItemOffhand();
        if (offhand.isEmpty() || !(offhand.getItem() instanceof TinkerToolCore)) return;

        if (!DualToolHarvestUtils.shouldUseOffhand(player, pos, stack)) return;

        // Replicate ToolCore.onBlockStartBreak swap logic:
        // 1) swap hands
        player.setHeldItem(EnumHand.MAIN_HAND, offhand);
        player.setHeldItem(EnumHand.OFF_HAND, stack);

        // 2) tag the TiC tool (now main-hand) so ToolCore.onBlockDestroyed swaps back
        NBTTagCompound tag = offhand.hasTagCompound() ? offhand.getTagCompound() : new NBTTagCompound();
        tag.setLong("SwitchedHand", player.getEntityWorld().getTotalWorldTime());
        offhand.setTagCompound(tag);

        // Return false → vanilla continues the block break with the TiC tool as main-hand
        cir.setReturnValue(false);
    }
}
