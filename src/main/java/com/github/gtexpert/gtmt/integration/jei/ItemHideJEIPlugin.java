package com.github.gtexpert.gtmt.integration.jei;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import com.github.gtexpert.gtmt.core.ToolsModule;
import com.github.gtexpert.gtmt.core.tools.ToolsConfigHolder;
import com.github.gtexpert.gtmt.core.tools.ToolsEntry;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientBlacklist;

@JEIPlugin
public class ItemHideJEIPlugin implements IModPlugin {

    @Override
    public void register(@Nonnull IModRegistry registry) {
        if (!ToolsConfigHolder.enable) return;

        List<ToolsEntry> entries = ToolsModule.getEntries();
        if (entries.isEmpty()) return;

        IIngredientBlacklist blacklist = registry.getJeiHelpers().getIngredientBlacklist();
        for (ToolsEntry entry : entries) {
            if (!entry.hideFromJEI) continue;
            ItemStack stack = entry.toStack();
            if (!stack.isEmpty()) {
                blacklist.addIngredientToBlacklist(stack);
            }
        }
    }
}
