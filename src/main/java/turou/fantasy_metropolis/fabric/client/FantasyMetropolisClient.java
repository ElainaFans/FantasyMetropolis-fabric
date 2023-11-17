package turou.fantasy_metropolis.fabric.client;

import dev.felnull.specialmodelloader.api.event.SpecialModelLoaderEvents;
import net.fabricmc.api.ClientModInitializer;
import turou.fantasy_metropolis.fabric.EventHandler;
import turou.fantasy_metropolis.fabric.FantasyMetropolis;

public class FantasyMetropolisClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SpecialModelLoaderEvents.LOAD_SCOPE.register(location -> FantasyMetropolis.MODID.equals(location.getNamespace()));
        EventHandler.registerClientEvents();
    }
}
