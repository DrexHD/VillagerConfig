package me.drex.villagerfix.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class ConfigScreen {

    public static Screen getConfigScreen(Screen parentScreen) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parentScreen)
                .setDefaultBackgroundTexture(new Identifier("minecraft:textures/block/emerald_block.png"))
                .setTitle(new TranslatableText("config.villagerfix.title"));

        builder.setGlobalized(true);
        builder.setGlobalizedExpanded(false);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory features = builder.getOrCreateCategory(new TranslatableText("config.villagerfix.category.features"));

        features.addEntry(entryBuilder.startDoubleField(new TranslatableText("config.villagerfix.maxdiscount"), ConfigEntries.features.maxDiscount)
        .setDefaultValue(100)
        .setMin(0.0)
        .setMax(100.0)
            .setSaveConsumer(value -> ConfigEntries.features.maxDiscount = value)
        .setTooltip(new TranslatableText("config.villagerfix.maxdiscount.tooltip"))
        .build());

        features.addEntry(entryBuilder.startDoubleField(new TranslatableText("config.villagerfix.maxraise"), ConfigEntries.features.maxRaise)
        .setDefaultValue(100)
        .setMin(0.0)
        .setMax(100.0)
            .setSaveConsumer(value -> ConfigEntries.features.maxRaise = value)
        .setTooltip(new TranslatableText("config.villagerfix.maxraise.tooltip"))
        .build());

        features.addEntry(entryBuilder.startDoubleField(new TranslatableText("config.villagerfix.maxuses"), ConfigEntries.features.maxUses)
        .setDefaultValue(100)
        .setMin(0.0)
            .setSaveConsumer(value -> ConfigEntries.features.maxUses = value)
        .setTooltip(new TranslatableText("config.villagerfix.maxuses.tooltip"))
        .build());

        features.addEntry(entryBuilder.startDoubleField(new TranslatableText("config.villagerfix.conversionchance"), ConfigEntries.features.conversionChance)
        .setDefaultValue(-1)
        .setMin(-1.0)
        .setMax(100.0)
            .setSaveConsumer(value -> ConfigEntries.features.conversionChance = value)
        .setTooltip(new TranslatableText("config.villagerfix.conversionchance.tooltip"))
        .build());

        features.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config.villagerfix.lock"), ConfigEntries.features.lock)
        .setDefaultValue(false)
            .setSaveConsumer(value -> ConfigEntries.features.lock = value)
        .setTooltip(new TranslatableText("config.villagerfix.lock.tooltip"))
        .build());

        features.addEntry(entryBuilder.startStrList(new TranslatableText("config.villagerfix.blacklisted_trades"), ConfigEntries.features.blacklistedTrades)
        .setDefaultValue(ArrayList::new)
            .setSaveConsumer(strings -> ConfigEntries.features.blacklistedTrades = strings)
        .build());

        ConfigCategory oldTrades = builder.getOrCreateCategory(new TranslatableText("config.villagerfix.category.old_trades"))
                .setCategoryBackground(new Identifier("minecraft:textures/block/emerald_block.png"));

        oldTrades.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config.villagerfix.enabled"), ConfigEntries.oldTrades.enabled)
                .setDefaultValue(false)
                .setSaveConsumer(value -> ConfigEntries.oldTrades.enabled = value)
                .setTooltip(new TranslatableText("config.villagerfix.enabled.tooltip"))
                .build());

        oldTrades.addEntry(entryBuilder.startIntField(new TranslatableText("config.villagerfix.minuses"), ConfigEntries.oldTrades.minUses)
                .setDefaultValue(2)
                .setMin(0)
                .setSaveConsumer(value -> ConfigEntries.oldTrades.minUses = value)
                .setTooltip(new TranslatableText("config.villagerfix.minuses.tooltip"))
                .build());

        oldTrades.addEntry(entryBuilder.startIntField(new TranslatableText("config.villagerfix.maxuses2"), ConfigEntries.oldTrades.maxUses)
                .setDefaultValue(12)
                .setMin(0)
                .setSaveConsumer(value -> ConfigEntries.oldTrades.maxUses = value)
                .setTooltip(new TranslatableText("config.villagerfix.maxuses2.tooltip"))
                .build());

        oldTrades.addEntry(entryBuilder.startDoubleField(new TranslatableText("config.villagerfix.lockchance"), ConfigEntries.oldTrades.lockChance)
                .setDefaultValue(20)
                .setMin(0)
                .setMax(100)
                .setSaveConsumer(value -> ConfigEntries.oldTrades.lockChance = value)
                .setTooltip(new TranslatableText("config.villagerfix.lockchance.tooltip"))
                .build());

        oldTrades.addEntry(entryBuilder.startDoubleField(new TranslatableText("config.villagerfix.unlockchance"), ConfigEntries.oldTrades.unlockChance)
                .setDefaultValue(20)
                .setMin(0)
                .setMax(100)
                .setSaveConsumer(value -> ConfigEntries.oldTrades.unlockChance = value)
                .setTooltip(new TranslatableText("config.villagerfix.unlockchance.tooltip"))
                .build());



        builder.setSavingRunnable(Config::saveModConfig);

        return builder.build();
    }

}
