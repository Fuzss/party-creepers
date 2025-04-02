package fuzs.partycreepers.fabric.client;

import fuzs.partycreepers.PartyCreepers;
import fuzs.partycreepers.client.PartyCreepersClient;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.fabricmc.api.ClientModInitializer;

public class PartyCreepersFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(PartyCreepers.MOD_ID, PartyCreepersClient::new);
    }
}
