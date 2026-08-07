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
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import org.jetbrains.annotations.NotNull;

import gregtech.api.gui.resources.SteamTexture;
import gregtech.api.items.toolitem.IGTTool;

import com.github.gtexpert.gtmt.api.ModValues;
import com.github.gtexpert.gtmt.api.modules.TModule;
import com.github.gtexpert.gtmt.api.util.Mods;
import com.github.gtexpert.gtmt.integration.IntegrationSubmodule;
import com.github.gtexpert.gtmt.integration.exnihilo.items.ExNihiloItems;
import com.github.gtexpert.gtmt.integration.exnihilo.metatileentities.ExNihiloMetaTileEntities;
import com.github.gtexpert.gtmt.integration.exnihilo.recipes.*;
import com.github.gtexpert.gtmt.integration.exnihilo.recipes.sieve.SieveDrops;
import com.github.gtexpert.gtmt.integration.exnihilo.recipes.sieve.VeinProbabilityReporter;
import com.github.gtexpert.gtmt.integration.exnihilo.tools.ExNihiloToolsItems;
import com.github.gtexpert.gtmt.modules.Modules;

import exnihilocreatio.registries.manager.ExNihiloRegistryManager;

@TModule(
         moduleID = Modules.MODULE_EXNIHILO,
         containerID = ModValues.MODID,
         modDependencies = Mods.Names.EX_NIHILO,
         name = "GTMoreTools Ex Nihilo Creatio Integration",
         description = "Ex Nihilo Creatio Module")
public class ExNihiloModule extends IntegrationSubmodule {

    public static final SteamTexture PROGRESS_BAR_SIFTER_STEAM = SteamTexture
            .fullImage("textures/gui/progress_bar/progress_bar_sift_%s.png");

    @NotNull
    @Override
    public List<Class<?>> getEventBusSubscribers() {
        return new ArrayList<>(Arrays.asList(ExNihiloModule.class, ExNihiloEventHandlers.class));
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        ExNihiloToolsItems.init();
        ExNihiloItems.init();
        ExNihiloMetaTileEntities.init();
    }

    @Override
    public void init(FMLInitializationEvent evet) {
        ExNihiloRegistryManager.registerSieveDefaultRecipeHandler(new SieveDrops());
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        if (ExNihiloConfigHolder.outputVeinProbabilities) {
            VeinProbabilityReporter.output();
        }
    }

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        for (IGTTool tool : ExNihiloToolsItems.getAllTools()) {
            registry.register(tool.get());
        }
        registry.register(ExNihiloItems.pebbleItem);
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
    public void registerRecipesNormal(RegistryEvent.Register<IRecipe> event) {
        SieveDrops.readSieveDropsFromConfig();
    }

    @Override
    public void registerRecipesLowest(RegistryEvent.Register<IRecipe> event) {
        ExNihiloUtil.init();
        ExNihiloToolRecipe.registerRecipes();
        ExNihiloMiscRecipe.init();
    }
}
