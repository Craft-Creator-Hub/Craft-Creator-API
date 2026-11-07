# Recipe creator assets

Each recipe creator block reuses the model of the real block it stands for, adapted to Craft Creator with a crafting
grid on one face. Models are self-contained (parents resolved) in `models/block/<id>.json`; textures are in
`textures/block/<id>/`, so each block can be redrawn without touching the others.

| Blocks | Based on | Craft Creator grid |
| --- | --- | --- |
| `thermal_*` (12 machines) | The Thermal Expansion machine (front, sides, bottom) | Top panel, cells in the machine's color |
| `thermal_tree_extractor` | Thermal Tree Extractor | In the opening of the top |
| `botania_mana_infusion` | Mana Pool | Pool floor, filled with mana |
| `botania_elven_trade` | Alfheim Portal | Gold grid on the top |
| `botania_pure_daisy` | Pure Daisy, standing on a livingrock base | Base top, yellow center |
| `botania_brewery` | Botania renders its brewery in code: rebuilt as a livingrock base, pillar and plate | Base top |
| `botania_petal_apothecary` | Petal Apothecary | Bowl floor, one petal color per cell |
| `botania_runic_altar` | Runic Altar | Top, rune gem in the center |
| `botania_terra_plate` | Terrestrial Agglomeration Plate | Green grid on the top |
| `create_crushing` | Crushing Wheel (OBJ model in `models/block/create_crushing/`) | Wooden grid on the wheel face, axis in the center |
| `create_cutting` | Mechanical Saw | Casing top |
| `minecraft_stonecutting` | Stonecutter (the saw stays the vanilla animated texture) | Top |
| `minecraft_smithing` | Smithing Table (other faces use the vanilla textures) | Top |

The crafting table and furnace creators keep their own original models.

## Licensing

Models and textures are derived from Botania (CurseForge file 3936568, Botania License), Thermal Expansion
(CurseForge file 4101926), Create (CurseForge file 4550986) and Minecraft. Check each license before publishing the
mod, and replace the assets that can't be redistributed.
