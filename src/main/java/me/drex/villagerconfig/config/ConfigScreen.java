package me.drex.villagerconfig.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static me.drex.villagerconfig.config.ConfigManager.CONFIG;

public class ConfigScreen {

    public static Screen getConfigScreen(Screen parentScreen) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parentScreen)
                .setDefaultBackgroundTexture(new ResourceLocation("minecraft:textures/block/emerald_block.png"))
                .setTitle(Component.translatable("config.villagerconfig.title"));

        builder.setGlobalized(true);
        builder.setGlobalizedExpanded(false);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory features = builder.getOrCreateCategory(Component.translatable("config.villagerconfig.category.features"));

        features.addEntry(entryBuilder.startDoubleField(Component.translatable("config.villagerconfig.maxdiscount"), CONFIG.features.maxDiscount)
                .setDefaultValue(100)
                .setMin(0.0)
                .setMax(100.0)
                .setSaveConsumer(value -> CONFIG.features.maxDiscount = value)
                .setTooltip(Component.translatable("config.villagerconfig.maxdiscount.tooltip"))
                .build());

        features.addEntry(entryBuilder.startDoubleField(Component.translatable("config.villagerconfig.maxraise"), CONFIG.features.maxRaise)
                .setDefaultValue(100)
                .setMin(0.0)
                .setMax(100.0)
                .setSaveConsumer(value -> CONFIG.features.maxRaise = value)
                .setTooltip(Component.translatable("config.villagerconfig.maxraise.tooltip"))
                .build());

        features.addEntry(entryBuilder.startDoubleField(Component.translatable("config.villagerconfig.conversionchance"), CONFIG.features.conversionChance)
                .setDefaultValue(-1)
                .setMin(-1.0)
                .setMax(100.0)
                .setSaveConsumer(value -> CONFIG.features.conversionChance = value)
                .setTooltip(Component.translatable("config.villagerconfig.conversionchance.tooltip"))
                .build());


        features.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.villagerconfig.tradecycling"), CONFIG.features.tradeCycling)
                .setDefaultValue(true)
                .setSaveConsumer(value -> CONFIG.features.tradeCycling = value)
                .setTooltip(Component.translatable("config.villagerconfig.tradecycling.tooltip"))
                .build());

        features.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.villagerconfig.infiniteTrades"), CONFIG.features.infiniteTrades)
                .setDefaultValue(false)
                .setSaveConsumer(value -> CONFIG.features.infiniteTrades = value)
                .setTooltip(Component.translatable("config.villagerconfig.infiniteTrades.tooltip"))
                .build());

        builder.setSavingRunnable(ConfigManager::saveModConfig);

        return builder.build();
    }

}
