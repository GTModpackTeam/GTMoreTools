package com.github.gtexpert.gtmt.integration.exnihilo;

import net.minecraftforge.common.config.Config;

import com.github.gtexpert.gtmt.api.ModValues;
import com.github.gtexpert.gtmt.modules.Modules;

@Config.LangKey(ModValues.MODID + ".config.integration.exnihilo")
@Config(modid = ModValues.MODID,
        name = ModValues.MODID + "/integration/" + Modules.MODULE_EXNIHILO,
        category = "ExNihilo")
public class ExNihiloConfigHolder {

    @Config.Comment({ "Config category for sieve drops" })
    @Config.RequiresMcRestart
    public static SieveCategory drops = new SieveCategory();

    @Config.Comment({ "Replaces the original Crooks with GT Crooks or recipe.", "Affected: Wood, Iron, Gold, Diamond.",
            "Default: false" })
    public static boolean replaceCrook = false;

    public static class SieveCategory {

        @Config.Comment({
                "Outputs GTCEu vein and material probability information to the log.",
                "This option is intended to help configure generated sieve drops.",
                "Default: false"
        })
        public boolean outputVeinProbabilities = false;

        @Config.Comment({ "Sand Sieve drop list.", "Format: modid:materialName@Chance*MeshLevel",
                "If no \"modid:\" is specified, \"gregtech:\" is assumed.",
                "\"Chance\" is parsed as a \"float\", and \"MeshLevel\" as an \"int\".",
                "Example: gregtech:iron@0.001*1 or iron@0.001*1" })
        public String[] sandSieveDrops = new String[] {
                // Oilsands 1
                "oilsands@0.0111*1",
                // Lapis - 25% of Diorite
                "sodalite@0.0027*1",
                "calcite@0.0008*1",
                "lapis@0.0014*1",
                "lazurite@0.0035*1",
                // Salt - 10% of Gravel
                "salt@0.0013*3",
                "spodumene@0.0004*3",
                "lepidolite@0.0007*3",
                "rock_salt@0.0017*3",

                // Mineral Sand
                "granitic_mineral_sand@0.0213*2",
                "gypsum@0.0065*2",
                "fullers_earth@0.0110*2",
                "basaltic_mineral_sand@0.0279*2",
                // Garnet Tin
                "garnet_sand@0.0213*2",
                "diatomite@0.0065*2",
                "asbestos@0.011*2",
                "cassiterite_sand@0.0279*2",

                // Sapphire
                "pyrope@0.0161*3",
                "green_sapphire@0.0046*3",
                "sapphire@0.0084*3",
                "almandine@0.0209*3",
                // Garnet
                "garnet_yellow@0.0107*3",
                "opal@0.0031*3",
                "amethyst@0.0056*3",
                "garnet_red@0.0141*3",
                // Copper Tin
                "zeolite@0.0133*3",
                "realgar@0.004*3",
                "cassiterite@0.0069*3",
                "chalcopyrite@0.0174*3",

                // Oilsands 2
                "oilsands@0.0222*4"
        };

        @Config.Comment({ "Gravel Sieve drop list.", "Format: modid:materialName@Chance*MeshLevel",
                "If no \"modid:\" is specified, \"gregtech:\" is assumed.",
                "\"Chance\" is parsed as a \"float\", and \"MeshLevel\" as an \"int\".",
                "Example: gregtech:iron@0.001*1 or iron@0.001*1" })
        public String[] gravelSieveDrops = new String[] {
                // Cassiterite
                "tin@0.0557*1",
                "cassiterite@0.011*1",
                // Coal
                "coal@0.0667*1",
                // Redstone - Redstone only
                "redstone@0.0368*1",

                // Galena
                "galena@0.0247*2",
                "lead@0.0031*2",
                "silver@0.0056*2",
                // Sapphire - 25% of Sand
                "pyrope@0.0004*3",
                "green_sapphire@0.0012*3",
                "sapphire@0.0021*3",
                "almandine@0.052*3",

                // Salt
                "salt@0.0133*3",
                "spodumene@0.0041*3",
                "lepidolite@0.0069*3",
                "rock_salt@0.0174*3",

                // Mica
                "mica@0.0054*4",
                "pollucite@0.0015*4",
                "bauxite@0.0028*4",
                "kyanite@0.0070*4"
        };

        @Config.Comment({ "Granite Sieve drop list.", "Format: modid:materialName@Chance*MeshLevel",
                "If no \"modid:\" is specified, \"gregtech:\" is assumed.",
                "\"Chance\" is parsed as a \"float\", and \"MeshLevel\" as an \"int\".",
                "Example: gregtech:iron@0.001*1 or iron@0.001*1" })
        public String[] graniteSieveDrops = new String[] {
                // Coal
                "coal@0.0667*1",
                // Magnetite
                "magnetite@0.0489*1",
                "gold@0.006*1",
                "vanadium_magnetite@0.0108*1",

                // Redstone
                "redstone@0.0368*2",
                "cinnabar@0.0004*2",
                "ruby@0.0082*2",

                // Olivin
                "magnesite@0.0054*3",
                "glauconite_sand@0.0015*3",
                "olivine@0.0028*3",
                "bentonite@0.0070*3",

                // Manganese
                "spessartine@0.0054*4",
                "tantalite@0.0015*4",
                "pyrolusite@0.0028*4",
                "grossular@0.00704*4"
        };

