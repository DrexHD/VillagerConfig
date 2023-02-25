package me.drex.villagerconfig.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import static me.drex.villagerconfig.config.ConfigManager.CONFIG;

public class ConfigScreen {

    public static Screen getConfigScreen(Screen parentScreen) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parentScreen)
                .setDefaultBackgroundTexture(new ResourceLocation("minecraft:textures/block/emerald_block.png"))
                .setTitle(new TranslatableComponent("config.villagerconfig.title"));

        builder.setGlobalized(true);
        builder.setGlobalizedExpanded(false);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory features = builder.getOrCreateCategory(new TranslatableComponent("config.villagerconfig.category.features"));

        features.addEntry(entryBuilder.startDoubleField(new TranslatableComponent("config.villagerconfig.maxdiscount"), CONFIG.features.maxDiscount)
                .setDefaultValue(100)
                .setMin(0.0)
                .setMax(100.0)
                .setSaveConsumer(value -> CONFIG.features.maxDiscount = value)
                .setTooltip(new TranslatableComponent("config.villagerconfig.maxdiscount.tooltip"))
                .build());

        features.addEntry(entryBuilder.startDoubleField(new TranslatableComponent("config.villagerconfig.maxraise"), CONFIG.features.maxRaise)
                .setDefaultValue(100)
                .setMin(0.0)
                .setMax(100.0)
                .setSaveConsumer(value -> CONFIG.features.maxRaise = value)
                .setTooltip(new TranslatableComponent("config.villagerconfig.maxraise.tooltip"))
                .build());

        features.addEntry(entryBuilder.startDoubleField(new TranslatableComponent("config.villagerconfig.conversionchance"), CONFIG.features.conversionChance)
                .setDefaultValue(-1)
                .setMin(-1.0)
                .setMax(100.0)
                .setSaveConsumer(value -> CONFIG.features.conversionChance = value)
                .setTooltip(new TranslatableComponent("config.villagerconfig.conversionchance.tooltip"))
                .build());


        features.addEntry(entryBuilder.startBooleanToggle(new TranslatableComponent("config.villagerconfig.tradecycling"), CONFIG.features.tradeCycling)
                .setDefaultValue(true)
                .setSaveConsumer(value -> CONFIG.features.tradeCycling = value)
                .setTooltip(new TranslatableComponent("config.villagerconfig.tradecycling.tooltip"))
                .build());

        features.addEntry(entryBuilder.startBooleanToggle(new TranslatableComponent("config.villagerconfig.infiniteTrades"), CONFIG.features.infiniteTrades)
                .setDefaultValue(false)
                .setSaveConsumer(value -> CONFIG.features.infiniteTrades = value)
                .setTooltip(new TranslatableComponent("config.villagerconfig.infiniteTrades.tooltip"))
                .build());

        ConfigCategory oldTrades = builder.getOrCreateCategory(new TranslatableComponent("config.villagerconfig.category.old_trades"))
                .setCategoryBackground(new ResourceLocation("minecraft:textures/block/emerald_block.png"));

        oldTrades.addEntry(entryBuilder.startBooleanToggle(new TranslatableComponent("config.villagerconfig.enabled"), CONFIG.oldTrades.enabled)
                .setDefaultValue(false)
                .setSaveConsumer(value -> CONFIG.oldTrades.enabled = value)
                .setTooltip(new TranslatableComponent("config.villagerconfig.enabled.tooltip"))
                .build());

        oldTrades.addEntry(entryBuilder.startIntField(new TranslatableComponent("config.villagerconfig.minuses"), CONFIG.oldTrades.minUses)
                .setDefaultValue(2)
                .setMin(0)
                .setSaveConsumer(value -> CONFIG.oldTrades.minUses = value)
                .setTooltip(new TranslatableComponent("config.villagerconfig.minuses.tooltip"))
                .build());

        oldTrades.addEntry(entryBuilder.startIntField(new TranslatableComponent("config.villagerconfig.maxuses2"), CONFIG.oldTrades.maxUses)
                .setDefaultValue(12)
                .setMin(0)
                .setSaveConsumer(value -> CONFIG.oldTrades.maxUses = value)
                .setTooltip(new TranslatableComponent("config.villagerconfig.maxuses2.tooltip"))
                .build());

        oldTrades.addEntry(entryBuilder.startDoubleField(new TranslatableComponent("config.villagerconfig.lockchance"), CONFIG.oldTrades.lockChance)
                .setDefaultValue(20)
                .setMin(0)
                .setMax(100)
                .setSaveConsumer(value -> CONFIG.oldTrades.lockChance = value)
                .setTooltip(new TranslatableComponent("config.villagerconfig.lockchance.tooltip"))
                .build());

        oldTrades.addEntry(entryBuilder.startDoubleField(new TranslatableComponent("config.villagerconfig.unlockchance"), CONFIG.oldTrades.unlockChance)
                .setDefaultValue(20)
                .setMin(0)
                .setMax(100)
                .setSaveConsumer(value -> CONFIG.oldTrades.unlockChance = value)
                .setTooltip(new TranslatableComponent("config.villagerconfig.unlockchance.tooltip"))
                .build());


        builder.setSavingRunnable(ConfigManager::saveModConfig);

        return builder.build();
    }

}
