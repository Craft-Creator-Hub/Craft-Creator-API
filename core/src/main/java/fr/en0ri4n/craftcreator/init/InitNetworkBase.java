package fr.en0ri4n.craftcreator.init;

import fr.en0ri4n.craftcreator.utils.Identifier;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class InitNetworkBase
{
    protected static final String PROTOCOL_VERSION = Integer.toString(1);
    private final Identifier networkId;
    private static int packetId;

    public void init()
    {
        registerPackets();
    }

    public abstract void registerPackets();
}
