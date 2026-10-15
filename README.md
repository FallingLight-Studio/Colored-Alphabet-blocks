# Colored Alphabet Blocks

A Minecraft mod that adds beautiful, fully dyeable A-Z alphabet blocks to the game! decorating building or creating word-guessing games.
offical fork from [Thai Alphabet blocks](https://github.com/AitShiroKu/Thai-alphabet-block)

[![Minecraft Versions](https://img.shields.io/badge/Minecraft-26.2-blue.svg)](#)
[![Fabric](https://img.shields.io/badge/Fabric-Supported-brightgreen.svg)](#)
[![Forge](https://img.shields.io/badge/Forge-Supported-green.svg)](#)
[![NeoForge](https://img.shields.io/badge/NeoForge-Supported-green.svg)](#)
[![License](https://img.shields.io/badge/License-Apache%202.0-yellow.svg)](LICENSE)

---

## ✨ Features

- **27 Placeable Alphabet Blocks**: Includes all English letters (A-Z) and a matching empty wood block.
- **Dual-Layer Dyeing System**:
  - **Background Panel Customization**: Right-click a block with any of the 16 Minecraft Dyes to dye the background wood panel.
  - **Glyph/Letter Customization**: Sneak + Right-click a block with any dye to customize the letter color independently!
- **High-Quality Cutout Rendering**: Crafted using transparent cutout layers, ensuring sharp letter edges, crisp visibility, and integration with the surrounding wood texture.
- **Dedicated Creative Tab**: Comes with an organized custom Creative Tab containing all the letters in alphabetical order for quick access.
- **Multi-Loader Support**: Runs natively on **Fabric**, **Forge**, and **NeoForge** for Minecraft `26.2`.
  
<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/527e8d76-53af-4273-9d52-0a9131026431" />

---

## 🎨 Dyeing Guide

| Action                           | Target                | Description                                                                  |
| :------------------------------- | :-------------------- | :--------------------------------------------------------------------------- |
| **Right-Click with Dye**         | Background Wood Panel | Colors the background panel to any of the 16 vanilla dye colors.             |
| **Shift + Right-Click with Dye** | Glyph / Letter        | Colors the character (letter) itself. _(Does not apply to the Empty Block)._ |

---

## 📥 Installation

1. Make sure you have installed the mod loader of your choice:
   - [Fabric Loader](https://fabricmc.net/)
   - [Forge](https://files.minecraftforge.net/)
   - [NeoForge](https://neoforged.net/)
2. _(Fabric Users)_ Download and install the [Fabric API](https://modrinth.com/mod/fabric-api) for your version of Minecraft.
3. Download the mod `.jar` file matching your Mod Loader and Minecraft version `1.21.11`.
4. Place the downloaded `.jar` file into your `.minecraft/mods/` folder.
5. Boot up the game and look for the **Colored Alphabet Blocks** creative tab!

---

## ⚠️ Upgrading from 1.0.0

2.0.0 renames the mod id from `alphabet_block` to `colored_alphabet_blocks`. Every
block and item id changed with it, so **back up your world before upgrading**.

Old worlds are migrated automatically on all three loaders — placed blocks and
items in chests or inventories keep their background and glyph colors. No
external tool is needed.

| Loader   | Mechanism                                     |
| :------- | :-------------------------------------------- |
| NeoForge | `Registry#addAlias`                           |
| Forge    | `MissingMappingsEvent` remap                  |
| Fabric   | Mixin on `MappedRegistry` lookups             |

Migration happens per chunk, the first time that chunk loads after the upgrade.
A chunk keeps the old names on disk until a player goes there and the game saves
it again, so this migration code stays in the mod permanently rather than being
removed in a later release.

Block and item names themselves did not change — only the namespace:

```
alphabet_block:letter_a    ->  colored_alphabet_blocks:letter_a
alphabet_block:empty_block ->  colored_alphabet_blocks:empty_block
```

Config files under `config/alphabet_block-*.toml` are orphaned by the rename and
can be deleted.

Separately, 1.0.0 shipped its loot tables in a folder Minecraft never reads, so
blocks dropped nothing when broken. 2.0.0 fixes that; letter blocks now drop
themselves.

---

## 📝 License

This project is licensed under the [Apache License 2.0](LICENSE).
