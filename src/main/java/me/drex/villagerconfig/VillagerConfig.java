package me.drex.villagerconfig;

import me.drex.villagerconfig.commands.VillagerConfigCommand;
import me.drex.villagerconfig.config.Config;
import me.drex.villagerconfig.mixin.LootContextTypesAccessor;
import me.drex.villagerconfig.util.Deobfuscator;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

public class VillagerConfig implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("VillagerConfig");
    public static final Path DATA_PATH = FabricLoader.getInstance().getConfigDir().resolve("VillagerConfig");
    public static final LootContextType VILLAGER_LOOT_CONTEXT = LootContextTypesAccessor.invokeRegister("villager", builder -> builder.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.ORIGIN));

    @Override
    public void onInitialize() {
        Config.load();
        Deobfuscator.init();
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            VillagerConfigCommand.register(dispatcher);
        });
    }
}
