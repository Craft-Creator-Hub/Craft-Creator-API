package fr.en0ri4n.craftcreator.api.ui.screen;

import fr.en0ri4n.craftcreator.api.net.RecipeManagementState;

/**
 * Screen definitions receiving the server's answers to {@link fr.en0ri4n.craftcreator.api.net.RecipeManagementRequest}s.
 */
public interface RecipeManagementListener
{
    void onManagementState(RecipeManagementState state);
}
