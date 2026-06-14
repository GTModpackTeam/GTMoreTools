package com.github.gtexpert.gtmt.core.tools;

import net.minecraftforge.common.config.Config;

import com.github.gtexpert.gtmt.api.ModValues;
import com.github.gtexpert.gtmt.modules.Modules;

@Config(modid = ModValues.MODID, name = ModValues.MODID + "/" + Modules.MODULE_TOOLS)
public class ToolsConfigHolder {

    @Config.Comment({ "Enable tool restrictions and JEI item hiding.", "default: false" })
    public static boolean enable = false;

    @Config.Comment({
            "Items to apply restrictions to.",
            "Format: modid:item_name@meta, maxDamage, hideFromJEI",
            "  maxDamage   0 = no restriction; positive = set max durability to this value",
            "  hideFromJEI true = hide from JEI and remove crafting recipes",
            "Example: minecraft:wooden_sword, 0, false",
            "Example: appliedenergistics2:certus_quartz_sword, 0, false" })
    public static String[] items = {
            // Vanilla tools — durability restriction only
            "minecraft:wooden_sword, 0, false",
            "minecraft:wooden_shovel, 0, false",
            "minecraft:wooden_pickaxe, 0, false",
            "minecraft:wooden_axe, 0, false",
            "minecraft:stone_sword, 0, false",
            "minecraft:stone_shovel, 0, false",
            "minecraft:stone_pickaxe, 0, false",
            "minecraft:stone_axe, 0, false",
            "minecraft:iron_sword, 0, false",
            "minecraft:iron_shovel, 0, false",
            "minecraft:iron_pickaxe, 0, false",
            "minecraft:iron_axe, 0, false",
            "minecraft:golden_sword, 0, false",
            "minecraft:golden_shovel, 0, false",
            "minecraft:golden_pickaxe, 0, false",
            "minecraft:golden_axe, 0, false",
            "minecraft:diamond_sword, 0, false",
            "minecraft:diamond_shovel, 0, false",
            "minecraft:diamond_pickaxe, 0, false",
            "minecraft:diamond_axe, 0, false",
            // AE2 tools — JEI hide + recipe removal
            "appliedenergistics2:certus_quartz_sword, 0, false",
            "appliedenergistics2:certus_quartz_spade, 0, false",
            "appliedenergistics2:certus_quartz_pickaxe, 0, false",
            "appliedenergistics2:certus_quartz_axe, 0, false",
            "appliedenergistics2:nether_quartz_sword, 0, false",
            "appliedenergistics2:nether_quartz_spade, 0, false",
            "appliedenergistics2:nether_quartz_pickaxe, 0, false",
            "appliedenergistics2:nether_quartz_axe, 0, false",
            // ArchitectureCraft — JEI hide + recipe removal
            "architecturecraft:sawblade, 0, false",
            "architecturecraft:largepulley, 0, false"
    };
}
