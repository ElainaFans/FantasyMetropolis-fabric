package turou.fantasy_metropolis.fabric.state.payload;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import turou.fantasy_metropolis.fabric.FantasyMetropolis;

public record SwordExecutionEffectPayload(BlockPos center, int range, int durationTicks) implements CustomPacketPayload {
    public static final Identifier EXECUTION_EFFECT_PACKET = Identifier.fromNamespaceAndPath(FantasyMetropolis.MODID,
            "sword_execution_effect");
    public static final Type<SwordExecutionEffectPayload> TYPE = new Type<>(EXECUTION_EFFECT_PACKET);
    public static final StreamCodec<RegistryFriendlyByteBuf, SwordExecutionEffectPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SwordExecutionEffectPayload::center,
            ByteBufCodecs.INT, SwordExecutionEffectPayload::range,
            ByteBufCodecs.INT, SwordExecutionEffectPayload::durationTicks,
            SwordExecutionEffectPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
