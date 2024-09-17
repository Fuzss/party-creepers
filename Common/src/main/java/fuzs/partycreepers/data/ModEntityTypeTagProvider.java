package fuzs.partycreepers.data;

import fuzs.partycreepers.init.ModRegistry;
import fuzs.puzzleslib.api.data.v2.AbstractTagProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public class ModEntityTypeTagProvider extends AbstractTagProvider.EntityTypes {

    public ModEntityTypeTagProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        this.tag(ModRegistry.EXPLOSIVE_CREEPERS_ENTITY_TYPE_TAG).add(EntityType.CREEPER).addOptionalTag(new ResourceLocation("creeperoverhaul:creepers"));
    }
}
