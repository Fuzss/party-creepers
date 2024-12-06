package fuzs.partycreepers.handler;

import com.google.common.collect.ImmutableList;
import fuzs.partycreepers.PartyCreepers;
import fuzs.partycreepers.config.ServerConfig;
import fuzs.partycreepers.init.ModRegistry;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class CreeperConfettiHandler {
    // just some particle that won't visually show client-side
    private static final BlockParticleOption INVISIBLE_EXPLOSION_PARTICLES = new BlockParticleOption(ParticleTypes.BLOCK,
            Blocks.AIR.defaultBlockState());

    public static EventResult onExplosionStart(ServerLevel serverLevel, ServerExplosion explosion) {
        if (isCreeperExplosion(explosion)) {
            explosion.explode();
            for (ServerPlayer serverPlayer : serverLevel.players()) {
                if (serverPlayer.distanceToSqr(explosion.center()) < 4096.0) {
                    Optional<Vec3> optional = Optional.ofNullable(explosion.getHitPlayers().get(serverPlayer));
                    // explosion sound event is not captured by the event, so we just assume the generic explosion sound is used
                    // in vanilla only wind charge explosions use a different sound event here, so this should be fine
                    serverPlayer.connection.send(new ClientboundExplodePacket(explosion.center(),
                            optional,
                            INVISIBLE_EXPLOSION_PARTICLES,
                            SoundEvents.GENERIC_EXPLODE));
                }
            }
            return EventResult.INTERRUPT;
        } else {
            return EventResult.PASS;
        }
    }

    public static void onExplosionDetonate(ServerLevel serverLevel, ServerExplosion explosion, List<BlockPos> affectedBlocks, List<Entity> affectedEntities) {
        Entity entity = explosion.getDirectSourceEntity();
        if (entity != null && entity.getType().is(ModRegistry.EXPLOSIVE_CREEPERS_ENTITY_TYPE_TAG)) {
            if (!PartyCreepers.CONFIG.get(ServerConfig.class).damageTerrain) {
                affectedBlocks.clear();
            }
            affectedEntities.removeIf(Predicate.not(PartyCreepers.CONFIG.get(ServerConfig.class).damageEntities::filter));
            if (serverLevel.getRandom().nextDouble() < PartyCreepers.CONFIG.get(ServerConfig.class).confettiChance) {
                ItemStack itemStack = new ItemStack(Items.FIREWORK_ROCKET);
                boolean largeExplosion = entity instanceof Creeper creeper && creeper.isPowered();
                itemStack.set(DataComponents.FIREWORKS, getFireworksComponent(serverLevel.getRandom(), largeExplosion));
                // use an actual firework rocket to be compatible with vanilla clients,
                // as otherwise there is no way of triggering firework particles client-side
                FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(serverLevel,
                        entity.getX(),
                        entity.getEyeY(),
                        entity.getZ(),
                        itemStack);
                serverLevel.addFreshEntity(fireworkRocketEntity);
                fireworkRocketEntity.explode(serverLevel);
            }
        }
    }

    private static boolean isCreeperExplosion(Explosion explosion) {
        Entity entity = explosion.getDirectSourceEntity();
        return entity != null && entity.getType().is(ModRegistry.EXPLOSIVE_CREEPERS_ENTITY_TYPE_TAG);
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
