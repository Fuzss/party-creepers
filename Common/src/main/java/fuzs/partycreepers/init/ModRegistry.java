package fuzs.partycreepers.init;

import fuzs.partycreepers.PartyCreepers;
import fuzs.puzzleslib.api.init.v3.tags.BoundTagFactory;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class ModRegistry {
    static final BoundTagFactory TAGS = BoundTagFactory.make(PartyCreepers.MOD_ID);
    public static final TagKey<EntityType<?>> EXPLOSIVE_CREEPERS_ENTITY_TYPE_TAG = TAGS.registerEntityTypeTag("explosive_creepers");

    public static void touch() {

    }
}
