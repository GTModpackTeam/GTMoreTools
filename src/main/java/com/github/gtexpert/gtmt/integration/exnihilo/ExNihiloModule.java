package com.github.gtexpert.gtmt.integration.exnihilo;

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

import org.jetbrains.annotations.NotNull;

import com.github.gtexpert.gtmt.api.ModValues;
import com.github.gtexpert.gtmt.api.modules.TModule;
import com.github.gtexpert.gtmt.api.util.Mods;
import com.github.gtexpert.gtmt.integration.IntegrationModule;
import com.github.gtexpert.gtmt.modules.Modules;

@TModule(
         moduleID = Modules.MODULE_EXNIHILO,
         containerID = ModValues.MODID,
         modDependencies = Mods.Names.EX_NIHOLO,
         name = "GTMoreTools Ex Nihilo Creatio Integration",
         description = "Ex Nihilo Creatio Module")
public class ExNihiloModule extends IntegrationModule {

    @NotNull
    @Override
    public List<Class<?>> getEventBusSubscribers() {
        return Collections.singletonList(ExNihiloModule.class);
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {}

    @Override
    public void postInit(FMLPostInitializationEvent event) {}

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event) {}

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onRegisterModels(ModelRegistryEvent event) {}

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onRegisterColors(ColorHandlerEvent.Item event) {}

    @Override
    public void registerBlocks(RegistryEvent.Register<Block> event) {}

    @Override
    public void registerRecipesNormal(RegistryEvent.Register<IRecipe> event) {}

    @Override
    public void registerRecipesLowest(RegistryEvent.Register<IRecipe> event) {}
}
