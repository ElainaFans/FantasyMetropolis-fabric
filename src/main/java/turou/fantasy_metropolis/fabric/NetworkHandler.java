package turou.fantasy_metropolis.fabric;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import turou.fantasy_metropolis.fabric.client.FantasyMetropolisClient;
import turou.fantasy_metropolis.fabric.item.ItemSwordWhiter;
import turou.fantasy_metropolis.fabric.state.container.SimpleContainer;
import turou.fantasy_metropolis.fabric.state.payload.ContainerUpdatePayload;
import turou.fantasy_metropolis.fabric.state.payload.SwordScrollPayload;

import java.util.Objects;
import java.util.UUID;

public class NetworkHandler {
    public static void registerPackets() {
        PayloadTypeRegistry.playC2S().register(SwordScrollPayload.TYPE, SwordScrollPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ContainerUpdatePayload.TYPE, ContainerUpdatePayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(SwordScrollPayload.TYPE, (payload, context) -> {
            int scroll = payload.scroll();
            var player = context.player();
            if (player != null) {
                ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
                if (stack.getItem() instanceof ItemSwordWhiter) {
                    CompoundTag tag = stack.getOrCreateTag();
                    int value = tag.getInt("range") + scroll;
                    tag.putInt("range", Math.max(value, 0));
                }
            }
        });
    }

    public static void registerClientPackets() {
        ClientPlayNetworking.registerGlobalReceiver(ContainerUpdatePayload.TYPE, (payload, context) -> {
            UUID playerUUID = payload.uuid();
            SimpleContainer simpleContainer = new SimpleContainer(1);
            simpleContainer.deserializeNBT(Objects.requireNonNull(payload.tag()));
            context.client().execute(() -> {
                FantasyMetropolisClient.playerContainers.merge(playerUUID, simpleContainer, (oldValue, newValue) -> newValue);
            });
        });
    }
}
