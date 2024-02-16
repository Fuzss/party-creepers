package fuzs.partycreepers;

import fuzs.partycreepers.config.ServerConfig;
import fuzs.partycreepers.handler.CreeperConfettiHandler;
import fuzs.partycreepers.init.ModRegistry;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.event.v1.level.ExplosionEvents;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PartyCreepers implements ModConstructor {
    public static final String MOD_ID = "partycreepers";
    public static final String MOD_NAME = "Party Creepers";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final ConfigHolder CONFIG = ConfigHolder.builder(MOD_ID).server(ServerConfig.class);

    @Override
    public void onConstructMod() {
        ModRegistry.touch();
        registerHandlers();
    }

    private static void registerHandlers() {
        ExplosionEvents.DETONATE.register(CreeperConfettiHandler::onExplosionDetonate);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
