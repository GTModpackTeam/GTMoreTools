package com.github.gtexpert.gtmt.integration.jei;

import javax.annotation.Nonnull;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import gregtech.api.items.toolitem.IGTTool;
import gregtech.api.items.toolitem.ToolClasses;

import com.github.gtexpert.gtmt.api.util.Mods;

import exnihilocreatio.compatibility.jei.hammer.HammerRecipeCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;

@JEIPlugin
public class ExNihiloJEIPlugin implements IModPlugin {

    @Override
    @SideOnly(Side.CLIENT)
    public void register(@Nonnull IModRegistry registry) {
        if (!Mods.ExNihilo.isModLoaded()) return;
        for (Item item : Item.REGISTRY) {
            if (item.getToolClasses(new ItemStack(item)).contains(ToolClasses.HARD_HAMMER) &&
                    item instanceof IGTTool) {
                registry.addRecipeCatalyst(new ItemStack(item), HammerRecipeCategory.UID);
            }
        }
    }
}
