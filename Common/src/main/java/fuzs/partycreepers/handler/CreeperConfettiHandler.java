package fuzs.partycreepers.handler;

import com.google.common.collect.ImmutableList;
import fuzs.partycreepers.PartyCreepers;
import fuzs.partycreepers.config.ServerConfig;
import fuzs.partycreepers.init.ModRegistry;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.FastColor;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class CreeperConfettiHandler {
    // just some particle that won't visually show client-side
    private static final BlockParticleOption INVISIBLE_EXPLOSION_PARTICLES = new BlockParticleOption(ParticleTypes.BLOCK,
            Blocks.AIR.defaultBlockState()
    );

    public static void onExplosionDetonate(Level level, Explosion explosion, List<BlockPos> affectedBlocks, List<Entity> affectedEntities) {
        Entity entity = explosion.getDirectSourceEntity();
        if (entity != null && entity.getType().is(ModRegistry.EXPLOSIVE_CREEPERS_ENTITY_TYPE_TAG)) {
            if (!PartyCreepers.CONFIG.get(ServerConfig.class).damageTerrain) explosion.clearToBlow();
            affectedEntities.removeIf(Predicate.not(PartyCreepers.CONFIG.get(ServerConfig.class).damageEntities::filter));
            if (level.getRandom().nextDouble() < PartyCreepers.CONFIG.get(ServerConfig.class).confettiChance) {
                explosion.smallExplosionParticles = explosion.largeExplosionParticles = INVISIBLE_EXPLOSION_PARTICLES;
                ItemStack itemStack = new ItemStack(Items.FIREWORK_ROCKET);
                boolean largeExplosion = entity instanceof Creeper creeper && creeper.isPowered();
                itemStack.set(DataComponents.FIREWORKS, getFireworksComponent(level.getRandom(), largeExplosion));
                // use an actual firework rocket to be compatible with vanilla clients,
                // as otherwise there is no way of triggering firework particles client-side
                FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(level,
                        entity.getX(),
                        entity.getEyeY(),
                        entity.getZ(),
                        itemStack
                );
                level.addFreshEntity(fireworkRocketEntity);
                fireworkRocketEntity.explode();
            }
        }
    }

    private static Fireworks getFireworksComponent(RandomSource randomSource, boolean largeExplosion) {
        List<FireworkExplosion> explosions = new ArrayList<>();
        IntList colors = IntList.of(nextColorArray(randomSource));
        FireworkExplosion.Shape shape = largeExplosion ?
                FireworkExplosion.Shape.LARGE_BALL :
                FireworkExplosion.Shape.SMALL_BALL;
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
        return FastColor.ARGB32.color(0,
                nextColorComponent(randomSource),
                nextColorComponent(randomSource),
                nextColorComponent(randomSource)
        );
    }

    private static int nextColorComponent(RandomSource randomSource) {
        // https://en.wikipedia.org/wiki/68%E2%80%9395%E2%80%9399.7_rule
        return (int) (Mth.clamp(randomSource.nextGaussian() / 6.0 + 0.5, 0.0, 1.0) * 255.0);
    }
}
