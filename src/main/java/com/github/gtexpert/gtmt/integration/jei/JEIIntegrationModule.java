package com.github.gtexpert.gtmt.integration.jei;

import com.github.gtexpert.gtmt.api.ModValues;
import com.github.gtexpert.gtmt.api.modules.TModule;
import com.github.gtexpert.gtmt.api.util.Mods;
import com.github.gtexpert.gtmt.integration.IntegrationSubmodule;
import com.github.gtexpert.gtmt.modules.Modules;

@TModule(
         moduleID = Modules.MODULE_JEI,
         containerID = ModValues.MODID,
         modDependencies = Mods.Names.JUST_ENOUGH_ITEMS,
         name = "GTMoreTools JEI Integration",
         description = "JEI item hiding integration")
public class JEIIntegrationModule extends IntegrationSubmodule {}
