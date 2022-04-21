package me.drex.villagerconfig.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.drex.villagerconfig.VillagerConfig;
import me.drex.villagerconfig.config.ConfigEntries;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class VillagerConfigCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> main = LiteralArgumentBuilder.literal("villagerconfig");
        LiteralArgumentBuilder<ServerCommandSource> alias = LiteralArgumentBuilder.literal("vc");
        main.executes(VillagerConfigCommand::execute).requires(src -> src.hasPermissionLevel(2));
        alias.executes(VillagerConfigCommand::execute).requires(src -> src.hasPermissionLevel(2));
        then(main);
        then(alias);
        dispatcher.register(main);
        dispatcher.register(alias);
    }

    private static void then(LiteralArgumentBuilder<ServerCommandSource> literal) {
        GenerateCommand.register(literal);
        ReloadCommand.register(literal);
    }

    private static int execute(CommandContext<ServerCommandSource> context) {
        ModMetadata meta = FabricLoader.getInstance().getModContainer("villagerconfig").get().getMetadata();
        ConfigEntries.OldTradesGroup oldTrades = ConfigEntries.oldTrades;
        ConfigEntries.FeaturesGroup features = ConfigEntries.features;
        MutableText text = Text.literal("")
                .append(Text.literal("VillagerConfig Version: " + meta.getVersion().getFriendlyString()).formatted(Formatting.WHITE, Formatting.BOLD)
                        .styled(style -> style
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Click to copy config file location.").formatted(Formatting.AQUA)))
                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, VillagerConfig.DATA_PATH.resolve("villagerconfig.json5").toFile().getAbsolutePath()))))
                .append(Text.literal("\n\nSettings: ").formatted(Formatting.WHITE, Formatting.BOLD))
                .append(Text.literal("\nDiscount (max): ").formatted(Formatting.AQUA))
                .append(Text.literal(features.maxDiscount + "%").formatted(Formatting.GRAY, Formatting.ITALIC)
                        .styled(style -> style
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Vanilla: ").formatted(Formatting.AQUA).append(Text.literal("100%").formatted(Formatting.GRAY))))))
                .append(Text.literal("\nRaise (max): ").formatted(Formatting.AQUA))
                .append(Text.literal(features.maxRaise + "%").formatted(Formatting.GRAY, Formatting.ITALIC)
                        .styled(style -> style
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Vanilla: ").formatted(Formatting.AQUA).append(Text.literal("100%").formatted(Formatting.GRAY))))))
                .append(Text.literal("\nConversion chance: ").formatted(Formatting.AQUA))
                .append(Text.literal(features.conversionChance + "%").formatted(Formatting.GRAY, Formatting.ITALIC)
                        .styled(style -> style
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Vanilla: ").formatted(Formatting.AQUA)
                                        .append(Text.literal("\nHard: ").formatted(Formatting.RED))
                                        .append(Text.literal("100%").formatted(Formatting.GRAY))
                                        .append(Text.literal("\nNormal: ").formatted(Formatting.YELLOW))
                                        .append(Text.literal("50%").formatted(Formatting.GRAY))
                                        .append(Text.literal("\nEasy / Peaceful: ").formatted(Formatting.GREEN))
                                        .append(Text.literal("0%").formatted(Formatting.GRAY))
                                ))))
                .append(Text.literal("\nTrade Cycling: ").formatted(Formatting.AQUA))
                .append(Text.literal(String.valueOf(ConfigEntries.features.tradeCycling)).formatted(Formatting.GRAY, Formatting.ITALIC)
                        .styled(style -> style
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Vanilla: ").formatted(Formatting.AQUA).append(Text.literal("false").formatted(Formatting.GRAY))))))
                .append(Text.literal("\nOld Trade Mechanics: ").formatted(Formatting.AQUA))
                .append(Text.literal(String.valueOf(oldTrades.enabled)).formatted(Formatting.GRAY, Formatting.ITALIC)
                        .styled(style -> style
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("uses (min): ").formatted(Formatting.AQUA).append(Text.literal(String.valueOf(oldTrades.minUses)).formatted(Formatting.GRAY))
                                        .append(Text.literal("\nuses (max): ").formatted(Formatting.AQUA).append(Text.literal(String.valueOf(oldTrades.maxUses)).formatted(Formatting.GRAY))
                                                .append(Text.literal("\nlockchance: ").formatted(Formatting.AQUA).append(Text.literal(String.valueOf(oldTrades.lockChance)).formatted(Formatting.GRAY))
                                                        .append(Text.literal("\nunlockchance: ").formatted(Formatting.AQUA).append(Text.literal(String.valueOf(oldTrades.unlockChance)).formatted(Formatting.GRAY)))))))));
        context.getSource().sendFeedback(text, false);
        return 1;
    }

}