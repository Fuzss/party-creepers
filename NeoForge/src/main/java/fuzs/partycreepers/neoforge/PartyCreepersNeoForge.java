package fuzs.partycreepers.neoforge;

import fuzs.partycreepers.common.PartyCreepers;
import fuzs.partycreepers.common.data.tags.ModEntityTypeTagsProvider;
import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v3.core.DataProviderBuilder;
import net.neoforged.fml.common.Mod;

@Mod(PartyCreepers.MOD_ID)
public class PartyCreepersNeoForge {

    public PartyCreepersNeoForge() {
        ModConstructor.construct(PartyCreepers.MOD_ID, PartyCreepers::new);
        DataProviderBuilder.of(PartyCreepers.MOD_ID).addProvider(ModEntityTypeTagsProvider::new);
    }
}
