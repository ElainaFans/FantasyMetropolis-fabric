package turou.fantasy_metropolis.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import turou.fantasy_metropolis.fabric.EventHandler;
import turou.fantasy_metropolis.fabric.NetworkHandler;
import turou.fantasy_metropolis.fabric.client.player.WhiterCombatRenderer;
import turou.fantasy_metropolis.fabric.state.container.SimpleContainer;

import java.util.HashMap;
import java.util.UUID;

public class FantasyMetropolisClient implements ClientModInitializer {
    public static final HashMap<UUID, SimpleContainer> playerContainers = new HashMap<>();

    @Override
    public void onInitializeClient() {
        LivingEntityFeatureRendererRegistrationCallback.EVENT
                .register((entityType, entityRenderer, registrationHelper, context) -> {
                    if (entityRenderer instanceof AvatarRenderer<?> avatarRenderer) {
                        registrationHelper.register(new WhiterCombatRenderer(avatarRenderer));
                    }
                });
        EventHandler.registerClientEvents();
        NetworkHandler.registerClientPackets();
    }
}
