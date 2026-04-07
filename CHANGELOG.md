# v1.2.1
## Tinkers' Construct Integration
- Traits are automatically assigned from GT material properties (no hardcoded material lists):
    - Holy (TiC built-in, Silver-bearing alloys), Heat Resistant (blast temp ≥ 2500 K), Cryogenic (vacuum-freezer processed, blast temp 1750–2499 K),
      Anti-Corrosion (non-unbreakable, durability ≥ 2000), Heavy Blow (attack ≥ 10), Magnetic (TiC built-in, GT isMagnetic), Unbreakable (GT isUnbreakable)
- GT ToolProperty enchantments are automatically applied to TiC parts as traits.
- Harvest levels beyond Cobalt (GT level 5+) are now registered as distinct TiC harvest levels.
- Arrow Shaft stats are now registered; GT bolt items are supported as TiC part items.
- If a GT material shares a name with an existing TiC material (e.g. Flint, Diamond), stats are merged by taking the better value per stat.
- Custom trait tooltips follow TiC's native display format (italic flavor text + effect description).

* * *

# v1.2.0
## New: Tinkers' Construct Integration
- CEu tool materials are now registered as TiC materials with Head, Handle, Extra, and Bow stats.
- Material names are inherited from CEu's localization.
- Molten fluid and smeltery casting/melting support for CEu materials.

* * *

# v1.1.2
## Better Builder's Wands Integration
- Fix BBW block preview showing on all GT tools.

* * *

# v1.1.1
## Chisel Integration
- Fix aluminum recipe conflicts with Chisel.

* * *

# v1.1.0
- Fix modid.

* * *

# v1.0.0
- First Release.
