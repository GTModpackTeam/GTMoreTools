package com.github.gtexpert.gtmt.integration.exnihilo;

import java.util.ArrayList;
import java.util.Arrays;
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

import com.github.gtexpert.gtmt.api.ModValues;
import com.github.gtexpert.gtmt.api.modules.TModule;
import com.github.gtexpert.gtmt.api.util.Mods;
import com.github.gtexpert.gtmt.integration.IntegrationSubmodule;
import com.github.gtexpert.gtmt.integration.exnihilo.recipes.ExNihiloToolRecipe;
import com.github.gtexpert.gtmt.integration.exnihilo.tools.ExNihiloToolsItems;
import com.github.gtexpert.gtmt.modules.Modules;

@TModule(
         moduleID = Modules.MODULE_EXNIHILO,
         containerID = ModValues.MODID,
         modDependencies = Mods.Names.EX_NIHOLO,
         name = "GTMoreTools Ex Nihilo Creatio Integration",
         description = "Ex Nihilo Creatio Module")
public class ExNihiloModule extends IntegrationSubmodule {

    @NotNull
    @Override
    public List<Class<?>> getEventBusSubscribers() {
        return new ArrayList<>(Arrays.asList(ExNihiloModule.class, ExNihiloEventHandlers.class));
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        ExNihiloToolsItems.init();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {}

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        for (IGTTool tool : ExNihiloToolsItems.getAllTools()) {
            registry.register(tool.get());
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onRegisterModels(ModelRegistryEvent event) {
        ExNihiloToolsItems.registerModels();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onRegisterColors(ColorHandlerEvent.Item event) {
        ExNihiloToolsItems.registerColors(event.getItemColors());
    }

    @Override
    public void registerBlocks(RegistryEvent.Register<Block> event) {}

    @Override
    public void registerRecipesNormal(RegistryEvent.Register<IRecipe> event) {}

    @Override
    public void registerRecipesLowest(RegistryEvent.Register<IRecipe> event) {
        ExNihiloToolRecipe.registerRecipes();
        ExNihiloUtil.init();
    }
}
