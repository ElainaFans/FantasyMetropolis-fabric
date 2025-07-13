package turou.fantasy_metropolis.fabric.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import turou.fantasy_metropolis.fabric.mixin.ILivingEntityMixin;

public class DamageUtil {
    public static void punishPlayer(Player player) {
        if (!PlayerUtil.hasSword(player)) {
            player.getInventory().dropAll();
            player.getEnderChestInventory().clearContent();
        }
    }

    public static void killLivingEntity(LivingEntity entity) {
        if(!entity.isAlive()) return;
        if (entity instanceof Player player) DamageUtil.punishPlayer(player);

        //make it no invulnerable
        entity.setInvulnerable(false);
        entity.setRemainingFireTicks(Integer.MAX_VALUE);
        entity.setSharedFlagOnFire(true);

        if(!entity.isAlive()) return;

        entity.removeVehicle();
        entity.removeAllEffects();

        if(!entity.isAlive()) return;

        //remove it
        entity.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK);
        if(!entity.isAlive()) return;
        entity.remove(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
        if(!entity.isAlive()) return;
        entity.remove(Entity.RemovalReason.CHANGED_DIMENSION);
        if(!entity.isAlive()) return;
        entity.remove(Entity.RemovalReason.DISCARDED);
        if(!entity.isAlive()) return;
        entity.remove(Entity.RemovalReason.KILLED);
        if(!entity.isAlive() && !entity.isRemoved()) return;

        //kill it
        entity.kill();
        if(!entity.isAlive()) return;
        entity.die(entity.damageSources().sonicBoom(entity));
        if(!entity.isAlive()) return;
        ((ILivingEntityMixin) entity).setDead(true);
        if(!entity.isAlive()) return;
        entity.deathTime = 20;
        entity.hurtTime = 20;

        ((ILivingEntityMixin) entity).setDead(true);
        entity.noPhysics = true;
    }

    public static void killWildcardEntity(Entity entity) {
        if(!entity.isAlive()) return;

        //make it no invulnerable
        entity.setInvulnerable(false);
        entity.setRemainingFireTicks(Integer.MAX_VALUE);
        entity.setSharedFlagOnFire(true);
        entity.removeVehicle();
        if(!entity.isAlive()) return;

        //remove it
        entity.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK);
        if(!entity.isAlive()) return;
        entity.remove(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
        if(!entity.isAlive()) return;
        entity.remove(Entity.RemovalReason.CHANGED_DIMENSION);
        if(!entity.isAlive()) return;
        entity.remove(Entity.RemovalReason.KILLED);
        if(!entity.isAlive()) return;
        entity.remove(Entity.RemovalReason.DISCARDED);
        if(!entity.isAlive() && !entity.isRemoved()) return;

        entity.kill();
        if(!entity.isAlive()) return;

        entity.noPhysics = true;
    }

    public static void hurtRange(int range, Player player, Level level, boolean lightning) {
        level.getEntitiesOfClass(Entity.class,new AABB(player.getOnPos()).inflate(range)).forEach(e -> {
            if(e != player){
                if(e instanceof LivingEntity livingEntity){
                    e.hurt(e.damageSources().sonicBoom(e),Float.MAX_VALUE);
                    livingEntity.setHealth(0.0f);
                    DamageUtil.killLivingEntity(livingEntity);
                    livingEntity.setHealth(0.0f);
                } else {
                    e.hurt(e.damageSources().sonicBoom(e),Float.MAX_VALUE);
                    DamageUtil.killWildcardEntity(e);
                }
                if (lightning) {
                    LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
                    lightningBolt.setVisualOnly(true);
                    lightningBolt.setPos(e.getPosition(0));
                    level.addFreshEntity(lightningBolt);
                }
            }
        });
    }
}
