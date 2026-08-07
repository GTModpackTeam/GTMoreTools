package com.github.gtexpert.gtmt.integration.exnihilo.metatileentities;

import static com.github.gtexpert.gtmt.api.util.ModUtility.id;
import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntities;
import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntity;

import gregtech.api.GTValues;

public class ExNihiloMetaTileEntities {

    public static MetaTileEntitySteamSieve STEAM_SIEVE_BRONZE;
    public static MetaTileEntitySteamSieve STEAM_SIEVE_STEEL;
    public static MetaTileEntityElectricSieve[] SIEVES = new MetaTileEntityElectricSieve[GTValues.V.length - 1];

    public static void init() {
        STEAM_SIEVE_BRONZE = registerMetaTileEntity(13000, new MetaTileEntitySteamSieve(id("sieve_steam"), false));
        STEAM_SIEVE_STEEL = registerMetaTileEntity(13001, new MetaTileEntitySteamSieve(id("sieve_steam.steel"), true));
        registerMetaTileEntities(SIEVES, 13002, "electric_sieve",
                (tier, voltageName) -> new MetaTileEntityElectricSieve(
                        id(String.format("electric_sieve.%s", voltageName)), tier));
    }
}
