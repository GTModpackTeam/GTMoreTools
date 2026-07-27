package com.github.gtexpert.gtmt.integration.exnihilo;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.jetbrains.annotations.NotNull;

import gregtech.api.items.toolitem.IGTTool;
import gregtech.api.items.toolitem.ToolClasses;
import gregtech.api.unification.material.event.MaterialEvent;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.items.MetaItems;

import com.github.gtexpert.gtmt.api.unification.material.ore.GTMTOrePrefix;

import exnihilocreatio.registries.manager.ExNihiloRegistryManager;

public class ExNihiloEventHandlers {

    private ExNihiloEventHandlers() {}

    // Hammer Event
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void hammer(BlockEvent.HarvestDropsEvent event) {
        if (event.getWorld().isRemote || event.getHarvester() == null || event.isSilkTouching())
            return;

        ItemStack held = event.getHarvester().getHeldItemMainhand();

        if (!(held.getItem().getToolClasses(held).contains(ToolClasses.HARD_HAMMER) &&
                held.getItem() instanceof IGTTool)) {
            return;
        }

        Block block = event.getState().getBlock();
        ItemStack stack = new ItemStack(block);

        if (ExNihiloUtil.isContained(stack)) {
            return;
        }

        List<ItemStack> rewards = ExNihiloRegistryManager.HAMMER_REGISTRY.getRewardDrops(event.getWorld().rand,
                event.getState(),
                ((IGTTool) held.getItem()).getTotalHarvestLevel(held), event.getFortuneLevel());

        if (rewards != null && rewards.size() > 0) {
            event.getDrops().clear();
            event.setDropChance(1.0F);
            event.getDrops().addAll(rewards);
        }
    }

    // Material Event
    @SubscribeEvent
    public static void registerOrePrefix(@NotNull MaterialEvent event) {
        MetaItems.addOrePrefix(GTMTOrePrefix.oreChunk, GTMTOrePrefix.oreEnderChunk, GTMTOrePrefix.oreNetherChunk,
                GTMTOrePrefix.oreSandyChunk);
        GTMTOrePrefix.oreChunk.setAlternativeOreName(OrePrefix.ore.name());
        GTMTOrePrefix.oreEnderChunk.setAlternativeOreName(OrePrefix.oreEndstone.name());
        GTMTOrePrefix.oreNetherChunk.setAlternativeOreName(OrePrefix.oreNetherrack.name());
        GTMTOrePrefix.oreSandyChunk.setAlternativeOreName(OrePrefix.ore.name());
    }
}
