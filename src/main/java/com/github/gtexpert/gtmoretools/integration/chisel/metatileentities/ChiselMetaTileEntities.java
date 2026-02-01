package com.github.gtexpert.gtmoretools.integration.chisel.metatileentities;

import static com.github.gtexpert.gtmoretools.api.util.ModUtility.id;
import static gregtech.api.GTValues.*;
import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntities;

import gregtech.api.util.GTUtility;

import com.github.gtexpert.gtmoretools.client.ModTextures;
import com.github.gtexpert.gtmoretools.integration.chisel.ChiselRecipeMaps;

public class ChiselMetaTileEntities {

    public static MetaTileEntityAutoChisel[] AUTO_CHISEL = new MetaTileEntityAutoChisel[9]; // LV - UV

    public static void init() {
        // Auto Chisel 11001~11008
        registerMetaTileEntities(AUTO_CHISEL, 11001, "auto_chisel",
                (tier, voltageName) -> new MetaTileEntityAutoChisel(
                        id(String.format("%s.%s", "auto_chisel", voltageName)),
                        ChiselRecipeMaps.AUTO_CHISEL_RECIPES,
                        ModTextures.AUTO_CHISEL_OVERLAY,
                        tier, true, GTUtility.defaultTankSizeFunction));
    }
}
