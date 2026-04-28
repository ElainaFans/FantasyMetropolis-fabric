package turou.fantasy_metropolis.fabric;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import turou.fantasy_metropolis.fabric.item.ItemSwordWhiter;

public class RegisterHandler {
    public static final Item WHITER_SWORD = new ItemSwordWhiter();
    public static final Block BEDROCK = new Block(BlockBehaviour.Properties.of().strength(1.0F, 3600000.0F));

    public static final ResourceLocation ORBITAL_STRIKE_SOUND_ID = new ResourceLocation(FantasyMetropolis.MODID, "orbital_strike");
    public static final SoundEvent ORBITAL_STRIKE_SOUND = SoundEvent.createVariableRangeEvent(ORBITAL_STRIKE_SOUND_ID);

    public static void registerItems() {
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(FantasyMetropolis.MODID, "whiter_sword"), WHITER_SWORD);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(FantasyMetropolis.MODID, "bedrock"), BEDROCK);
        Registry.register(BuiltInRegistries.SOUND_EVENT, ORBITAL_STRIKE_SOUND_ID, ORBITAL_STRIKE_SOUND);
    }
}
