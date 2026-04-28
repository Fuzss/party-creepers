package fuzs.partycreepers.fabric.client;

import fuzs.partycreepers.common.PartyCreepers;
import fuzs.partycreepers.common.client.PartyCreepersClient;
import fuzs.puzzleslib.common.api.client.core.v1.ClientModConstructor;
import net.fabricmc.api.ClientModInitializer;

public class PartyCreepersFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(PartyCreepers.MOD_ID, PartyCreepersClient::new);
    }
}
