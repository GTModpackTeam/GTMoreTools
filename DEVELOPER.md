# GTMoreTools — Developer API

GTMoreTools exposes addon APIs for its mod integrations.
The table below shows current coverage:

| Integration | API package | Status |
|---|---|---|
| Tinkers' Construct | `integration.tic.api` | Available |
| Better Builder's Wands | — | No API yet |
| Chisel | — | No API yet |

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
