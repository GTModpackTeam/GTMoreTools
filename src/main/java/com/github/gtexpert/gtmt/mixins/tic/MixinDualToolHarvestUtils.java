package com.github.gtexpert.gtmt.mixins.tic;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import gregtech.api.items.toolitem.IGTTool;

import slimeknights.tconstruct.library.tools.DualToolHarvestUtils;
import slimeknights.tconstruct.library.tools.TinkerToolCore;
import slimeknights.tconstruct.library.utils.ToolHelper;

/**
 * Bridges GT tools into TiC's {@code shouldUseOffhand} system for cross-hand combinations.
 *
 * <ul>
 * <li>GT main + TiC off: returns {@code true} when GT cannot harvest but TiC can, so that
 * {@link MixinItemGTTool} performs the hand-swap and TiC mines as main-hand.</li>
 * <li>TiC main + GT off: returns {@code true} when TiC cannot harvest but GT can, activating
 * TiC's {@code offhandBreakSpeed}, {@code getHarvestLevel}, and {@code onBlockDestroyed}
 * delegation.</li>
 * </ul>
 */
@Mixin(value = DualToolHarvestUtils.class, remap = false)
public class MixinDualToolHarvestUtils {

    @Inject(method = "shouldUseOffhand(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/item/ItemStack;)Z",
            at = @At("HEAD"),
            cancellable = true)
    private static void gtOffHandSupportState(EntityLivingBase entity, IBlockState state,
                                              ItemStack tool,
                                              CallbackInfoReturnable<Boolean> cir) {
        if (!(entity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) entity;
        ItemStack offhand = player.getHeldItemOffhand();
        if (tool.isEmpty() || offhand.isEmpty() || state == null) return;

        if (tool.getItem() instanceof TinkerToolCore && offhand.getItem() instanceof IGTTool) {
            if (!ToolHelper.canHarvest(tool, state) && gtmt$gtCanHarvest(offhand, state)) {
                cir.setReturnValue(true);
            }
            return;
        }

        if (tool.getItem() instanceof IGTTool && offhand.getItem() instanceof TinkerToolCore) {
            if (gtmt$shouldPreferOffhand(tool, offhand, state)) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "shouldUseOffhand(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/ItemStack;)Z",
            at = @At("HEAD"),
            cancellable = true)
    private static void gtOffHandSupportPos(EntityLivingBase entity, BlockPos pos,
                                            ItemStack tool,
                                            CallbackInfoReturnable<Boolean> cir) {
        if (!(entity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) entity;
        IBlockState state = entity.getEntityWorld().getBlockState(pos);
        ItemStack offhand = player.getHeldItemOffhand();
        if (tool.isEmpty() || offhand.isEmpty() || state == null) return;

        if (tool.getItem() instanceof TinkerToolCore && offhand.getItem() instanceof IGTTool) {
            if (!ToolHelper.canHarvest(tool, state) && gtmt$gtCanHarvest(offhand, state)) {
                cir.setReturnValue(true);
            }
            return;
        }

        if (tool.getItem() instanceof IGTTool && offhand.getItem() instanceof TinkerToolCore) {
            if (gtmt$shouldPreferOffhand(tool, offhand, state)) {
                cir.setReturnValue(true);
            }
        }
    }

    @Unique
    private static boolean gtmt$gtCanHarvest(ItemStack gt, IBlockState state) {
        if (state.getMaterial().isToolNotRequired()) return false;
        Block block = state.getBlock();
        String toolType = block.getHarvestTool(state);
        if (toolType == null) return false;
        int level = gt.getItem().getHarvestLevel(gt, toolType, null, state);
        return level >= 0 && level >= block.getHarvestLevel(state);
    }

    @Unique
    private static boolean gtmt$shouldPreferOffhand(ItemStack main, ItemStack offhand, IBlockState state) {
        if (state.getMaterial().isToolNotRequired()) return false;
        Block block = state.getBlock();
        String toolType = block.getHarvestTool(state);
        if (toolType == null) return false;
        int requiredLevel = block.getHarvestLevel(state);

        int mainRaw = main.getItem().getHarvestLevel(main, toolType, null, state);
        int offRaw = offhand.getItem().getHarvestLevel(offhand, toolType, null, state);

        if (requiredLevel >= 0 && offRaw >= requiredLevel && mainRaw < requiredLevel) return true;
        if (mainRaw < 0 && offRaw >= 0) return true;

        return false;
    }
}
