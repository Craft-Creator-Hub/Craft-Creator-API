package fr.en0ri4n.craftcreator.impl.item;

import fr.en0ri4n.craftcreator.utils.HasRegistryName;
import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an item in the Craft Creator API.
 */
public interface CCItem extends HasRegistryName
{
    int getMaxStackSize();
}
