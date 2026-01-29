package com.github.gtexpert.gtmoretools.integration.chisel;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import org.jetbrains.annotations.NotNull;

import gregtech.api.items.toolitem.IGTTool;

import com.github.gtexpert.gtmoretools.api.ModValues;
import com.github.gtexpert.gtmoretools.api.modules.TModule;
import com.github.gtexpert.gtmoretools.api.util.Mods;
import com.github.gtexpert.gtmoretools.integration.IntegrationSubmodule;
import com.github.gtexpert.gtmoretools.integration.chisel.loaders.ChiselOreDictionaryLoader;
import com.github.gtexpert.gtmoretools.integration.chisel.metatileentities.ChiselMetaTileEntities;
import com.github.gtexpert.gtmoretools.integration.chisel.recipes.ChiselBlocksRecipe;
import com.github.gtexpert.gtmoretools.integration.chisel.recipes.ChiselToolsRecipe;
import com.github.gtexpert.gtmoretools.integration.chisel.tools.ChiselToolItems;
import com.github.gtexpert.gtmoretools.integration.chisel.tools.ChiselToolRecipeHandler;
import com.github.gtexpert.gtmoretools.modules.Modules;

@TModule(
         moduleID = Modules.MODULE_CHISEL,
         containerID = ModValues.MODID,
         modDependencies = Mods.Names.CHISEL,
         name = "GTExpert Chisel Integration",
         description = "Chisel Integration Module")
public class ChiselModule extends IntegrationSubmodule {

    @NotNull
    @Override
    public List<Class<?>> getEventBusSubscribers() {
        return Collections.singletonList(ChiselModule.class);
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        ChiselToolItems.init();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        ChiselBlocksRecipe.registerAutoChiselRecipe();
    }

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        for (IGTTool tool : ChiselToolItems.getAllTools()) {
            registry.register(tool.get());
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onRegisterModels(ModelRegistryEvent event) {
        ChiselToolItems.registerModels();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onRegisterColors(ColorHandlerEvent.Item event) {
        ChiselToolItems.registerColors(event.getItemColors());
    }

    @Override
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        ChiselMetaTileEntities.init();
    }

    @Override
    public void registerRecipesNormal(RegistryEvent.Register<IRecipe> event) {
        ChiselOreDictionaryLoader.init();
        ChiselToolItems.registerOreDict();
    }

    @Override
    public void registerRecipesLowest(RegistryEvent.Register<IRecipe> event) {
        ChiselBlocksRecipe.init();
        ChiselToolsRecipe.init();
        ChiselToolRecipeHandler.registerRecipes();
    }
}
