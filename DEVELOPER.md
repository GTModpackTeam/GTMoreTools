# GTMoreTools — Developer API

GTMoreTools exposes addon APIs for its mod integrations.
The table below shows current coverage:

| Integration            | API package                      | Status                        |
|------------------------|----------------------------------|-------------------------------|
| Tinkers' Construct     | `integration.tic.api`            | Available                     |
| Better Builder's Wands | —                                | No API yet                    |
| Chisel                 | —                                | No API yet                    |
| Storage Drawers        | `integration.storagedrawers.api` | Available, but for mixin only |
| Ex Nihilo: Creatio     | -                                | No API yet                    |

All registration calls must happen during your mod's `preInit` phase,
**before** GTMT's `registerBlocks`, unless stated otherwise.

---

## Tinkers' Construct Integration

### TraitRegistry

### Composition rule

Triggered when a GT material's direct composition contains a specific material.

```java
// "If a material contains Osmium, give it the Shiny trait on the tool head"
TraitRegistry.registerCompositionTrait(Materials.Osmium, MyTraits.SHINY, "head");
```

### Property rule

Triggered by a predicate on the material and its `ToolProperty`.

```java
// "If GT harvest level ≥ 8, give it the Legendary trait on the tool head"
TraitRegistry.registerPropertyTrait(
    (mat, prop) -> prop.getToolHarvestLevel() >= 8,
    MyTraits.LEGENDARY, "head");
```

Pass `null` as the slot to apply a trait to all part types.

### Referencing GTMT's built-in traits

```java
AbstractTrait heat    = TraitRegistry.getHeatResistantTrait();
AbstractTrait cryo    = TraitRegistry.getCryogenicTrait();
AbstractTrait anti    = TraitRegistry.getAntiCorrosionTrait();
AbstractTrait heavy   = TraitRegistry.getHeavyBlowTrait();
AbstractTrait piercer = TraitRegistry.getPiercerTrait();
AbstractTrait moonlit = TraitRegistry.getMoonlitTrait();
AbstractTrait unbr    = TraitRegistry.getUnbreakableTrait();
AbstractTrait ench    = TraitRegistry.getOrCreateEnchantmentTrait(Enchantments.FORTUNE, 2);
```

---

### HarvestLevels

GTMT auto-assigns names for harvest levels above Cobalt (4) based on the first GT material
seen at that level. Pre-register a name here to override it.

```java
// Must be called before GTMT's registerBlocks
HarvestLevels.register(5, "Vibranium");
```

---

### SmelteryHelper

Register Smeltery melting recipes for custom GT materials.
May be called during `init` or `postInit`.

```java
SmelteryHelper.registerRecipe(
    OrePrefix.ore, MyMaterials.VIBRANIUM,
    MyMaterials.VIBRANIUM.getFluid(),
    slimeknights.tconstruct.library.materials.Material.VALUE_Ingot,
    SmelteryHelper.getDefaultTemperature(MyMaterials.VIBRANIUM));
```

`getDefaultTemperature` returns the same temperature tier GTMT derived from the material's
blast temperature (300 / 600 / 900 / 1200), so your recipe integrates consistently with
GTMT's auto-generated recipes.

---

## Better Builder's Wands Integration

No addon API is currently available. The following describes the internal logic for reference.

### What is added

GT-style Wand tools are registered for every GT material that has the `TOOL` property and
can produce a `plate` (or `gem` for Flint).

### Crafting

| Config | Recipe shape | Materials |
|--------|-------------|-----------|
| Normal | `" I" / " S " / "S  "` | Ingot + Wood Stick |
| `hardToolArmorRecipes = true` | `" fP" / " Sh" / "S  "` | Plate + Wood Stick |

The original BBW recipe is removed when GTMT registers its own.

### Behaviour

- **Block placement limit** — `durability / 16 + 1` blocks per use; 512 for Unbreakable tools.
- **Layout modes** — NORTHSOUTH, EASTWEST, VERTICAL, HORIZONTAL, NOLOCK.
- **Fluid mode** — optionally skip or include fluid blocks.
- **Undo** — placed block positions are saved to NBT and can be undone.
- Durability decreases by the number of blocks placed per use (no loss in Creative or Unbreakable).

### OreDict

Registered as `toolWand`.

---

## Chisel Integration

No addon API is currently available. The following describes the internal logic for reference.

### What is added

1. **GT-style Chisel tools** — registered for every GT material with the `TOOL` property.
2. **Auto Chisel machine** — LV through UV (tiers 1–9), ID range 11001–11008.
3. **Extended block variants** — Bookshelf (6 wood types), Lamps (Project:RED, 16 colours),
   and miscellaneous block variants are added to Chisel carving groups.

### Tool crafting

| Config | Recipe shape | Materials |
|--------|-------------|-----------|
| Normal | `" I" / "S "` | Ingot + Wood Stick |
| `hardToolArmorRecipes = true` | `"fP" / "Sh"` | Plate + Wood Stick |

The original Chisel recipe is removed when GTMT registers its own.
Registered as both `toolChisel` and `craftChisel` (the latter allows vanilla Chisel to accept it).

### Auto Chisel recipe

```
B S B
T H T
M C M
```

