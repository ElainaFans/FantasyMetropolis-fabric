package turou.fantasy_metropolis.fabric.state;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import turou.fantasy_metropolis.fabric.client.FantasyMetropolisClient;
import turou.fantasy_metropolis.fabric.state.container.SimpleContainer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import turou.fantasy_metropolis.fabric.state.payload.ContainerUpdatePayload;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class ContainerState extends SavedData {
    private static final String NAME = "fantasy_metropolis_container";
    final HashMap<UUID, SimpleContainer> playerContainers = new HashMap<>();

    public static final Codec<ContainerState> CODEC = Codec.unboundedMap(Codec.STRING, SimpleContainer.CODEC)
            .fieldOf("playerContainers")
            .xmap(
                    map -> {
                        ContainerState state = new ContainerState();
                        map.forEach((key, value) -> state.playerContainers.put(UUID.fromString(key), value));
                        return state;
                    },
                    state -> {
                        Map<String, SimpleContainer> map = new HashMap<>();
                        state.playerContainers.forEach((uuid, container) -> map.put(uuid.toString(), container));
                        return map;
                    })
            .codec();

    public static final SavedDataType<ContainerState> TYPE = new SavedDataType<>(
            NAME,
            ContainerState::new,
            CODEC,
            DataFixTypes.LEVEL);

    public static SimpleContainer getContainer(Player player) {
        var server = player.level().getServer();
        UUID uuid = player.getUUID();
        return server == null ? getClientContainer(uuid) : getServerContainer(server.overworld(), uuid);
    }

    public static SimpleContainer getClientContainer(UUID uuid) {
        HashMap<UUID, SimpleContainer> clientContainers = FantasyMetropolisClient.playerContainers;
        if (!clientContainers.containsKey(uuid))
            clientContainers.put(uuid, new SimpleContainer(1));
        return clientContainers.get(uuid);
    }

    public static ContainerState get(ServerLevel world) {
        return world.getDataStorage().computeIfAbsent(TYPE);
    }

    public static SimpleContainer getServerContainer(ServerLevel world, UUID uuid) {
        return ContainerState.get(world).getContainer(uuid);
    }

    public SimpleContainer getContainer(UUID uuid) {
        if (!playerContainers.containsKey(uuid))
            playerContainers.put(uuid, new SimpleContainer(1));
        return playerContainers.get(uuid);
    }

    public void notifyPlayers(UUID source, SimpleContainer container, ServerLevel world) {
        world.getServer().execute(() -> {
            world.players().forEach((player) -> {
                ServerPlayNetworking.send(player,
                        new ContainerUpdatePayload(source, container.serializeNBT(world.registryAccess())));
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
        if (reallyDirty.get())
            this.setDirty();
    }
}
