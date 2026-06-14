package com.github.gtexpert.gtmt.core.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.gtexpert.gtmt.Tags;

public class ToolRestrictionHandler {

    private static final Logger logger = LogManager.getLogger(Tags.MODNAME + " ToolRestriction");
    /** Maps restricted item registry name → configured maxDamage value. */
    private static final Map<ResourceLocation, Integer> RESTRICTED_ITEMS = new HashMap<>();

    public static void apply(List<ToolsEntry> entries) {
        if (!ToolsConfigHolder.enable) return;

        RESTRICTED_ITEMS.clear();
        for (ToolsEntry entry : entries) {
            if (entry.maxDamage <= 0) continue;
            Item item = ForgeRegistries.ITEMS.getValue(entry.registryName);
            if (item == null) {
                logger.warn("ToolRestriction: Item not found: {}", entry.registryName);
                continue;
            }
            try {
                // field_77699_b is the SRG name for Item.maxDamage
                ReflectionHelper.setPrivateValue(Item.class, item, entry.maxDamage,
                        "maxDamage", "field_77699_b");
                RESTRICTED_ITEMS.put(entry.registryName, entry.maxDamage);
                logger.info("ToolRestriction: Set maxDamage={} for {}", entry.maxDamage,
                        entry.registryName);
            } catch (Exception e) {
                logger.error("ToolRestriction: Failed to set maxDamage for {}", entry.registryName, e);
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (!ToolsConfigHolder.enable) return;
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) return;
        ResourceLocation rl = stack.getItem().getRegistryName();
        if (rl != null && RESTRICTED_ITEMS.containsKey(rl)) {
            event.getToolTip().add(TextFormatting.RED + I18n.format("gtmt.tool.restriction.tooltip"));
        }
    }
}
