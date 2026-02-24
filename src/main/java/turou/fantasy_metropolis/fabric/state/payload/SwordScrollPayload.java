package turou.fantasy_metropolis.fabric.state.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import turou.fantasy_metropolis.fabric.FantasyMetropolis;

public record SwordScrollPayload(int scroll) implements CustomPacketPayload {
    public static final Identifier SCROLL_SWORD_PACKET = Identifier.fromNamespaceAndPath(FantasyMetropolis.MODID,
            "scroll_sword");
    public static final CustomPacketPayload.Type<SwordScrollPayload> TYPE = new CustomPacketPayload.Type<>(
            SCROLL_SWORD_PACKET);
    public static final StreamCodec<RegistryFriendlyByteBuf, SwordScrollPayload> CODEC = StreamCodec
            .composite(ByteBufCodecs.INT, SwordScrollPayload::scroll, SwordScrollPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
