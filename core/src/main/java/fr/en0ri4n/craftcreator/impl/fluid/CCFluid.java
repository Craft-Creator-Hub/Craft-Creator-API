package fr.en0ri4n.craftcreator.impl.fluid;

import fr.en0ri4n.craftcreator.utils.HasRegistryName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class CCFluid implements HasRegistryName
{
    private int luminosity;
    private int density;
    private int viscosity;
}