        @Config.Comment({ "Diorite Sieve drop list.", "Format: modid:materialName@Chance*MeshLevel",
                "If no \"modid:\" is specified, \"gregtech:\" is assumed.",
                "\"Chance\" is parsed as a \"float\", and \"MeshLevel\" as an \"int\".",
                "Example: gregtech:iron@0.001*1 or iron@0.001*1" })
        public String[] dioriteSieveDrops = new String[] {
                // Iron - 10% of Andesite
                "yellow_limonite@0.0032*3",
                "malachite@0.001*3",
                "banded_iron@0.0017*3",
                "brown_limonite@0.0042*3",

                // Lapis
                "sodalite@0.0107*2",
                "calcite@0.0031*2",
                "lapis@0.0056*2",
                "lazurite@0.0140*2",

                // Lubricant
                "talc@0.0107*3",
                "pentlandite@0.0031*3",
                "glauconite_sand@0.0056*3",
                "soapstone@0.014*3",
                // Nickel
                "nickel@0.0107*3",
                "pentlandite@0.0031*3",
                "cobaltite@0.0056*3",
                "garnierite@0.0140*3"
        };

        @Config.Comment({ "Andesite Sieve drop list.", "Format: modid:materialName@Chance*MeshLevel",
                "If no \"modid:\" is specified, \"gregtech:\" is assumed.",
                "\"Chance\" is parsed as a \"float\", and \"MeshLevel\" as an \"int\".",
                "Example: gregtech:iron@0.001*1 or iron@0.001*1" })
        public String[] andesiteSieveDrops = new String[] {
                // Copper
                "iron@0.0213*1",
                "copper@0.0065*1",
                "chalcopyrite@0.0279*1",
                "pyrite@0.011*1",
                // Coal
                "coal@0.0667*1",

                // Diamond
                "graphite@0.0247*2",
                "coal@0.0031*2",
                "diamond@0.0056*2",

                // Iron
                "yellow_limonite@0.0319*3",
                "malachite@0.0098*3",
                "banded_iron@0.0165*3",
                "brown_limonite@0.0418*3",

                // Apatite
                "apatite@0.0247*4",
                "pyrochlore@0.0031*4",
                "tricalcium_phosphate@0.0056*4"
        };

        @Config.Comment({ "Netherrack Sieve drop list.", "Format: modid:materialName@Chance*MeshLevel",
                "If no \"modid:\" is specified, \"gregtech:\" is assumed.",
                "\"Chance\" is parsed as a \"float\", and \"MeshLevel\" as an \"int\".",
                "Example: gregtech:iron@0.001*1 or iron@0.001*1" })
        public String[] netherrackSieveDrops = new String[] {
                // BandedIron
                "yellow_limonite@0.0176*1",
                "gold@0.0054*1",
                "banded_iron@0.0091*1",
                "brown_limonite@0.0203*1",
                // Tetrahedrite
                "tetrahedrite@0.0946*1",
                "stibnite@0.0126*1",
                "copper@0.0212*1",
                // Redstone
                "redstone@0.0811*1",
                "cinnabar@0.00108*1",
                "ruby@0.0182*1",
                // Nether Quartz
                "nether_quartz@0.1324*2",
                "quartzite@0.0144*2",

                // Certus Quartz
                "certus_quartz@0.0359*2",
                "barite@0.0068*2",
                "quartzite@0.0307*2",
                // Sulfur
                "sulfur@0.1352*2",
                "sphalerite@0.018*2",
                "pyrite@0.0303*2",
                // Manganese
                "pyrolusite@0.00118*2",
                "tantalite@0.0034*2",
                "pyrochlore@0.0061*2",
                "grossular@0.0154*2",

                // Beryllium
                "beryllium@0.0407*3",
                "thorium@0.0051*3",
                "emerald@0.0092*3",
                // Topaz
                "topaz@0.0236*3",
                "bornite@0.0068*3",
                "chalcocite@0.0123*3",
                "blue_topaz@0.0307*3",
                // Saltpeter
                "diatomite@0.0236*3",
                "alunite@0.0068*3",
                "electrotine@0.0123*3",
                "saltpeter@0.0307*3",

                // Monazite
                "bastnasite@0.0407*4",
                "neodymium@0.0051*4",
                "monazite@0.0092*4",
                // Molybdenum
                "molybdenite@0.0029*4",
                "powellite@0.0008*4",
                "molybdenum@0.0015*4",
                "wulfenite@0.0038*4"
        };

        @Config.Comment({ "Endstone Sieve drop list.", "Format: modid:materialName@Chance*MeshLevel",
                "If no \"modid:\" is specified, \"gregtech:\" is assumed.",
                "\"Chance\" is parsed as a \"float\", and \"MeshLevel\" as an \"int\".",
                "Example: gregtech:iron@0.001*1 or iron@0.001*1" })
        public String[] endstoneSieveDrops = new String[] {
                // Bauxite
                "ilmenite@0.1103*2",
                "aluminium@0.0447*2",
                "bauxite@0.117*2",
                // Magnetite
                "vanadium_magnetite@0.0633*2",
                "gold@0.0208*2",
                "chromite@0.0325*2",
                "magnetite@0.0834*2",

                // Sheldnite
                "cooperite@0.0213*3",
                "palladium@0.0065*3",
                "platinum@0.011*3",
                "bornite@0.0279*3",
                // Scheelite
                "scheelite@0.0982*3",
                "lithium@0.0131*3",
                "tungstate@0.022*3",

                // Naquadah
                "naquadah@0.1792*4",
                "plutonium@0.0208*4",
                // Pitchblende
                "pitchblende@0.0987*4",
                "uraninite@0.0347*4"
        };
    }
}
