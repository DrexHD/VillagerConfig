package me.drex.villagerfix;

import me.drex.villagerfix.commands.VillagerFixCommand;
import me.drex.villagerfix.config.Config;
import me.drex.villagerfix.util.Deobfuscator;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

public class VillagerFix implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("VillagerFix");
    public static final Path DATA_PATH = FabricLoader.getInstance().getConfigDir().resolve("VillagerFix");

    @Override
    public void onInitialize() {
        Config.load();
        Deobfuscator.init();
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            VillagerFixCommand.register(dispatcher);
        });
    }
}
