package fr.en0ri4n.craftcreator.platform.item;

import fr.en0ri4n.craftcreator.api.item.CoreItemStack;
import fr.en0ri4n.craftcreator.api.item.tag.TagProvider;
import fr.en0ri4n.craftcreator.platform.NeoForge1206Platform;
import fr.en0ri4n.craftcreator.utils.Identifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.List;

public class ForgeTagProvider implements TagProvider
{
    @Override
    public List<Identifier> getTags(CoreItemStack coreStack)
    {
        ItemStack mcStack = NeoForge1206Platform.get().getItemStackAdapter().toPlatform(coreStack);
        return mcStack.getTags()
                .map(TagKey::location)
                .map(rl -> Identifier.from(rl.toString()))
                .toList();
    }

    @Override
    public List<CoreItemStack> getItemsInTag(Identifier tagId)
    {
        TagKey<Item> tagKey = TagKey.create(Registries.ITEM, NeoForge1206Platform.get().getIdentifierAdapter().fromCore(tagId));
        return BuiltInRegistries.ITEM.getTag(tagKey)
                .map(set -> set.stream().map(holder -> NeoForge1206Platform.get().getItemStackAdapter().fromPlatform(new ItemStack(holder.value()))).toList())
                .orElse(List.of());
    }
}
