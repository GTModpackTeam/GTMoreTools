package com.github.gtexpert.gtmt.core.tools;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import org.apache.logging.log4j.Logger;

public class ToolsEntry {

    public final ResourceLocation registryName;
    public final int meta;
    /** 0 = no restriction; positive = set Item.maxDamage to this value. */
    public final int maxDamage;
    public final boolean hideFromJEI;

    private ToolsEntry(ResourceLocation registryName, int meta, int maxDamage, boolean hideFromJEI) {
        this.registryName = registryName;
        this.meta = meta;
        this.maxDamage = maxDamage;
        this.hideFromJEI = hideFromJEI;
    }

    public ItemStack toStack() {
        Item item = ForgeRegistries.ITEMS.getValue(registryName);
        if (item == null) return ItemStack.EMPTY;
        return new ItemStack(item, 1, meta);
    }

    /**
     * Parses config strings into entries.
     * Format: "modid:item_name@meta, maxDamage, hideFromJEI"
     */
    public static List<ToolsEntry> parse(String[] raw, Logger logger) {
        List<ToolsEntry> result = new ArrayList<>();
        for (String line : raw) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(",", 3);
            if (parts.length != 3) {
                logger.warn("Tools: invalid entry (expected 3 comma-separated fields): '{}'", line);
                continue;
            }

            String itemPart = parts[0].trim();
            int maxDamage;
            boolean hideFromJEI;
            try {
                maxDamage = Integer.parseInt(parts[1].trim());
                hideFromJEI = Boolean.parseBoolean(parts[2].trim());
            } catch (Exception e) {
                logger.warn("Tools: invalid maxDamage or hideFromJEI value in entry: '{}'", line);
                continue;
            }

            if (maxDamage < 0) {
                logger.warn("Tools: maxDamage must be >= 0 in entry: '{}'", line);
                continue;
            }

            int meta = 0;
            int atIdx = itemPart.indexOf('@');
            if (atIdx >= 0) {
                try {
                    meta = Integer.parseInt(itemPart.substring(atIdx + 1));
                } catch (NumberFormatException e) {
                    logger.warn("Tools: invalid meta in entry: '{}'", line);
                    continue;
                }
                itemPart = itemPart.substring(0, atIdx);
            }

            if (!itemPart.contains(":")) {
                logger.warn("Tools: missing namespace in entry: '{}'", line);
                continue;
            }

            ResourceLocation rl = new ResourceLocation(itemPart);
            if (ForgeRegistries.ITEMS.getValue(rl) == null) {
                logger.warn("Tools: item not found: '{}'", itemPart);
                continue;
            }

            result.add(new ToolsEntry(rl, meta, maxDamage, hideFromJEI));
        }
        return result;
    }
}
