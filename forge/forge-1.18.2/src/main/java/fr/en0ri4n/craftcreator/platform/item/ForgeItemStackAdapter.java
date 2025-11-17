package fr.en0ri4n.craftcreator.platform.item;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fr.en0ri4n.craftcreator.api.blockentity.CoreItemStack;
import fr.en0ri4n.craftcreator.api.item.ItemStackAdapter;
import fr.en0ri4n.craftcreator.utils.Identifier;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Forge implementation of ItemStackAdapter for Minecraft ItemStack (1.18+).
 * Converts CoreItemStack <-> ItemStack and preserves NBT by translating between
 * JsonObject and CompoundTag recursively.
 */
public final class ForgeItemStackAdapter implements ItemStackAdapter<ItemStack> {

    private static final ForgeItemStackAdapter INSTANCE = new ForgeItemStackAdapter();
    private ForgeItemStackAdapter() {}
    public static ForgeItemStackAdapter get() {
        return INSTANCE;
    }

    @Override
    public ItemStack toPlatform(CoreItemStack coreStack) {
        if (coreStack == null) return ItemStack.EMPTY;

        Identifier id = coreStack.getItemId();
        if (id == null) return ItemStack.EMPTY;
        // treat air/zero as empty
        if ("minecraft".equals(id.getNamespace()) && "air".equals(id.getPath())) return ItemStack.EMPTY;

        ResourceLocation rl = new ResourceLocation(id.getNamespace(), id.getPath());
        Item item = ForgeRegistries.ITEMS.getValue(rl);
        if (item == null) return ItemStack.EMPTY;

        ItemStack stack = new ItemStack(item, Math.max(0, coreStack.getCount()));

        // Apply NBT if present
        if (coreStack.getNbt() != null) {
            CompoundTag tag = jsonToTag(coreStack.getNbt());
            if (tag != null) stack.setTag(tag);
        }

        return stack;
    }

    @Override
    public CoreItemStack fromPlatform(ItemStack platformStack) {
        if (platformStack.isEmpty()) return new CoreItemStack(Identifier.from("minecraft:air"), 0, null);

        Item item = platformStack.getItem();
        ResourceLocation rl = ForgeRegistries.ITEMS.getKey(item);
        if (rl == null) return new CoreItemStack(Identifier.from("minecraft:air"), 0, null);

        Identifier id = Identifier.from(rl.getNamespace(), rl.getPath());
        JsonObject nbtJson = null;
        CompoundTag tag = platformStack.getTag();
        if (tag != null) {
            nbtJson = tagToJson(tag);
        }
        return new CoreItemStack(id, platformStack.getCount(), nbtJson);
    }

    /* ----------------------
     * JSON <-> NBT conversion utilities
     * ---------------------- */

    private CompoundTag jsonToTag(JsonObject obj) {
        if (obj == null) return null;
        CompoundTag tag = new CompoundTag();
        for (String key : obj.keySet()) {
            JsonElement el = obj.get(key);
            putJsonElementInTag(tag, key, el);
        }
        return tag;
    }

