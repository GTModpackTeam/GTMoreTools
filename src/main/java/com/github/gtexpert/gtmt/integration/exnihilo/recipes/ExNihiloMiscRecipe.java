package com.github.gtexpert.gtmt.integration.exnihilo.recipes;

import static com.github.gtexpert.gtmt.integration.exnihilo.metatileentities.ExNihiloMetaTileEntities.*;
import static gregtech.common.blocks.BlockSteamCasing.SteamCasingType.BRONZE_HULL;
import static gregtech.loaders.recipe.CraftingComponent.*;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.blocks.StoneVariantBlock;
import gregtech.common.blocks.StoneVariantBlock.StoneVariant;
import gregtech.loaders.recipe.MetaTileEntityLoader;

import com.github.gtexpert.gtmt.api.util.Mods;
import com.github.gtexpert.gtmt.integration.exnihilo.ExNihiloConfigHolder;
import com.github.gtexpert.gtmt.integration.exnihilo.items.ExNihiloItems;
import com.github.gtexpert.gtmt.integration.exnihilo.items.ItemGTMTPebbles;

import exnihilocreatio.ModBlocks;
import exnihilocreatio.modules.AppliedEnergistics2;

public class ExNihiloMiscRecipe {

    public static void init() {
        itemRecipe();
        machineRecipe();
    }

    private static void itemRecipe() {
        // Mesh
        if (ExNihiloConfigHolder.harderMeshes) {
            ItemStack mesh = Mods.ExNihilo.getItem("item_mesh", 1, 1);
            // Flint -> Tin Alloy
            ModHandler.removeRecipeByOutput(Mods.ExNihilo.getItem("item_mesh", 1, 2));
            ModHandler.addShapedRecipe("tin_alloy_mesh", Mods.ExNihilo.getItem("item_mesh", 1, 2), "RIR", "RMR", "RIR",
                    'R', new UnificationEntry(OrePrefix.stick, Materials.TinAlloy),
                    'I', new ItemStack(Items.FLINT),
                    'M', mesh);
            // Iron -> Steel
            ModHandler.removeRecipeByOutput(Mods.ExNihilo.getItem("item_mesh", 1, 3));
            ModHandler.addShapedRecipe("steel_mesh", Mods.ExNihilo.getItem("item_mesh", 1, 3), "RIR", "RMR", "RIR",
                    'R', new UnificationEntry(OrePrefix.stick, Materials.Steel),
                    'I', new UnificationEntry(OrePrefix.ingot, Materials.Iron),
                    'M', mesh);
            // Diamond -> Aluminum
            ModHandler.removeRecipeByOutput(Mods.ExNihilo.getItem("item_mesh", 1, 4));
            ModHandler.addShapedRecipe("aluminium_mesh", Mods.ExNihilo.getItem("item_mesh", 1, 4), "RIR", "RMR", "RIR",
                    'R', new UnificationEntry(OrePrefix.stick, Materials.Aluminium),
                    'I', new UnificationEntry(OrePrefix.gem, Materials.Diamond),
                    'M', mesh);
        }

        // Hammer conversion
        RecipeMaps.FORGE_HAMMER_RECIPES.recipeBuilder()
                .input(Blocks.SAND)
                .output(ModBlocks.dust)
                .EUt(16).duration(10)
                .buildAndRegister();

        RecipeMaps.FORGE_HAMMER_RECIPES.recipeBuilder()
                .inputs(new ItemStack(Blocks.STONE, 1, 1))
                .output(ModBlocks.crushedGranite)
                .EUt(16).duration(10)
                .buildAndRegister();

        RecipeMaps.FORGE_HAMMER_RECIPES.recipeBuilder()
                .inputs(new ItemStack(Blocks.STONE, 1, 3))
                .output(ModBlocks.crushedDiorite)
                .EUt(16).duration(10)
                .buildAndRegister();

        RecipeMaps.FORGE_HAMMER_RECIPES.recipeBuilder()
                .inputs(new ItemStack(Blocks.STONE, 1, 5))
                .output(ModBlocks.crushedAndesite)
                .EUt(16).duration(10)
                .buildAndRegister();

        RecipeMaps.FORGE_HAMMER_RECIPES.recipeBuilder()
                .input(Blocks.NETHERRACK)
                .output(ModBlocks.netherrackCrushed)
                .EUt(16).duration(10)
                .buildAndRegister();

        RecipeMaps.FORGE_HAMMER_RECIPES.recipeBuilder()
                .input(Blocks.END_STONE)
                .output(ModBlocks.endstoneCrushed)
                .EUt(16).duration(10)
                .buildAndRegister();

        if (Mods.AppliedEnergistics2.isModLoaded()) {
            RecipeMaps.FORGE_HAMMER_RECIPES.recipeBuilder()
                    .inputs(Mods.AppliedEnergistics2.getItem("sky_stone_block"))
                    .output(AppliedEnergistics2.skystoneCrushed)
                    .EUt(16)
                    .duration(10)
                    .buildAndRegister();
        }

        // Pebbles
        EnumMap<StoneVariant, List<ItemStack>> variantListMap = new EnumMap<>(
                StoneVariant.class);
        for (StoneVariant shape : StoneVariant.values()) {
            StoneVariantBlock block = MetaBlocks.STONE_BLOCKS.get(shape);
            variantListMap.put(shape,
                    Arrays.stream(StoneVariantBlock.StoneType.values())
                            .map(block::getItemVariant)
                            .collect(Collectors.toList()));
        }
        List<ItemStack> cobbles = variantListMap.get(StoneVariant.COBBLE);
        for (ItemGTMTPebbles.GTPebbles pebble : ItemGTMTPebbles.GTPebbles.VALUES) {
            String name = pebble.getName();
            int i = pebble.ordinal();
            ModHandler.addShapedRecipe(name, cobbles.get(i),
                    "PP", "PP",
                    'P', new ItemStack(ExNihiloItems.pebbleItem, 1, i));
        }
    }

    private static void machineRecipe() {
        // Machine Recipes
        MetaTileEntityLoader.registerMachineRecipe(SIEVES, "CPC", "FMF", "OSO",
                'M', HULL, 'C', CIRCUIT, 'O', CABLE,
                'F', CONVEYOR, 'S', new ItemStack(ModBlocks.sieve), 'P', PISTON);
        ModHandler.addShapedRecipe(true, "steam_sieve_bronze", STEAM_SIEVE_BRONZE.getStackForm(), "BPB", "BMB", "BSB",
                'B', new UnificationEntry(OrePrefix.pipeSmallFluid, Materials.Bronze), 'M',
                MetaBlocks.STEAM_CASING.getItemVariant(BRONZE_HULL), 'S', new ItemStack(ModBlocks.sieve), 'P',
                Blocks.PISTON);
        ModHandler.addShapedRecipe(true, "steam_sieve_steel", STEAM_SIEVE_STEEL.getStackForm(), "BPB", "WMW", "BBB",
                'B', new UnificationEntry(OrePrefix.pipeSmallFluid, Materials.TinAlloy), 'M',
                STEAM_SIEVE_BRONZE.getStackForm(), 'W', new UnificationEntry(OrePrefix.plate, Materials.WroughtIron),
                'P', new UnificationEntry(OrePrefix.plate, Materials.Steel));
    }
}
