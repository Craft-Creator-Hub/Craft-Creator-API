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
| `create_milling`, `create_pressing`, `create_mixing`, `create_deploying`, `create_filling`, `create_mechanical_crafting` | Millstone, Mechanical Press, Mechanical Mixer, Deployer, Spout, Mechanical Crafter (item models; parts sticking out of the block are removed or scaled in) | Top of the highest part |
| `create_compacting` | Basin | Basin floor |
| `create_splashing`, `create_haunting` | Encased Fan | Top, blue (water) or cyan (soul fire) cells |
| `create_emptying` | Item Drain | Top |
| `create_sandpaper_polishing`, `create_sequenced_assembly` | Depot | Top, sand or brass cells |
| `create_item_application` | Andesite Casing | Top |

The Create GUIs (`textures/gui/container/create/`) are drawn from each type's slot layout: an andesite plate framed in
brass, recipe slots (fluid slots are blue, outputs have a brass ring) and a light player inventory. Arrows, machines and
titles are drawn by the screen.
| `minecraft_stonecutting` | Stonecutter (the saw stays the vanilla animated texture) | Top |
| `minecraft_smithing` | Smithing Table (other faces use the vanilla textures) | Top |

The crafting table and furnace creators keep their own original models.

## Licensing

Models and textures are derived from Botania (CurseForge file 3936568, Botania License), Thermal Expansion
(CurseForge file 4101926), Create (CurseForge file 4550986) and Minecraft. Check each license before publishing the
mod, and replace the assets that can't be redistributed.
