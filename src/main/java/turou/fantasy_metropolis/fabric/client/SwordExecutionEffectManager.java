package turou.fantasy_metropolis.fabric.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public final class SwordExecutionEffectManager implements ClientTickEvents.EndTick {
    public static final SwordExecutionEffectManager INSTANCE = new SwordExecutionEffectManager();
    private static final int DEFAULT_DURATION = 40;

    private @Nullable ActiveEffect activeEffect;

    private SwordExecutionEffectManager() {
    }

    public void trigger(BlockPos center, int range, int durationTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            return;
        }

        int totalDuration = Math.max(durationTicks, DEFAULT_DURATION);
        ActiveEffect newEffect = new ActiveEffect(level.dimension(), center.immutable(), Math.max(range, 1), totalDuration);
        this.activeEffect = newEffect;
        this.spawnInitialBurst(level, newEffect);
        this.playInitialSounds(level, newEffect);
    }

    public @Nullable ActiveEffect getActiveEffect() {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null || this.activeEffect == null) {
            return null;
        }
        if (!this.activeEffect.dimension().equals(level.dimension()) || this.activeEffect.isExpired()) {
            this.activeEffect = null;
            return null;
        }
        return this.activeEffect;
    }

    @Override
    public void onEndTick(Minecraft minecraft) {
        ActiveEffect effect = this.getActiveEffect();
        if (effect == null) {
            return;
        }

        ClientLevel level = minecraft.level;
        if (level == null) {
            this.activeEffect = null;
            return;
        }

        effect.tick();
        this.spawnTickParticles(level, effect);
        if (effect.isExpired()) {
            this.activeEffect = null;
        }
    }

    private void playInitialSounds(ClientLevel level, ActiveEffect effect) {
        Vector3f center = effect.centerVec();
        level.playLocalSound(center.x, center.y, center.z, SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS,
                1.0f, 0.5f, false);
        level.playLocalSound(center.x, center.y, center.z, SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS,
                0.8f, 1.2f, false);
    }

    private void spawnInitialBurst(ClientLevel level, ActiveEffect effect) {
        Vector3f center = effect.centerVec();
        for (int i = 0; i < 32; i++) {
            double angle = (Math.PI * 2.0 * i) / 32.0;
            double radius = effect.range() * 0.6;
            double x = center.x + Math.cos(angle) * radius;
            double z = center.z + Math.sin(angle) * radius;
            level.addAlwaysVisibleParticle(ParticleTypes.END_ROD, x, center.y + 0.1, z,
                    0.0, 0.12, 0.0);
        }
        for (int i = 0; i < 20; i++) {
            double offsetX = (level.random.nextDouble() - 0.5) * effect.range();
            double offsetZ = (level.random.nextDouble() - 0.5) * effect.range();
            level.addAlwaysVisibleParticle(ParticleTypes.ELECTRIC_SPARK, center.x + offsetX, center.y + 0.2,
                    center.z + offsetZ, 0.0, 0.25, 0.0);
        }
    }

    private void spawnTickParticles(ClientLevel level, ActiveEffect effect) {
        Vector3f center = effect.centerVec();
        float progress = effect.progress();
        float ringRadius = Mth.lerp(progress, 0.2f, effect.range());
        int samples = Math.max(12, effect.range() * 4);
        for (int i = 0; i < samples; i++) {
            double angle = (Math.PI * 2.0 * i) / samples;
            double x = center.x + Math.cos(angle) * ringRadius;
            double z = center.z + Math.sin(angle) * ringRadius;
            level.addParticle(ParticleTypes.END_ROD, x, center.y + 0.05, z, 0.0, 0.02, 0.0);
        }

        double heightBase = center.y + 24.0;
        for (int i = 0; i < 8; i++) {
            double x = center.x + (level.random.nextDouble() - 0.5) * 0.8;
            double z = center.z + (level.random.nextDouble() - 0.5) * 0.8;
            double y = heightBase + level.random.nextDouble() * 28.0;
            level.addAlwaysVisibleParticle(ParticleTypes.ELECTRIC_SPARK, x, y, z, 0.0, -1.5, 0.0);
        }

        if (effect.age() % 4 == 0) {
            level.addAlwaysVisibleParticle(ParticleTypes.EXPLOSION_EMITTER, center.x, center.y + 0.2, center.z,
                    0.0, 0.0, 0.0);
        }
    }

    public static final class ActiveEffect {
        private final ResourceKey<Level> dimension;
        private final BlockPos center;
        private final int range;
        private final int durationTicks;
        private int age;

        public ActiveEffect(ResourceKey<Level> dimension, BlockPos center, int range, int durationTicks) {
            this.dimension = dimension;
            this.center = center;
            this.range = range;
            this.durationTicks = durationTicks;
            this.age = 0;
        }

        public ResourceKey<Level> dimension() {
            return this.dimension;
        }

        public int range() {
            return this.range;
        }

        public int age() {
            return this.age;
        }

        public void tick() {
            this.age++;
        }

        public boolean isExpired() {
            return this.age >= this.durationTicks;
        }

        public float progress() {
            return Mth.clamp((float) this.age / (float) this.durationTicks, 0.0f, 1.0f);
        }

        public float progress(float partialTick) {
            return Mth.clamp(((float) this.age + partialTick) / (float) this.durationTicks, 0.0f, 1.0f);
        }

        public Vector3f centerVec() {
            return this.center.getCenter().toVector3f();
        }

        public Vec3 centerVec3() {
            return this.center.getCenter();
        }
    }
}
