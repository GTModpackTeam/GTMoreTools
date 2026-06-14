package com.github.gtexpert.gtmt.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.registries.IForgeRegistryModifiable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.github.gtexpert.gtmt.Tags;
import com.github.gtexpert.gtmt.api.ModValues;
import com.github.gtexpert.gtmt.api.modules.TModule;
import com.github.gtexpert.gtmt.core.tools.ToolRestrictionHandler;
import com.github.gtexpert.gtmt.core.tools.ToolsConfigHolder;
import com.github.gtexpert.gtmt.core.tools.ToolsEntry;
import com.github.gtexpert.gtmt.modules.BaseModule;
import com.github.gtexpert.gtmt.modules.Modules;

@TModule(
         moduleID = Modules.MODULE_TOOLS,
         containerID = ModValues.MODID,
         name = "GTMoreTools Tools",
         description = "Tool durability restriction and JEI item hiding via config")
public class ToolsModule extends BaseModule {

    private static final Logger logger = LogManager.getLogger(Tags.MODNAME + " Tools");

    private static List<ToolsEntry> entries = null;

    /** Lazy-parsed entry list shared by JEI plugin, recipe removal, and restriction handler. */
    public static List<ToolsEntry> getEntries() {
        if (entries == null) {
            if (!ToolsConfigHolder.enable || ToolsConfigHolder.items.length == 0) {
                entries = Collections.emptyList();
            } else {
                entries = ToolsEntry.parse(ToolsConfigHolder.items, logger);
            }
        }
        return entries;
    }

    @Override
    public @NotNull Logger getLogger() {
        return logger;
    }

    @Override
    public List<Class<?>> getEventBusSubscribers() {
        return Collections.singletonList(ToolRestrictionHandler.class);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        ToolRestrictionHandler.apply(getEntries());
    }

    @Override
    public void registerRecipesLowest(RegistryEvent.Register<IRecipe> event) {
        if (!ToolsConfigHolder.enable) return;

        List<ToolsEntry> toProcess = getEntries();
        if (toProcess.isEmpty()) return;

        List<ToolsEntry> hideTargets = new ArrayList<>();
        for (ToolsEntry entry : toProcess) {
            if (entry.hideFromJEI) hideTargets.add(entry);
        }
        if (hideTargets.isEmpty()) return;

        @SuppressWarnings("unchecked")
        IForgeRegistryModifiable<IRecipe> registry = (IForgeRegistryModifiable<IRecipe>) event.getRegistry();

        List<ResourceLocation> keysToRemove = new ArrayList<>();
        for (IRecipe recipe : registry.getValuesCollection()) {
            ItemStack output = recipe.getRecipeOutput();
            if (output.isEmpty()) continue;
            ResourceLocation outputRl = output.getItem().getRegistryName();
            for (ToolsEntry target : hideTargets) {
                if (target.registryName.equals(outputRl) && output.getMetadata() == target.meta) {
                    keysToRemove.add(registry.getKey(recipe));
                    break;
                }
            }
        }

        for (ResourceLocation key : keysToRemove) {
            registry.remove(key);
            logger.info("Tools: Removed recipe '{}'", key);
        }
    }
}
