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
 * Extends TiC's off-hand mining to work with GT tools.
 *
 * <ul>
 * <li>GT main-hand + TiC off-hand: uses TiC dig speed and generates drops when GT cannot harvest.</li>
 * <li>TiC main-hand + GT off-hand: uses GT dig speed when TiC cannot harvest.</li>
 * </ul>
 *
 * Runs at {@link EventPriority#LOW} so TiC's own {@code BreakSpeed} handler fires first.
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
     * For Case 1, also spawns drops using the off-hand TiC tool when
     * {@code ForgeHooks.canHarvestBlock} returns {@code false} for the GT main-hand tool.
     *
     * Runs at {@link EventPriority#LOWEST} so cancellations by other mods are resolved first.
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
                if (!ForgeHooks.canHarvestBlock(state.getBlock(), player, player.world, event.getPos())) {
                    state.getBlock().harvestBlock(player.world, player, event.getPos(), state,
                            player.world.getTileEntity(event.getPos()), offhand);
                }
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
        int level = stack.getItem().getHarvestLevel(stack, toolType, player, state);
        if (level < 0) return player.canHarvestBlock(state);
        return level >= block.getHarvestLevel(state);
    }
}
