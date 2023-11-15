package turou.fantasy_metropolis.fabric.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class DamageUtil {
    private static AABB growAABB(AABB aabb, int range) {
        return aabb.expandTowards(range, range, range).expandTowards(-range, -range, -range);
    }

    public static void punishPlayer(Player player) {
        player.getInventory().dropAll();
        player.getEnderChestInventory().clearContent();
    }

    // black magic included for anti entity and player punish
    public static void killEntityLiving(LivingEntity entity) {
        var level = entity.level();
        if (!(level.isClientSide || entity.isDeadOrDying() || entity.getHealth() == 0.0F)) {
            var damageSource = level.damageSources().magic();
            entity.getCombatTracker().recordDamage(damageSource, Float.MAX_VALUE);
            entity.setHealth(0.0F);
            entity.remove(Entity.RemovalReason.KILLED);
            if (entity instanceof Player player) {
                punishPlayer(player);
            }
        }
    }

    public static void hurtRange(int range, Player player) {
        AABB rangeAABB = growAABB(new AABB(player.blockPosition()), range);
        List<Entity> entities = player.level().getEntitiesOfClass(Entity.class, rangeAABB);
        for (Entity entity : entities) {
            if (entity.getUUID().equals(player.getUUID())) continue; // You shouldn't attack yourself
            if (entity instanceof LivingEntity) {
                killEntityLiving((LivingEntity) entity);
            } else {
                // If entity can't be damaged, we remove it(NPCMod, wither, items, item frame...)
                entity.remove(Entity.RemovalReason.KILLED);
            }
        }
    }
}
