package fuzs.partycreepers.fabric;

import fuzs.partycreepers.PartyCreepers;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;

public class PartyCreepersFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(PartyCreepers.MOD_ID, PartyCreepers::new);
    }
}
