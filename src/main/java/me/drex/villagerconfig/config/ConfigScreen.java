package me.drex.villagerconfig.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ConfigScreen {

    public static Screen getConfigScreen(Screen parentScreen) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parentScreen)
                .setDefaultBackgroundTexture(new Identifier("minecraft:textures/block/emerald_block.png"))
                .setTitle(Text.translatable("config.villagerconfig.title"));

        builder.setGlobalized(true);
        builder.setGlobalizedExpanded(false);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory features = builder.getOrCreateCategory(Text.translatable("config.villagerconfig.category.features"));

        features.addEntry(entryBuilder.startDoubleField(Text.translatable("config.villagerconfig.maxdiscount"), ConfigEntries.features.maxDiscount)
                .setDefaultValue(100)
                .setMin(0.0)
                .setMax(100.0)
                .setSaveConsumer(value -> ConfigEntries.features.maxDiscount = value)
                .setTooltip(Text.translatable("config.villagerconfig.maxdiscount.tooltip"))
                .build());

        features.addEntry(entryBuilder.startDoubleField(Text.translatable("config.villagerconfig.maxraise"), ConfigEntries.features.maxRaise)
                .setDefaultValue(100)
                .setMin(0.0)
                .setMax(100.0)
                .setSaveConsumer(value -> ConfigEntries.features.maxRaise = value)
                .setTooltip(Text.translatable("config.villagerconfig.maxraise.tooltip"))
                .build());

        features.addEntry(entryBuilder.startDoubleField(Text.translatable("config.villagerconfig.conversionchance"), ConfigEntries.features.conversionChance)
                .setDefaultValue(-1)
                .setMin(-1.0)
                .setMax(100.0)
                .setSaveConsumer(value -> ConfigEntries.features.conversionChance = value)
                .setTooltip(Text.translatable("config.villagerconfig.conversionchance.tooltip"))
                .build());


        features.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.villagerconfig.tradecycling"), ConfigEntries.features.tradeCycling)
                .setDefaultValue(true)
                .setSaveConsumer(value -> ConfigEntries.features.tradeCycling = value)
                .setTooltip(Text.translatable("config.villagerconfig.tradecycling.tooltip"))
                .build());

        features.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.villagerconfig.infiniteTrades"), ConfigEntries.features.infiniteTrades)
                .setDefaultValue(false)
                .setSaveConsumer(value -> ConfigEntries.features.infiniteTrades = value)
                .setTooltip(Text.translatable("config.villagerconfig.infiniteTrades.tooltip"))
                .build());

        ConfigCategory oldTrades = builder.getOrCreateCategory(Text.translatable("config.villagerconfig.category.old_trades"))
                .setCategoryBackground(new Identifier("minecraft:textures/block/emerald_block.png"));

        oldTrades.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.villagerconfig.enabled"), ConfigEntries.oldTrades.enabled)
                .setDefaultValue(false)
                .setSaveConsumer(value -> ConfigEntries.oldTrades.enabled = value)
                .setTooltip(Text.translatable("config.villagerconfig.enabled.tooltip"))
                .build());

        oldTrades.addEntry(entryBuilder.startIntField(Text.translatable("config.villagerconfig.minuses"), ConfigEntries.oldTrades.minUses)
                .setDefaultValue(2)
                .setMin(0)
                .setSaveConsumer(value -> ConfigEntries.oldTrades.minUses = value)
                .setTooltip(Text.translatable("config.villagerconfig.minuses.tooltip"))
                .build());

        oldTrades.addEntry(entryBuilder.startIntField(Text.translatable("config.villagerconfig.maxuses2"), ConfigEntries.oldTrades.maxUses)
                .setDefaultValue(12)
                .setMin(0)
                .setSaveConsumer(value -> ConfigEntries.oldTrades.maxUses = value)
                .setTooltip(Text.translatable("config.villagerconfig.maxuses2.tooltip"))
                .build());

        oldTrades.addEntry(entryBuilder.startDoubleField(Text.translatable("config.villagerconfig.lockchance"), ConfigEntries.oldTrades.lockChance)
                .setDefaultValue(20)
                .setMin(0)
                .setMax(100)
                .setSaveConsumer(value -> ConfigEntries.oldTrades.lockChance = value)
                .setTooltip(Text.translatable("config.villagerconfig.lockchance.tooltip"))
                .build());

        oldTrades.addEntry(entryBuilder.startDoubleField(Text.translatable("config.villagerconfig.unlockchance"), ConfigEntries.oldTrades.unlockChance)
                .setDefaultValue(20)
                .setMin(0)
                .setMax(100)
                .setSaveConsumer(value -> ConfigEntries.oldTrades.unlockChance = value)
                .setTooltip(Text.translatable("config.villagerconfig.unlockchance.tooltip"))
                .build());


        builder.setSavingRunnable(Config::saveModConfig);

        return builder.build();
    }

}
