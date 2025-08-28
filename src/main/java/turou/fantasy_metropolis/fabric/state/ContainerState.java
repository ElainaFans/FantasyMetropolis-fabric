package turou.fantasy_metropolis.fabric.state;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import turou.fantasy_metropolis.fabric.NetworkHandler;
import turou.fantasy_metropolis.fabric.client.FantasyMetropolisClient;
import turou.fantasy_metropolis.fabric.state.container.SimpleContainer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class ContainerState extends SavedData {
    private static final String NAME = "fantasy_metropolis_container";
    private final HashMap<UUID, SimpleContainer> playerContainers = new HashMap<>();

    public static ContainerState fromNbt(CompoundTag tag) {
        ContainerState state = new ContainerState();
        CompoundTag containersTag = tag.getCompound("playerContainers");
        containersTag.getAllKeys().forEach(key -> {
            SimpleContainer container = new SimpleContainer(1);
            container.deserializeNBT(containersTag.getCompound(key));
            state.playerContainers.put(UUID.fromString(key), container);
        });
        return state;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag) {
        CompoundTag containersTag = new CompoundTag();
        playerContainers.forEach((uuid, simpleContainer) -> {
            containersTag.put(uuid.toString(), simpleContainer.serializeNBT());
        });
        compoundTag.put("playerContainers", containersTag);
        return compoundTag;
    }

    public static SimpleContainer getContainer(Player player) {
        MinecraftServer server = player.getServer();
        UUID uuid = player.getUUID();
        return server == null ? getClientContainer(uuid) : getServerContainer(server.overworld(), uuid);
    }

    public static SimpleContainer getClientContainer(UUID uuid) {
        HashMap<UUID, SimpleContainer> clientContainers = FantasyMetropolisClient.playerContainers;
        if (!clientContainers.containsKey(uuid)) clientContainers.put(uuid, new SimpleContainer(1));
        return clientContainers.get(uuid);
    }

    public static ContainerState get(ServerLevel world) {
        var factory = new SavedData.Factory<>(ContainerState::new, ContainerState::fromNbt, DataFixTypes.LEVEL);
        return world.getDataStorage().computeIfAbsent(factory, NAME);
    }

    public static SimpleContainer getServerContainer(ServerLevel world, UUID uuid) {
        return ContainerState.get(world).getContainer(uuid);
    }

    public SimpleContainer getContainer(UUID uuid) {
        if (!playerContainers.containsKey(uuid)) playerContainers.put(uuid, new SimpleContainer(1));
        return playerContainers.get(uuid);
    }

    public void notifyPlayers(UUID source, SimpleContainer container, ServerLevel world) {
        world.getServer().execute(() -> {
            world.players().forEach((player) -> {
                FriendlyByteBuf byteBuf = PacketByteBufs.create();
                byteBuf.writeUUID(source);
                byteBuf.writeNbt(container.serializeNBT());
                ServerPlayNetworking.send(player, NetworkHandler.CONTAINER_UPDATE_PACKET, byteBuf);
            });
        });
    }

    public void refreshDirty(ServerLevel world) {
        AtomicBoolean reallyDirty = new AtomicBoolean(false);
        playerContainers.forEach((uuid, simpleContainer) -> {
            if (simpleContainer.isDirty()) {
                reallyDirty.set(true);
                notifyPlayers(uuid, simpleContainer, world);
                simpleContainer.setDirty(false);
            }
        });
        if (reallyDirty.get()) this.setDirty();
    }
}