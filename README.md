# Adaptive HUD Brightness

A Better Than Wolves Community Edition addon that adjusts the in-game HUD brightness dynamically based on the light level at the player's head.

## Overview

Adaptive HUD Brightness enhances immersion and readability by making the heads-up display (hotbar, health, armor, and other HUD elements) respond to the light conditions the player is experiencing. In dark caves, the HUD dims, and in well-lit areas it brightens, giving a more natural visual feedback for your surroundings while maintaining the classic BTW feel.

## Features

### Dynamic Brightness

**Light-Aware HUD**:

* The HUD responds to the light level where the player’s head is located.
* Blocks and ambient light influence HUD brightness, making low-light areas feel darker and bright areas fully visible.

**Smooth Transitions**:

* Brightness changes are interpolated over time to prevent flickering or abrupt jumps.
* Even in total darkness, a minimum brightness ensures the HUD remains visible.

**Block and Sky Light Considered**:

* Both block light (torches, glowstone, etc.) and sky light are taken into account.
* Gives a realistic balance between underground torchlight and outdoor daylight.

---

### Client-Side Only

* All calculations occur on the client.
* No changes to the server, world data, or other players.
* Fully compatible with existing Better Than Wolves CE worlds.

---

### Optional Extensions (Planned)

* Night vision and torchlight flicker integration.
* Configurable minimum and maximum HUD brightness.
* Support for mod-added light sources in a consistent manner.

---

## Installation

1. Install Better Than Wolves: Community Edition 3.0.0 + Legacy Fabric by following the instructions on the [wiki](https://wiki.btwce.com/view/Main_Page).
2. Download this addon's JAR file from the Releases page.
3. Place the addon JAR file in your `.minecraft/mods` folder.
4. Launch Minecraft. The HUD will now adapt its brightness automatically.

---

## Compatibility

* **Required**: Better Than Wolves CE 3.0.0
* **Mod Loader**: Fabric/Mixin based (Packaged with the BTW Instance)
* Designed to work with the vanilla HUD.
* Should not interfere with other addons, though HUD-altering mods may conflict visually.

---

## License

This project is released under the [BSD Zero-Clause License](LICENSE).
You’re free to use, modify, and share it however you see fit.

---

## Credits

* **Addon author**: Abigail Read
* **Better Than Wolves**: Created by *FlowerChild*, continued by the BTW Community
* Thanks to the **Legacy Fabric team** for keeping classic modding alive.

---

*"Darkness which may be felt."*  – Exodus 10:21 </br><small>[wikiquote](https://en.wikiquote.org/wiki/Darkness)</small>