    private void putJsonElementInTag(CompoundTag tag, String key, JsonElement el) {
        if (el == null || el.isJsonNull()) {
            // do nothing
        } else if (el.isJsonPrimitive()) {
            if (el.getAsJsonPrimitive().isString()) {
                tag.putString(key, el.getAsString());
            } else if (el.getAsJsonPrimitive().isNumber()) {
                // choose numeric tag based on presence of decimal point (best-effort)
                Number n = el.getAsNumber();
                if (n.doubleValue() == n.longValue()) {
                    // integer -> putLong if big, else int
                    long v = n.longValue();
                    if (v <= Integer.MAX_VALUE && v >= Integer.MIN_VALUE) {
                        tag.putInt(key, (int) v);
                    } else {
                        tag.putLong(key, v);
                    }
                } else {
                    tag.putDouble(key, n.doubleValue());
                }
            } else if (el.getAsJsonPrimitive().isBoolean()) {
                tag.putBoolean(key, el.getAsBoolean());
            } else {
                tag.putString(key, el.getAsString());
            }
        } else if (el.isJsonObject()) {
            tag.put(key, jsonToTag(el.getAsJsonObject()));
        } else if (el.isJsonArray()) {
            JsonArray arr = el.getAsJsonArray();
            // Determine if array is primitive numbers (map to IntArrayTag or ByteArrayTag) or generic -> ListTag of CompoundTag/StringTag/etc.
            boolean allPrimitives = true;
            boolean allInts = true;
            boolean allBytes = true;
            for (JsonElement item : arr) {
                if (!item.isJsonPrimitive()) {
                    allPrimitives = false;
                    break;
                }
                if (!item.getAsJsonPrimitive().isNumber()) {
                    allInts = false;
                    allBytes = false;
                } else {
                    Number num = item.getAsNumber();
                    double d = num.doubleValue();
                    long l = num.longValue();
                    if (d != l) allInts = false;
                    if (l < Byte.MIN_VALUE || l > Byte.MAX_VALUE) allBytes = false;
                }
            }

            if (allPrimitives && allInts && allBytes) {
                // ByteArrayTag
                byte[] bytes = new byte[arr.size()];
                for (int i = 0; i < arr.size(); i++) bytes[i] = (byte) arr.get(i).getAsInt();
                tag.putByteArray(key, bytes);
            } else if (allPrimitives && allInts) {
                int[] ints = new int[arr.size()];
                for (int i = 0; i < arr.size(); i++) ints[i] = arr.get(i).getAsInt();
                tag.putIntArray(key, ints);
            } else {
                // generic list -> ListTag of appropriate types; we will create a ListTag of CompoundTag/StringTag/primitive tags
                ListTag list = new ListTag();
                for (JsonElement item : arr) {
                    if (item.isJsonObject()) {
                        list.add(jsonToTag(item.getAsJsonObject()));
                    } else if (item.isJsonPrimitive()) {
                        if (item.getAsJsonPrimitive().isString()) {
                            list.add(StringTag.valueOf(item.getAsString()));
                        } else if (item.getAsJsonPrimitive().isNumber()) {
                            Number n = item.getAsNumber();
                            if (n.doubleValue() == n.longValue()) {
                                list.add(IntTag.valueOf(n.intValue()));
                            } else {
                                list.add(DoubleTag.valueOf(n.doubleValue()));
                            }
                        } else if (item.getAsJsonPrimitive().isBoolean()) {
                            list.add(ByteTag.valueOf((byte) (item.getAsBoolean() ? 1 : 0)));
                        } else {
                            list.add(StringTag.valueOf(item.getAsString()));
                        }
                    } else {
                        // fallback: empty compound
                        list.add(new CompoundTag());
                    }
                }
                tag.put(key, list);
            }
        }
    }

    private JsonObject tagToJson(CompoundTag tag) {
        JsonObject obj = new JsonObject();
        for (String key : tag.getAllKeys()) {
            Tag child = tag.get(key);
            obj.add(key, tagElementToJson(child));
        }
        return obj;
    }

    private JsonElement tagElementToJson(Tag tag) {
        if (tag == null) return null;
        switch (tag.getId()) {
            case 1: // Byte
                return new JsonPrimitive(((ByteTag) tag).getAsByte() != 0);
            case 2: // Short
                return new JsonPrimitive(((ShortTag) tag).getAsShort());
            case 3: // Int
                return new JsonPrimitive(((IntTag) tag).getAsInt());
            case 4: // Long
                return new JsonPrimitive(((LongTag) tag).getAsLong());
            case 5: // Float
                return new JsonPrimitive(((FloatTag) tag).getAsFloat());
            case 6: // Double
                return new JsonPrimitive(((DoubleTag) tag).getAsDouble());
            case 8: // String
                return new JsonPrimitive(((StringTag) tag).getAsString());
            case 7: { // Byte Array
                byte[] ba = ((ByteArrayTag) tag).getAsByteArray();
                JsonArray arr = new JsonArray();
                for (byte b : ba) arr.add(b);
                return arr;
            }
            case 11: { // Int Array
                int[] ia = ((IntArrayTag) tag).getAsIntArray();
                JsonArray arr = new JsonArray();
                for (int i : ia) arr.add(i);
                return arr;
            }
            case 9: { // List
                ListTag list = (ListTag) tag;
                JsonArray arr = new JsonArray();
                for (int i = 0; i < list.size(); i++) {
                    arr.add(tagElementToJson(list.get(i)));
                }
                return arr;
            }
            case 10: { // Compound
                return tagToJson((CompoundTag) tag);
            }
            default:
                // unsupported/unknown tags -> represent as string
                return new JsonPrimitive(tag.getAsString());
        }
    }
}