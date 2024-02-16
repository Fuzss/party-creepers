package fuzs.partycreepers.neoforge;

import fuzs.partycreepers.PartyCreepers;
import fuzs.partycreepers.data.ModEntityTypeTagProvider;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

@Mod(PartyCreepers.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PartyCreepersNeoForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(PartyCreepers.MOD_ID, PartyCreepers::new);
        DataProviderHelper.registerDataProviders(PartyCreepers.MOD_ID, ModEntityTypeTagProvider::new);
    }
}
