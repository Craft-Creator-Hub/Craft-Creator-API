package fr.en0ri4n.craftcreator.api.item;

/**
 * Adapter that converts between core CoreItemStack and a platform ItemStack object.
 *
 * - Core lives in the API module and must not reference platform classes.
 * - The methods use Object for the platform stack type; platform code should cast accordingly.
 *
 * Platform modules should register an implementation (e.g. ForgeItemStackAdapter) with the
 * platform entrypoint so platform code and screens can use it for conversions.
 */
public interface ItemStackAdapter<T> {

    /**
     * Convert a core item stack to the platform-specific item stack object.
     * Return platform's ItemStack instance (as Object) or platform's empty value if not available.
     */
    T toPlatform(CoreItemStack coreStack);

    /**
     * Convert a platform item stack (passed as Object) into a CoreItemStack.
     * Platform code supplies a platform ItemStack instance (e.g. net.minecraft.world.item.ItemStack).
     */
    CoreItemStack fromPlatform(T platformStack);
}