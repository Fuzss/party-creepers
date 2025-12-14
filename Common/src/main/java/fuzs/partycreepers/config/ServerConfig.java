package fuzs.partycreepers.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ServerConfig implements ConfigCore {
    @Config(description = "Disable damage to surrounding blocks when a creeper explodes.")
    public boolean preventTerrainDamage = true;
    @Config(description = "Kind of entities to damage when a creeper explodes. Living does exclude e.g. items laying on the ground.")
    public EntityDamage damageEntities = EntityDamage.LIVING_ONLY;
    @Config(description = {
            "Chance a creeper will burst into fireworks, otherwise vanilla explosion particles show.",
            "Other changes to explosion behavior still apply such as no damage to terrain."
    })
    @Config.DoubleRange(min = 0.0, max = 1.0)
    public double confettiChance = 1.0;
    @Config(description = "Always spawn dust and smoke particles from normal explosions, also together with confetti.")
    public boolean dustParticles = true;

    public enum EntityDamage {
        NONE {
            @Override
            public boolean appliesTo(Entity entity) {
                return false;
            }
        },
        LIVING_ONLY {
            @Override
            public boolean appliesTo(Entity entity) {
                return entity instanceof LivingEntity;
            }
        },
        PLAYERS_ONLY {
            @Override
            public boolean appliesTo(Entity entity) {
                return entity instanceof Player;
            }
        },
        ALL {
            @Override
            public boolean appliesTo(Entity entity) {
                return true;
            }
        };

        public abstract boolean appliesTo(Entity entity);
    }
}
