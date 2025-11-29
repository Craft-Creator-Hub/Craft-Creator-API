package fr.en0ri4n.craftcreator.platform.item;

import fr.en0ri4n.craftcreator.api.item.CoreItemStack;
import fr.en0ri4n.craftcreator.api.item.tag.TagProvider;
import fr.en0ri4n.craftcreator.platform.Forge1182Platform;
import fr.en0ri4n.craftcreator.utils.Identifier;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;

import java.util.List;

public class ForgeTagProvider implements TagProvider
{
    @Override
    public List<Identifier> getTags(CoreItemStack coreStack)
    {
        ItemStack mcStack = Forge1182Platform.get().getItemStackAdapter().toPlatform(coreStack);
        return mcStack.getTags()
                .map(TagKey::location)
                .map(rl -> Identifier.from(rl.toString()))
                .toList();
    }

    @Override
    public List<CoreItemStack> getItemsInTag(Identifier tagId)
    {
        TagKey<Item> tagKey = TagKey.create(Registry.ITEM_REGISTRY, Forge1182Platform.get().getIdentifierAdapter().fromCore(tagId));
        ITagManager<Item> itemTagManager = ForgeRegistries.ITEMS.tags();

        if(itemTagManager == null)
            return List.of();

        return itemTagManager.getTag(tagKey).stream().map(item -> Forge1182Platform.get().getItemStackAdapter().fromPlatform(new ItemStack(item))).toList();
    }
}
