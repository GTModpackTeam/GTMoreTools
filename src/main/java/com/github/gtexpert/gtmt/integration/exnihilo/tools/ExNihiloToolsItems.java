package com.github.gtexpert.gtmt.integration.exnihilo.tools;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import gregtech.api.GTValues;
import gregtech.api.items.toolitem.IGTTool;
import gregtech.api.items.toolitem.ItemGTTool;
import gregtech.api.unification.OreDictUnifier;

import com.github.gtexpert.gtmt.api.ModValues;

public final class ExNihiloToolsItems {

    private static final List<IGTTool> TOOLS = new ArrayList<>();

    public static IGTTool CROOK;

    public static List<IGTTool> getAllTools() {
        return TOOLS;
    }

    public static void init() {
        CROOK = register(ItemGTTool.Builder.of(ModValues.MODID, "crook")
                .toolStats(b -> b.blockBreaking().attackDamage(1.0F).attackSpeed(1.0F))
                .oreDict("toolCrook")
                .toolClasses("crook")
                .build());
    }

    public static IGTTool register(IGTTool tool) {
        TOOLS.add(tool);
        return tool;
    }

    @SideOnly(Side.CLIENT)
    public static void registerModels() {
        TOOLS.forEach(tool -> ModelLoader.setCustomModelResourceLocation(tool.get(), 0, tool.getModelLocation()));
    }

    @SideOnly(Side.CLIENT)
    public static void registerColors(ItemColors itemColors) {
        TOOLS.forEach(tool -> itemColors.registerItemColorHandler(tool::getColor, tool.get()));
    }

    public static void registerOreDict() {
        TOOLS.forEach(tool -> {
            final ItemStack stack = new ItemStack(tool.get(), 1, GTValues.W);
            if (tool.getOreDictName() != null) {
                OreDictUnifier.registerOre(stack, tool.getOreDictName());
            }
            tool.getSecondaryOreDicts().forEach(oreDict -> OreDictUnifier.registerOre(stack, oreDict));
        });
    }
}
