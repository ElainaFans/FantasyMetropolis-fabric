package turou.fantasy_metropolis.fabric;

import net.fabricmc.api.ModInitializer;

public class FantasyMetropolis implements ModInitializer {
	public static final String MODID = "fantasy_metropolis";
	@Override
	public void onInitialize() {
		RegisterHandler.registerItems();
		EventHandler.registerEvents();
		NetworkHandler.registerPackets();
	}
}