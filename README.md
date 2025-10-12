# Adaptive HUD Brightness

A Better Than Wolves Community Edition addon that dynamically adjusts the brightness of the Heads-Up Display (the user interface elements in normal gameplay, outside of menus).

## Overview

Adaptive HUD Brightness enhances immersion and reduces eye strain by making the HUD respond to the light conditions around the player.
Care has been taken to avoid overstepping and affecting other GUI elements.  If anyone wants menus to dim, that's some whole other rigmarole that I'm not prepared for, personally.

## Features

### Dynamic Brightness

**Light-Aware HUD**:

* The HUD responds to the light level at the player’s eye position.
* Vanilla blocks and ambient light influence HUD brightness, making low-light areas darker and bright areas fully visible.

**Smooth Transitions**:

* Brightness changes are smoothed across frames to reduce abrupt jumps.
* Even in total darkness, a minimum brightness ensures the HUD remains visible.

**Other Notes**

* The effect is limited to the HUD; it does **not** affect world lighting.
* Could help in the video editing process if you need to brighten footage.

---

### Client-Side Only

* All calculations occur on the client.
* No changes to the server, world data, or other players.

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
* Currently has slight incompatibility with BTW-Freelook, but Jeff is in the process of retooling it for BTW proper.
  * The only problem I'm aware of is that the crosshair is prevented from dimming when Freelook is loaded.
  * Freelook is awesome though.  You should try it.

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