package com.github.gtexpert.gtmt.mixins.tic;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import gregtech.api.items.toolitem.IGTTool;

import slimeknights.tconstruct.library.tools.TinkerToolCore;
import slimeknights.tconstruct.library.utils.ToolHelper;

/**
 * Extends {@code EntityPlayer.canHarvestBlock} to consider the TiC off-hand tool
 * when the GT main-hand tool's {@code getHarvestLevel} returns -1 (wrong tool type)
 * and {@code ForgeHooks.canHarvestBlock} falls back to this method.
 */
@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer {

    @Inject(
            method = "canHarvestBlock(Lnet/minecraft/block/state/IBlockState;)Z",
            at = @At("RETURN"),
            cancellable = true)
    private void checkOffHandForHarvest(IBlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return;

        EntityPlayer player = (EntityPlayer) (Object) this;
        ItemStack mainhand = player.getHeldItemMainhand();
        ItemStack offhand = player.getHeldItemOffhand();
        if (offhand.isEmpty()) return;

        if (mainhand.getItem() instanceof IGTTool && offhand.getItem() instanceof TinkerToolCore) {
            if (ToolHelper.canHarvest(offhand, state)) {
                cir.setReturnValue(true);
            }
        }
    }
}
