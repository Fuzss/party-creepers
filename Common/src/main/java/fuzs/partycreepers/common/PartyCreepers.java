package fuzs.partycreepers.common;

import fuzs.partycreepers.common.config.ServerConfig;
import fuzs.partycreepers.common.handler.CreeperConfettiHandler;
import fuzs.partycreepers.common.init.ModRegistry;
import fuzs.puzzleslib.common.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import net.minecraft.resources.Identifier;
import fuzs.puzzleslib.common.api.event.v1.level.ExplosionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PartyCreepers implements ModConstructor {
    public static final String MOD_ID = "partycreepers";
    public static final String MOD_NAME = "Party Creepers";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final ConfigHolder CONFIG = ConfigHolder.builder(MOD_ID).server(ServerConfig.class);

    @Override
    public void onConstructMod() {
        ModRegistry.bootstrap();
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        ExplosionEvents.START.register(CreeperConfettiHandler::onExplosionStart);
        ExplosionEvents.DETONATE.register(CreeperConfettiHandler::onExplosionDetonate);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
