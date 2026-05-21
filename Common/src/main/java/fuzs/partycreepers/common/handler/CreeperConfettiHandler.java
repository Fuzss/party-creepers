package fuzs.partycreepers.common.handler;

import com.google.common.collect.ImmutableList;
import fuzs.partycreepers.common.PartyCreepers;
import fuzs.partycreepers.common.config.ServerConfig;
import fuzs.partycreepers.common.init.ModRegistry;
import fuzs.puzzleslib.common.api.event.v1.core.EventResult;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;

public class CreeperConfettiHandler {
    /**
     * Just some particle that won't visually show client-side.
     */
    private static final BlockParticleOption INVISIBLE_EXPLOSION_PARTICLES = new BlockParticleOption(ParticleTypes.BLOCK,
            Blocks.AIR.defaultBlockState());

    private static OptionalInt explosionBlockCount = OptionalInt.empty();

    public static void onExplosionDetonate(ServerLevel serverLevel, ServerExplosion explosion, List<BlockPos> affectedBlocks, List<Entity> affectedEntities) {
        Entity entity = explosion.getDirectSourceEntity();
        if (entity != null && entity.is(ModRegistry.EXPLOSIVE_CREEPERS_ENTITY_TYPE_TAG)) {
            if (PartyCreepers.CONFIG.get(ServerConfig.class).dustParticles) {
                explosionBlockCount = OptionalInt.of(affectedBlocks.size());
            } else {
                explosionBlockCount = OptionalInt.of(0);
            }

            if (PartyCreepers.CONFIG.get(ServerConfig.class).preventTerrainDamage) {
                affectedBlocks.clear();
            }

            affectedEntities.removeIf(Predicate.not(PartyCreepers.CONFIG.get(ServerConfig.class).damageEntities));
        }
    }

    /**
     * We copy most of the vanilla method to be able to use our own custom invisible explosion particles.
     *
     * @see ServerLevel#explode(Entity, DamageSource, ExplosionDamageCalculator, double, double, double, float,
     *         boolean, Level.ExplosionInteraction, ParticleOptions, ParticleOptions, WeightedList, Holder)
     */
    public static EventResult onExplosionStart(ServerLevel serverLevel, ServerExplosion explosion) {
        Entity entity = explosion.getDirectSourceEntity();
        if (entity != null && entity.is(ModRegistry.EXPLOSIVE_CREEPERS_ENTITY_TYPE_TAG)) {
            if (serverLevel.getRandom().nextDouble() < PartyCreepers.CONFIG.get(ServerConfig.class).confettiChance) {
                summonFireworkParticles(serverLevel, entity);
            } else {
                return EventResult.PASS;
            }

            int blockCount = explosion.explode();
            for (ServerPlayer serverPlayer : serverLevel.players()) {
                if (serverPlayer.distanceToSqr(explosion.center()) < 4096.0) {
                    Optional<Vec3> playerKnockback = Optional.ofNullable(explosion.getHitPlayers().get(serverPlayer));
                    // The explosion sound event is not captured here, so we just assume the generic sound is used.
                    // In vanilla, only wind charge explosions use a different sound event here, so this should be fine.
                    serverPlayer.connection.send(new ClientboundExplodePacket(explosion.center(),
                            explosion.radius(),
                            explosionBlockCount.orElse(blockCount),
                            playerKnockback,
                            INVISIBLE_EXPLOSION_PARTICLES,
                            SoundEvents.GENERIC_EXPLODE,
                            ServerLevel.DEFAULT_EXPLOSION_BLOCK_PARTICLES));
                }
            }

            explosionBlockCount = OptionalInt.empty();
            return EventResult.INTERRUPT;
        } else {
            return EventResult.PASS;
        }
    }

    /**
     * Does not call {@link FireworkRocketEntity#dealExplosionDamage(ServerLevel)}
     *
     * @see FireworkRocketEntity#explode(ServerLevel)
     */
    private static void summonFireworkParticles(ServerLevel serverLevel, Entity entity) {
        ItemStack itemStack = new ItemStack(Items.FIREWORK_ROCKET);
        boolean largeExplosion = entity instanceof Creeper creeper && creeper.isPowered();
        itemStack.set(DataComponents.FIREWORKS, getFireworksComponent(serverLevel.getRandom(), largeExplosion));
        // Use an actual firework rocket to be compatible with vanilla clients.
        // Otherwise, there is no way of triggering firework particles client-side.
        FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(serverLevel,
                entity.getX(),
                entity.getEyeY(),
                entity.getZ(),
                itemStack);
        serverLevel.addFreshEntity(fireworkRocketEntity);
        serverLevel.broadcastEntityEvent(fireworkRocketEntity, EntityEvent.FIREWORKS_EXPLODE);
        fireworkRocketEntity.gameEvent(GameEvent.EXPLODE, fireworkRocketEntity.getOwner());
        fireworkRocketEntity.discard();
    }

    private static Fireworks getFireworksComponent(RandomSource randomSource, boolean largeExplosion) {
        List<FireworkExplosion> explosions = new ArrayList<>();
        IntList colors = IntList.of(nextColorArray(randomSource));
        FireworkExplosion.Shape shape =
                largeExplosion ? FireworkExplosion.Shape.LARGE_BALL : FireworkExplosion.Shape.SMALL_BALL;
        explosions.add(new FireworkExplosion(shape, colors, IntLists.emptyList(), false, true));
        explosions.add(new FireworkExplosion(FireworkExplosion.Shape.BURST, colors, IntLists.emptyList(), true, false));
        return new Fireworks(0, ImmutableList.copyOf(explosions));
    }

    private static int[] nextColorArray(RandomSource randomSource) {
        int[] colors = new int[randomSource.nextInt(5) + 4];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = nextColor(randomSource);
        }

        return colors;
    }

    private static int nextColor(RandomSource randomSource) {
        return ARGB.color(0,
                nextColorComponent(randomSource),
                nextColorComponent(randomSource),
                nextColorComponent(randomSource));
    }

    private static int nextColorComponent(RandomSource randomSource) {
        // https://en.wikipedia.org/wiki/68%E2%80%9395%E2%80%9399.7_rule
        return (int) (Mth.clamp(randomSource.nextGaussian() / 6.0 + 0.5, 0.0, 1.0) * 255.0);
    }
}
