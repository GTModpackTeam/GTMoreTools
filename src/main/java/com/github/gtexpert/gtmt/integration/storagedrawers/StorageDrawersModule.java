package com.github.gtexpert.gtmt.integration.storagedrawers;

import static com.github.gtexpert.gtmt.integration.storagedrawers.items.StorageDrawersItems.upgradeStorageGT;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
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

import gregtech.api.unification.material.event.PostMaterialEvent;

import com.github.gtexpert.gtmt.api.ModValues;
import com.github.gtexpert.gtmt.api.modules.TModule;
import com.github.gtexpert.gtmt.api.unification.material.info.GTMTMaterialFlags;
import com.github.gtexpert.gtmt.api.util.Mods;
import com.github.gtexpert.gtmt.integration.IntegrationSubmodule;
import com.github.gtexpert.gtmt.integration.storagedrawers.items.ItemGTMaterialUpgradeStorage;
import com.github.gtexpert.gtmt.integration.storagedrawers.items.StorageDrawersItems;
import com.github.gtexpert.gtmt.integration.storagedrawers.recipes.UpgradesLoader;
import com.github.gtexpert.gtmt.integration.storagedrawers.storageupgrades.StorageUpgradeColors;
import com.github.gtexpert.gtmt.integration.storagedrawers.storageupgrades.UpgradeMaterialData;
import com.github.gtexpert.gtmt.integration.storagedrawers.storageupgrades.UpgradesMaterialRegistry;
import com.github.gtexpert.gtmt.modules.Modules;

@TModule(
         moduleID = Modules.MODULE_DRAWERS,
         containerID = ModValues.MODID,
         modDependencies = Mods.Names.STORAGE_DRAWERS,
         name = "GTMoreTools Storage Drawers Integration",
         description = "Storage Drawers Module")
public class StorageDrawersModule extends IntegrationSubmodule {

    @NotNull
    @Override
    public List<Class<?>> getEventBusSubscribers() {
        return Collections.singletonList(StorageDrawersModule.class);
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        StorageDrawersItems.init();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void init(FMLInitializationEvent event) {
        StorageUpgradeColors.init();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {}

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.register(upgradeStorageGT);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onRegisterModels(ModelRegistryEvent event) {
        ItemGTMaterialUpgradeStorage.registerModels();
    }

    @SubscribeEvent
    public static void registerMaterialFlags(PostMaterialEvent event) {
        GTMTMaterialFlags.integrationStorageDrawers();
    }

    @Override
    public void registerBlocks(RegistryEvent.Register<Block> event) {}

    @Override
    public void registerRecipesNormal(RegistryEvent.Register<IRecipe> event) {
        StorageDrawersUtil.UPGRADE_MATERIALS = StorageDrawersUtil.parse(StorageDrawersConfigHolder.upgradeMaterials);
        for (UpgradeMaterialData data : StorageDrawersUtil.UPGRADE_MATERIALS) {
            UpgradesMaterialRegistry.REGISTRY.put(data.getMaterial(), data.getId(), data.getMultiple(),
                    data.getTier());
        }
    }

    @Override
    public void registerRecipesLowest(RegistryEvent.Register<IRecipe> event) {
        UpgradesLoader.upgradeStorage();
    }
}
