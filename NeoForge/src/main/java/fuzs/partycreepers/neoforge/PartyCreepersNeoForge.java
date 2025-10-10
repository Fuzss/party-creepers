package fuzs.partycreepers.neoforge;

import fuzs.partycreepers.PartyCreepers;
import fuzs.partycreepers.data.tags.ModEntityTypeTagProvider;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.neoforged.fml.common.Mod;

@Mod(PartyCreepers.MOD_ID)
public class PartyCreepersNeoForge {

    public PartyCreepersNeoForge() {
        ModConstructor.construct(PartyCreepers.MOD_ID, PartyCreepers::new);
        DataProviderHelper.registerDataProviders(PartyCreepers.MOD_ID, ModEntityTypeTagProvider::new);
    }
}
