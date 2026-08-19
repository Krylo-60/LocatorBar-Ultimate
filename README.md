<div align="center">
  <img src="assets/banner.png" alt="LocatorBar: The Ultimate Edition" width="100%" />

  # LocatorBar — The Ultimate Edition 🌟

  <p><strong>The ultimate RPG & MMO-style HUD compass and player/waypoint locator navigation system for Minecraft!</strong></p>

  <p>
    <a href="https://modrinth.com/mod/locatorbar-ultimate"><img src="https://img.shields.io/badge/Modrinth-Download-00AF5C?style=for-the-badge&logo=modrinth" alt="Modrinth" /></a>
    <a href="https://www.curseforge.com/projects/1658929"><img src="https://img.shields.io/badge/CurseForge-Download-F16436?style=for-the-badge&logo=curseforge" alt="CurseForge" /></a>
    <a href="https://github.com/Krylo-60/LocatorBar-Ultimate"><img src="https://img.shields.io/badge/GitHub-Repository-181717?style=for-the-badge&logo=github" alt="GitHub" /></a>
    <img src="https://img.shields.io/badge/Minecraft-1.20%20to%2026.2-orange?style=for-the-badge" alt="Minecraft Versions" />
    <img src="https://img.shields.io/badge/License-GPL--3.0-blue?style=for-the-badge" alt="License" />
  </p>
</div>

---

## ⚡ What makes the Ultimate Edition special?

**LocatorBar: The Ultimate Edition** modernizes vanilla Minecraft navigation by adding a sleek, customizable navigation compass bar directly to your in-game HUD. Whether exploring solo, venturing through massive multiplayer servers, or raiding with friends, LocatorBar keeps you oriented with zero screen clutter.

---

## 🌟 Key Features

### 🧭 Dynamic Degrees & Precision Navigation
* **Smooth Degree Numbers**: Continuous degree readouts (0° to 360°) with dynamic interval ticks ($15^\circ$ & $30^\circ$).
* **Full Cardinal & Intercardinal Markers**: `N (0°)`, `NE (45°)`, `E (90°)`, `SE (135°)`, `S (180°)`, `SW (225°)`, `W (270°)`, `NW (315°)`.

### 🎯 Off-Screen Edge Indicators & Target Lock Glow
* **Off-Screen Carets (`◄` / `►`)**: Never lose track of your waypoints or teammates. Glowing arrow indicators on the left and right edges show you which way to rotate your camera.
* **Target Lock Glow**: When aiming directly toward an active tracked waypoint ($\pm 2.5^\circ$), a subtle pulse glow confirms you are locked on target.

### 📍 In-Game Waypoint Creator (`B` Keybind)
* Press **`B`** at any moment in-game to pop open the **Instant Waypoint Creator**!
* Choose custom names, 12 built-in icons (`★`, `⌂`, `☠`, `💎`, `⚔`, `🏰`, `🌲`, `📍`, `🚩`, `⛏`, `❤️`, `⚡`), and custom color palettes.
* Saved instantly to your config without needing lodestone compasses.

### 🌈 8+ Visual Themes & Chroma RGB Wave
* **Chroma RGB Wave**: Smooth animated rainbow wave flowing across your compass bar.
* **Cyberpunk Neon**: High-contrast glowing hot pink and electric cyan.
* **Sunset Twilight**: Warm twilight orange-to-purple gradient.
* **OLED Minimal**: Pure floating markers with zero background clutter for competitive PvP.
* **Classic & Metallic**: *Frosted Glass, Neon Cyan, Amethyst, Emerald, Gold, and Nether Crimson*.

### 🔊 Audio Sound Packs & Smooth Fade Animations
* Customizable sound effects: *Modern Chime, Sci-Fi Blip, Mechanical Click, Level Up Ding, or Mute*.
* Smooth animated fade-in and fade-out transitions when toggling the bar.

### 🖱️ Drag-and-Drop HUD Position Editor
* Move the locator bar to any position on your screen with live visual preview and grid snapping.

---

## 📦 Compatibility & Supported Platforms

| Platform | Type | Supported Versions | Notes |
| :--- | :--- | :--- | :--- |
| 🧵 **Fabric** | Client & Server | `1.20` – `26.2` | Native Fabric API support |
| 🪡 **Quilt** | Client & Server | `1.20` – `26.2` | 100% compatible via Quilt loader |
| 🔨 **NeoForge** | Client & Server | `1.21` – `1.21.4` | Native NeoForge event system |
| ⚒️ **Forge** | Client & Server | `1.20.1` – `1.21.4` | Full Forge loader compatibility |
| 📜 **Paper / Spigot** | Server Plugin | `1.20` – `26.2+` | Standalone server plugin with Vanish support |
| ⚡ **Purpur / Folia** | Server Plugin | `1.20` – `26.2+` | Multi-threaded Folia & Purpur compatible |

---

## 📥 Installation

### 🎮 For Players (Client-side):
1. Make sure you have **Fabric Loader**, **NeoForge**, or **Forge** installed.
2. Download the appropriate `.jar` file from [Modrinth](https://modrinth.com/mod/locatorbar-ultimate) or [CurseForge](https://www.curseforge.com/projects/1658929).
3. Place the downloaded `.jar` into your `.minecraft/mods` directory.
4. Launch Minecraft and press **`K`** to toggle your new locator bar!

### 🖥️ For Server Admins (Server-side):
1. Download **`LocatorBar-Paper-1.2.3.jar`**.
2. Place it into your server's `plugins/` directory.
3. Restart your server. The plugin will automatically broadcast player locations to connected LocatorBar clients while respecting all vanish/stealth plugins (`EssentialsX`, `SuperVanish`, `PremiumVanish`, `CMI`).

---

## ⌨️ Default Controls

| Keybind | Action | Description |
| :--- | :--- | :--- |
| **`K`** *(or `Numpad 0`)* | **Toggle HUD** | Instantly show or hide the LocatorBar |
| **`B`** | **Create Waypoint** | Open the interactive Waypoint Creator modal |

*(All keybinds can be reconfigured anytime in `Options -> Controls -> Key Binds -> Locator Bar`)*

---

## 📜 Authors, Attribution & License

```text
LocatorBar: The Ultimate Edition
Original Mod Copyright (C) 2026 FuzjaJadrowa (https://github.com/FuzjaJadrowa/LocatorBar)
Enhanced Ultimate Edition Copyright (C) 2026 Krylo_plays (https://github.com/Krylo-60)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details: https://www.gnu.org/licenses/gpl-3.0.html
```

* **Original Creator**: [FuzjaJadrowa](https://github.com/FuzjaJadrowa/LocatorBar)
* **Enhanced & Maintained by**: [Krylo_plays](https://github.com/Krylo-60)
* **License**: [GNU General Public License v3.0 (GPL-3.0)](https://www.gnu.org/licenses/gpl-3.0.html)
