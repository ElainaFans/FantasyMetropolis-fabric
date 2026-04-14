package turou.fantasy_metropolis.fabric.state.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record OrbitalStrikePayload(BlockPos pos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OrbitalStrikePayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath("fantasy_metropolis", "orbital_strike"));

    public static final StreamCodec<ByteBuf, OrbitalStrikePayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            OrbitalStrikePayload::pos,
            OrbitalStrikePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
