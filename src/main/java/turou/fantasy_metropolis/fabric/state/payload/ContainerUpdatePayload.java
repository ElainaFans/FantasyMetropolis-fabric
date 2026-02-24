package turou.fantasy_metropolis.fabric.state.payload;

import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import turou.fantasy_metropolis.fabric.FantasyMetropolis;

import java.util.UUID;

public record ContainerUpdatePayload(UUID uuid, CompoundTag tag) implements CustomPacketPayload {
    public static final Identifier CONTAINER_UPDATE_PACKET = Identifier.fromNamespaceAndPath(FantasyMetropolis.MODID,
            "container_update");
    public static final Type<ContainerUpdatePayload> TYPE = new Type<>(CONTAINER_UPDATE_PACKET);
    public static final StreamCodec<RegistryFriendlyByteBuf, ContainerUpdatePayload> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, ContainerUpdatePayload::uuid,
            ByteBufCodecs.COMPOUND_TAG, ContainerUpdatePayload::tag,
            ContainerUpdatePayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
