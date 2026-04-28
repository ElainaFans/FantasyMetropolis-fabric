package turou.fantasy_metropolis.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FantasyMetropolis implements ModInitializer {
	public static final String MODID = "fantasy_metropolis";

	private static final double STRIKE_RANGE = 500.0;
	private static final int STRIKE_DURATION_TICKS = 1600;

	private static final List<ActiveStrike> activeStrikes = Collections.synchronizedList(new ArrayList<>());
	private static final Map<UUID, Set<ActiveStrike>> playerStrikeMap = new ConcurrentHashMap<>();

	@Override
	public void onInitialize() {
		RegisterHandler.registerItems();
		EventHandler.registerEvents();
		NetworkHandler.registerPackets();

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTickCount() % 20 != 0) return;

			// Remove expired strikes
			activeStrikes.removeIf(strike -> {
				if (server.getTickCount() - strike.startTick > STRIKE_DURATION_TICKS) {
					// Clean up player tracking for this strike
					playerStrikeMap.values().forEach(set -> set.remove(strike));
					return true;
				}
				return false;
			});

			// Check each player's distance to active strikes
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				Set<ActiveStrike> knownStrikes = playerStrikeMap.computeIfAbsent(
						player.getUUID(), k -> ConcurrentHashMap.newKeySet());

				for (ActiveStrike strike : activeStrikes) {
					if (player.level().dimension() != strike.dimension) {
						if (knownStrikes.remove(strike)) {
							sendStopPackets(player);
						}
						continue;
					}

					double distSq = player.distanceToSqr(strike.x, strike.y, strike.z);
					boolean inRange = distSq <= STRIKE_RANGE * STRIKE_RANGE;

					if (inRange && knownStrikes.add(strike)) {
						// Player entered range — send strike packet + sound
						FriendlyByteBuf buf = PacketByteBufs.create();
						buf.writeDouble(strike.x);
						buf.writeDouble(strike.y);
						buf.writeDouble(strike.z);
						ServerPlayNetworking.send(player, NetworkHandler.ORBITAL_STRIKE_PACKET, buf);
						player.playNotifySound(RegisterHandler.ORBITAL_STRIKE_SOUND, SoundSource.PLAYERS, 1.0f, 1.0f);
					} else if (!inRange && knownStrikes.remove(strike)) {
						// Player left range — stop sound + animation
						sendStopPackets(player);
					}
				}
			}
		});
	}

	public static void trackStrike(double x, double y, double z, Level level) {
		int tick = level.getServer() != null ? level.getServer().getTickCount() : 0;
		activeStrikes.add(new ActiveStrike(x, y, z, level.dimension(), tick));
	}

	private static void sendStopPackets(ServerPlayer player) {
		ServerPlayNetworking.send(player, NetworkHandler.STOP_STRIKE_SOUND_PACKET, PacketByteBufs.create());
		ServerPlayNetworking.send(player, NetworkHandler.STOP_STRIKE_ANIMATION_PACKET, PacketByteBufs.create());
	}

	private static class ActiveStrike {
		final double x, y, z;
		final ResourceKey<Level> dimension;
		final int startTick;

		ActiveStrike(double x, double y, double z, ResourceKey<Level> dimension, int startTick) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.dimension = dimension;
			this.startTick = startTick;
		}
	}
}