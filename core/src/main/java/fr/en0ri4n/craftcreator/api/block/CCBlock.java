package fr.en0ri4n.craftcreator.api.block;

import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CCBlock {

    private final Identifier registryName;

    @Override
    public String toString() {
        return registryName.toString();
    }
}