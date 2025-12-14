package fuzs.partycreepers.neoforge.client;

import fuzs.partycreepers.PartyCreepers;
import fuzs.partycreepers.client.PartyCreepersClient;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = PartyCreepers.MOD_ID, dist = Dist.CLIENT)
public class PartyCreepersNeoForgeClient {

    public PartyCreepersNeoForgeClient() {
        ClientModConstructor.construct(PartyCreepers.MOD_ID, PartyCreepersClient::new);
    }
}
