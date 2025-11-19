package fr.en0ri4n.craftcreator.platform.item;

import fr.en0ri4n.craftcreator.api.item.CoreItemStack;
import fr.en0ri4n.craftcreator.api.item.tag.TagProvider;
import fr.en0ri4n.craftcreator.platform.Forge1182Platform;
import fr.en0ri4n.craftcreator.utils.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;

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
}