- **B** = `toolHeadBuzzSaw` (Invar)
- **S** = Sensor, **H** = Hull, **M** = Motor, **C** = Circuit (all tier-scaled)
- **T** = `craftChisel` (OreDict — accepts any registered Chisel tool)

### Auto Chisel processing

All block-to-block conversion recipes are auto-generated from Chisel's Carving API at startup:
each block within a group can be converted to any other block in the same group (10 EU/t, ULV, 10 t).
Parallel count scales as `4^(tier − EV) / 2` from EV upward.

### Bookshelf variants (Assembler)

6 wood types × 1 recipe each: 6 planks + 3 Books → carved Bookshelf (100 EU, ULV).

---

## Storage Drawers Integration
An addon API currently exists, but it is mixin-based and not usable. The following describes the internal logic for reference.

### What is added

- Storage Upgrades made from GT materials will be registered for the GT materials specified in the cfg.

### Config Options

`removeOriginal`
- Controls whether the default Storage Upgrade recipes are removed.
    - true: All original Storage Upgrade recipes are removed. Only recipes defined by this mod (via upgradeMaterials) will be available.
    - false: Original recipes remain alongside the newly added ones.
- Use this if you want to fully replace the default progression with GT-based materials.

`upgradeMaterials`
- Defines custom materials used to craft Storage Upgrades.
- Each entry adds a new Storage Upgrade variant based on a GT material.

#### _Format_
`modId:materialName@multiplier%tier`

#### _Parameters_
- `materialName`: Must be a valid GT material with a dust property.
- `multiplier`: Determines the storage capacity multiplier of the upgrade. 
  - Range: 1 to 2147483647
  - Not affected by the StorageDrawers config.
- `tier` (optional): Required Field Generator tier. 
  - Range: 1 (LV) to 8 (UV)
  - If omitted, defaults to -1 (no Field Generator required). 
  
#### _Behavior_
- A Storage Upgrade using the specified material will be registered for each entry. 
- If the config is empty, only the original materials are available. 
- Original materials (Obsidian / Iron / Gold / Diamond / Emerald):
  - Always included automatically.
  - Use tier -1.
  - Their multipliers are controlled by the StorageDrawers config.
  - **Do NOT define them here.**
  
#### _Example_
`gregtech:steel@4%2`
- Adds a Steel Storage Upgrade 
  - Multiplier: x4 
  - Requires MV-tier Field Generator
  
#### _Logging_
- Registration results can be checked in the log. 
- The process starts with `[GT More Tools]: UpgradeMaterialData registration started.` and ends with `[GT More Tools]: UpgradeMaterialData registration finished.`
- On successful registration, the following message is logged: `[GT More Tools]: Registered UpgradeMaterial (Material=..., meta=..., multiplier=x..., requiredTier=...)`
  - For example, a successful registration will produce a log like: `[GT More Tools]: Registered UpgradeMaterial (Material=steel, meta=324, multiplier=x4, requiredTier=2 (MV))`
- Entries with an invalid format will be skipped, and the log will indicate the issue.

### Crafting

```
P S P
S U S
P X P
```

- **P** = `plate`
- **S** = `screw`
- **U** = Upgrade Template
- **X** = If _tier_ is -1, a `stickLong`; otherwise, a `Field Generator` of the specified tier

- _Storage Upgrade_ conversion recipes between StorageDrawers and GTMoreTools are always added for Obsidian / Iron / Gold / Diamond / Emerald.

---

## Ex Nihilo: Creatio Integration

No addon API is currently available. The following describes the internal logic for reference.

### What is added

1. **GT-style Crooks** — registered for every GT material with the `TOOL` property.
2. **Hammer Conversion** - CEu Hard hammers can now be used as Ex Nihilo hammers.
3. **Sieve Compatibility** — added Steam and Electric Sieves, GT pebbles and GT Ore chunk.

### Tool crafting

| Config | Recipe shape         | Materials    |
|--------|----------------------|--------------|
| Always | `"SS" / " S" / " S"` | Rod (or Gem) |

The original Wood, Iron, Gold, Diamond Crook recipe is removed when `replaceCrook = true`.
Registered as `toolCrook`.

### Electric Sieve Recipe

```
C P C
V H V
W S W
```

- **C** = Circuit (all tier-scaled), **P** = Piston
- **V** = Conveyer, **H** = Hull, **W** = `cableGTSingle` (LV = Tin, MV = Copper, ...)
- **S** = Sieve

### Sieving Compatibility

:warning:**_WARNING_**:warning: This feature is temporary and will be removed once GTCEu supports it natively.
- Adds GT stone Pebbles and Rubber Saplings to the Dirt Sieve drop table. 
- Allows GT Materials to be added to the Sieve drop tables for Sand, Gravel, Andesite, Granite, Diorite, Netherrack, and Endstone via the config.
  - Format: `modid:materialName@chance*meshLevel` (`modid` is optional and defaults to `gregtech`.)
  - When `outputVeinProbabilities` is set to `true`, all worldgen vein definitions under `config/gregtech/worldgen/vein/` are scanned, and the generation probability of each ore is written to the log.
> **Note:** GTMT only adds new entries and does not modify the default Sieve drop tables.

---
