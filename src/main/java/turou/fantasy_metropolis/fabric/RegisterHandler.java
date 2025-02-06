package turou.fantasy_metropolis.fabric;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import turou.fantasy_metropolis.fabric.item.ItemSwordWhiter;

import java.util.UUID;

public class RegisterHandler {
    public static final Item WHITER_SWORD = new ItemSwordWhiter();
    public static final Block BEDROCK = new Block(BlockBehaviour.Properties.of().strength(1.0F, 3600000.0F));

    public static void registerItems() {
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(FantasyMetropolis.MODID, "whiter_sword"), WHITER_SWORD);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(FantasyMetropolis.MODID, "bedrock"), BEDROCK);
    }

    public static final DataComponentType<Integer> SWORD_RANGE = DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build();
    public static final DataComponentType<UUID> SWORD_OWNER = DataComponentType.<UUID>builder().persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC).build();

    public static void registerDataComponents() {
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(FantasyMetropolis.MODID, "sword_range"), SWORD_RANGE);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(FantasyMetropolis.MODID, "sword_owner"), SWORD_OWNER);
    }
}
