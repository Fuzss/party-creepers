package fuzs.partycreepers.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ServerConfig implements ConfigCore {
    @Config(description = "Damage surrounding blocks when a creeper explodes.")
    public boolean damageTerrain = false;
    @Config(description = "Kind of entities to damage when a creeper explodes. Living does exclude e.g. items laying on the ground.")
    public EntityDamage damageEntities = EntityDamage.LIVING_ONLY;
    @Config(description = {"Chance a creeper will burst into fireworks, otherwise vanilla explosion particles show.", "Other changes to explosion behavior still apply such as no damage to terrain."})
    @Config.DoubleRange(min = 0.0, max = 1.0)
    public double confettiChance = 1.0;

    public enum EntityDamage {
        NONE,
        LIVING_ONLY,
        PLAYERS_ONLY,
        ALL;

        public boolean filter(Entity entity) {
            if (this == NONE) {
                return false;
            } else if (this == LIVING_ONLY) {
                return entity instanceof LivingEntity;
            } else if (this == PLAYERS_ONLY) {
                return entity instanceof Player;
            } else {
                return true;
            }
        }
    }
}
