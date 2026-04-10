package com.github.gtexpert.gtmt.integration.tic;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import gregtech.api.items.toolitem.IGTTool;

import slimeknights.tconstruct.library.tools.TinkerToolCore;
import slimeknights.tconstruct.library.utils.ToolHelper;

/**
 * Extends TiC's off-hand mining to cover GT + TiC cross-hand combinations.
 *
 * <ul>
 * <li>GT main + TiC off: applies TiC dig speed and drop logic when GT cannot harvest.</li>
 * <li>TiC main + GT off: applies GT dig speed when TiC cannot harvest.</li>
 * </ul>
 *
 * Registered at {@link EventPriority#LOW} so TiC's own {@code BreakSpeed} handler fires first.
 */
public final class DualToolHandler {

    private DualToolHandler() {}

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onBreakSpeed(net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed event) {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack mainhand = player.getHeldItemMainhand();
        ItemStack offhand = player.getHeldItemOffhand();

        if (offhand.isEmpty()) return;

        IBlockState state = event.getState();

        if (mainhand.getItem() instanceof IGTTool && offhand.getItem() instanceof TinkerToolCore) {
            if (!canHarvestForDrops(player, mainhand, state) && ToolHelper.canHarvest(offhand, state)) {
                float speed = ToolHelper.calcDigSpeed(offhand, state);
                BlockPos pos = event.getPos();
                if (pos == null || !ForgeHooks.canHarvestBlock(state.getBlock(), player, player.world, pos)) {
                    // Block hardness is divided by 100 instead of 30 when canHarvestBlock
                    // returns false; multiply to restore TiC's intended mining speed.
                    speed *= (100.0f / 30.0f);
                }
                event.setNewSpeed(speed);
            }
        }

        if (mainhand.getItem() instanceof TinkerToolCore && offhand.getItem() instanceof IGTTool) {
            if (!ToolHelper.canHarvest(mainhand, state) && canHarvestForDrops(player, offhand, state)) {
                float speed = offhand.getItem().getDestroySpeed(offhand, state);
                if (speed > 1.0f) {
                    int efficiency = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, offhand);
                    if (efficiency > 0) speed += efficiency * efficiency + 1;
                }
                event.setNewSpeed(speed);
            }
        }
    }

    /**
     * Damages the off-hand tool after a block is broken via off-hand mining.
     * Drops are handled by vanilla: {@code MixinEntityPlayer} makes
     * {@code player.canHarvestBlock()} return {@code true} for GT+TiC combos,
     * so {@code tryHarvestBlock} calls {@code harvestBlock} exactly once.
     *
     * Registered at {@link EventPriority#LOWEST} so other mods' cancellations are resolved first.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.isCanceled()) return;

        EntityPlayer player = event.getPlayer();
        if (player == null || player.world.isRemote) return;

        ItemStack mainhand = player.getHeldItemMainhand();
        ItemStack offhand = player.getHeldItemOffhand();

        if (offhand.isEmpty()) return;

        IBlockState state = event.getState();

        if (mainhand.getItem() instanceof IGTTool && offhand.getItem() instanceof TinkerToolCore) {
            if (!canHarvestForDrops(player, mainhand, state) && ToolHelper.canHarvest(offhand, state)) {
                // Drops are provided by vanilla: MixinEntityPlayer makes player.canHarvestBlock()
                // return true for this combo, so tryHarvestBlock's harvestBlock() call fires normally.
                // Only damage the TiC off-hand tool for its contribution to the harvest.
                offhand.getItem().onBlockDestroyed(offhand, player.world, state,
                        event.getPos(), player);
            }
        }

        if (mainhand.getItem() instanceof TinkerToolCore && offhand.getItem() instanceof IGTTool) {
            if (!ToolHelper.canHarvest(mainhand, state) && canHarvestForDrops(player, offhand, state)) {
                offhand.getItem().onBlockDestroyed(offhand, player.world, state,
                        event.getPos(), player);
            }
        }
    }

    private static boolean canHarvestForDrops(EntityPlayer player, ItemStack stack, IBlockState state) {
        if (state.getMaterial().isToolNotRequired()) return true;
        Block block = state.getBlock();
        String toolType = block.getHarvestTool(state);
        if (stack.isEmpty() || toolType == null) {
            return player.canHarvestBlock(state);
        }
        // Null player: bypasses MixinItemGTTool's off-hand elevation so the raw level of
        // this specific tool is returned. The fallback only fires when the tool itself
        // cannot harvest.
        int level = stack.getItem().getHarvestLevel(stack, toolType, null, state);
        if (level < 0) return player.canHarvestBlock(state);
        return level >= block.getHarvestLevel(state);
    }
}
