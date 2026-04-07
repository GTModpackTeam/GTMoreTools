<p align="center"><img src="https://github.com/GTModpackTeam/GTMoreTools/blob/master/src/main/resources/assets/gregtech/textures/gui/icon/gtmt_logo_high_resolution.png" alt="Logo" width="128" height="128"></p>
<h1 align="center">GTMoreTools</h1>
<h1 align="center">
    <a href="https://www.curseforge.com/minecraft/mc-mods/gtmoretools"><img src="https://img.shields.io/badge/Available%20for-MC%201.12.2%20-informational?style=for-the-badge" alt="Supported Versions"></a>
    <a href="https://github.com/GTModpackTeam/GTMoreTools/blob/master/LICENSE"><img src="https://img.shields.io/github/license/GTModpackTeam/GTMoreTools?style=for-the-badge" alt="License"></a>
    <a href="https://discord.gg/xBwHpZyZdW"><img src="https://img.shields.io/discord/945647524855812176?color=5464ec&label=Discord&style=for-the-badge" alt="Discord"></a>
    <br>
    <a href="https://www.curseforge.com/minecraft/mc-mods/gtmoretools"><img src="https://cf.way2muchnoise.eu/1458022.svg?badge_style=for_the_badge" alt="CurseForge"></a>
    <a href="https://modrinth.com/mod/gtmoretools"><img src="https://img.shields.io/modrinth/dt/gtmoretools?logo=modrinth&label=&suffix=%20&style=for-the-badge&color=2d2d2d&labelColor=5ca424&logoColor=1c1c1c" alt="Modrinth"></a>
    <a href="https://github.com/GTModpackTeam/GTMoreTools/releases"><img src="https://img.shields.io/github/downloads/GTModpackTeam/GTMoreTools/total?sort=semver&logo=github&label=&style=for-the-badge&color=2d2d2d&labelColor=545454&logoColor=FFFFFF" alt="GitHub"></a>
</h1>

## Features
This mod is an add-on for GregTech CEu that provides integration with various mods.

### Chisel Integration
- Adds GT material-based Chisel tools
- Adds the Auto Chisel machine for automated block chiseling

### Better Builder's Wands Integration
- Adds GT material-based Wand tools

### Tinkers' Construct Integration
- CEu tool materials are automatically registered as Tinkers' Construct materials
- Supports Head, Handle, Extra, Bow, and Arrow Shaft stats derived from GT material properties
- Material names are inherited from CEu's localization
- Molten fluid and smeltery casting/melting support for CEu materials with fluid properties
- GT bolt items are registered as TiC part items for Arrow Shaft crafting
- Traits are automatically assigned from GT material properties (no hardcoded material lists):
  - **Holy** (TiC built-in) - Silver-bearing alloys deal bonus damage to undead
  - **Heat Resistant** - Materials processed at blast temp ≥ 2500 K gain +30% mining speed in the Nether
  - **Cryogenic** - Vacuum-freezer processed materials (blast temp 1750–2499 K) apply Slowness on hit
  - **Anti-Corrosion** - High-durability alloys (≥ 2000) have 15% chance to negate durability loss
  - **Heavy Blow** - High-attack materials (≥ 10 dmg) deal increased knockback
  - **Magnetic** (TiC built-in) - Mirrors GT's isMagnetic flag; mined items go directly into inventory
  - **Unbreakable** - Mirrors GT's isUnbreakable flag; tool never loses durability
- GT ToolProperty enchantments (e.g. Fortune, Looting) are automatically applied to TiC parts as traits
- Harvest levels beyond Cobalt are registered dynamically for GT materials with harvest level 5+
- If a GT material shares a name with an existing TiC material (e.g. Flint, Diamond), stats are merged by taking the better value per stat rather than skipping the material


## Credits

- I modified some textures & codes from [GregTech CE: Unofficial](https://www.curseforge.com/minecraft/mc-mods/gregtech-ce-unofficial) under [LGPL-3.0](https://github.com/GregTechCEu/GregTech/blob/main/LICENSE) License
