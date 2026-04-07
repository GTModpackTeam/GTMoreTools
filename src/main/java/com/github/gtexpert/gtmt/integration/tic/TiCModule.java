package com.github.gtexpert.gtmt.integration.tic;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.jetbrains.annotations.NotNull;

import com.github.gtexpert.gtmt.api.ModValues;
import com.github.gtexpert.gtmt.api.modules.TModule;
import com.github.gtexpert.gtmt.api.util.Mods;
import com.github.gtexpert.gtmt.integration.IntegrationSubmodule;
import com.github.gtexpert.gtmt.integration.tic.materials.ElasticMaterialRegistrar;
import com.github.gtexpert.gtmt.integration.tic.materials.ToolMaterialRegistrar;
import com.github.gtexpert.gtmt.modules.Modules;

@TModule(
         moduleID = Modules.MODULE_TIC,
         containerID = ModValues.MODID,
         modDependencies = Mods.Names.TINKERS_CONSTRUCT,
         name = "GTMoreTools Tinkers' Construct Integration",
         description = "Tinkers' Construct Integration Module")
public class TiCModule extends IntegrationSubmodule {

    @NotNull
    @Override
    public List<Class<?>> getEventBusSubscribers() {
        return Collections.singletonList(TiCModule.class);
    }

    @Override
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        ToolMaterialRegistrar.register(event.getRegistry());
        ElasticMaterialRegistrar.register(event.getRegistry());
        TiCSmeltery.register();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onRegisterModels(ModelRegistryEvent event) {
        ToolMaterialRegistrar.registerFluidModels();
    }
}
