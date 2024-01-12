package turou.fantasy_metropolis.fabric.client;

import dev.felnull.specialmodelloader.api.event.SpecialModelLoaderEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import turou.fantasy_metropolis.fabric.EventHandler;
import turou.fantasy_metropolis.fabric.FantasyMetropolis;
import turou.fantasy_metropolis.fabric.NetworkHandler;
import turou.fantasy_metropolis.fabric.client.player.WhiterCombatRenderer;
import turou.fantasy_metropolis.fabric.state.container.SimpleContainer;

import java.util.HashMap;
import java.util.UUID;

public class FantasyMetropolisClient implements ClientModInitializer {
    public static final HashMap<UUID, SimpleContainer> playerContainers = new HashMap<>();
    @Override
    public void onInitializeClient() {
        SpecialModelLoaderEvents.LOAD_SCOPE.register(location -> FantasyMetropolis.MODID.equals(location.getNamespace()));
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            if (entityRenderer instanceof PlayerRenderer playerRenderer) {
                registrationHelper.register(new WhiterCombatRenderer(playerRenderer));
            }
        });
        EventHandler.registerClientEvents();
        NetworkHandler.registerClientPackets();
    }
}
