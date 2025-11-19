package fr.en0ri4n.craftcreator.platform.net;

import fr.en0ri4n.craftcreator.api.CCReferences;
import fr.en0ri4n.craftcreator.api.net.FetchData;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static final String PROTOCOL = "1";
    public static final ResourceLocation CHANNEL_NAME = new ResourceLocation(CCReferences.MOD_ID, "main");
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            CHANNEL_NAME,
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    private static int id = 0;

    public static void registerPackets() {
        // Register client -> server packet (PacketUiUpdate)
        INSTANCE.registerMessage(id++, UiUpdatePacket.class,
                UiUpdatePacket::encode, UiUpdatePacket::decode, UiUpdatePacket::clientHandle);
        // Register server -> client packet (BlockEntityUpdatePacket)
        INSTANCE.registerMessage(id++, BlockEntityUpdatePacket.class,
                BlockEntityUpdatePacket::encode, BlockEntityUpdatePacket::decode, BlockEntityUpdatePacket::handleServer);
        // Register server -> client packet (FetchDataPacket)
        INSTANCE.registerMessage(id++, FetchDataPacket.class,
                FetchDataPacket::encode, FetchDataPacket::decode, FetchDataPacket::handleServer);
    }

    private NetworkHandler() {}
}