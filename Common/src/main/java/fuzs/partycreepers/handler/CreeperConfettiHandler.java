package fuzs.partycreepers.handler;

import fuzs.partycreepers.PartyCreepers;
import fuzs.partycreepers.config.ServerConfig;
import fuzs.partycreepers.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

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
                CompoundTag compoundTag = itemStack.getOrCreateTagElement(FireworkRocketItem.TAG_FIREWORKS);
                boolean largeExplosion = entity instanceof Creeper creeper && creeper.isPowered();
                addFireworkTag(compoundTag, level.getRandom(), largeExplosion);
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

    private static void addFireworkTag(CompoundTag itemTag, RandomSource randomSource, boolean largeExplosion) {
        ListTag listTag = new ListTag();
        itemTag.put(FireworkRocketItem.TAG_EXPLOSIONS, listTag);
        int[] colors = new int[randomSource.nextInt(5) + 4];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = nextColor(randomSource);
        }
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putIntArray(FireworkRocketItem.TAG_EXPLOSION_COLORS, colors);
        compoundTag.putBoolean(FireworkRocketItem.TAG_EXPLOSION_FLICKER, true);
        FireworkRocketItem.Shape shape = largeExplosion ?
                FireworkRocketItem.Shape.LARGE_BALL :
                FireworkRocketItem.Shape.SMALL_BALL;
        compoundTag.putByte(FireworkRocketItem.TAG_EXPLOSION_TYPE, (byte) shape.getId());
        listTag.add(compoundTag);
        compoundTag = new CompoundTag();
        compoundTag.putIntArray(FireworkRocketItem.TAG_EXPLOSION_COLORS, colors);
        compoundTag.putBoolean(FireworkRocketItem.TAG_EXPLOSION_TRAIL, true);
        compoundTag.putByte(FireworkRocketItem.TAG_EXPLOSION_TYPE, (byte) FireworkRocketItem.Shape.BURST.getId());
        listTag.add(compoundTag);
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
