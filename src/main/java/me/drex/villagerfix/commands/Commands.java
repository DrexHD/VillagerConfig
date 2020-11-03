package me.drex.villagerfix.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.drex.villagerfix.VillagerFix;
import me.drex.villagerfix.config.ConfigEntries;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

public class Commands {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> main = LiteralArgumentBuilder.literal("villagerfix");
        LiteralArgumentBuilder<ServerCommandSource> alias = LiteralArgumentBuilder.literal("vf");
        main.executes(this::execute).requires(src -> src.hasPermissionLevel(2));
        alias.executes(this::execute).requires(src -> src.hasPermissionLevel(2));
        then(main);
        then(alias);
        dispatcher.register(main);
        dispatcher.register(alias);
    }

    private void then(LiteralArgumentBuilder<ServerCommandSource> literal) {
        new Reload().register(literal);
    }

    private int execute(CommandContext<ServerCommandSource> context) {
        ModMetadata meta = FabricLoader.getInstance().getModContainer("villagerfix").get().getMetadata();
        boolean lock = ConfigEntries.features.lock;
        ConfigEntries.OldTradesGroup oldTrades = ConfigEntries.oldTrades;
        ConfigEntries.FeaturesGroup features = ConfigEntries.features;
        MutableText text = new LiteralText("")
                .append(new LiteralText("VillagerFix Version: " + meta.getVersion().getFriendlyString()).formatted(Formatting.WHITE, Formatting.BOLD)
                    .styled(style -> style
                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Click to copy config file location.").formatted(Formatting.AQUA)))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, FabricLoader.getInstance().getConfigDir().resolve("villagerfix.json5").toFile().getAbsolutePath()))))
                .append(new LiteralText("\n\nSettings: ").formatted(Formatting.WHITE, Formatting.BOLD))
                .append(new LiteralText("\nDiscount (max): ").formatted(Formatting.AQUA))
                .append(new LiteralText(features.maxDiscount + "%").formatted(Formatting.GRAY, Formatting.ITALIC)
                    .styled(style -> style
                            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Vanilla: ").formatted(Formatting.AQUA).append(new LiteralText("100%").formatted(Formatting.GRAY))))))
                .append(new LiteralText("\nRaise (max): ").formatted(Formatting.AQUA))
                .append(new LiteralText(features.maxRaise + "%").formatted(Formatting.GRAY, Formatting.ITALIC)
                        .styled(style -> style
                                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Vanilla: ").formatted(Formatting.AQUA).append(new LiteralText("100%").formatted(Formatting.GRAY))))))
                .append(new LiteralText("\nUses (max): ").formatted(Formatting.AQUA))
                .append(new LiteralText(features.maxUses + "%").formatted(Formatting.GRAY, Formatting.ITALIC)
                        .styled(style -> style
                                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Vanilla: ").formatted(Formatting.AQUA).append(new LiteralText("100%").formatted(Formatting.GRAY))))))
                .append(new LiteralText("\nConversion chance: ").formatted(Formatting.AQUA))
                .append(new LiteralText(features.conversionChance + "%").formatted(Formatting.GRAY, Formatting.ITALIC)
                    .styled(style -> style
                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Vanilla: ").formatted(Formatting.AQUA)
                                .append(new LiteralText("\nHard: ").formatted(Formatting.RED))
                                .append(new LiteralText("100%").formatted(Formatting.GRAY))
                                .append(new LiteralText("\nNormal: ").formatted(Formatting.YELLOW))
                                .append(new LiteralText("50%").formatted(Formatting.GRAY))
                                .append(new LiteralText("\nEasy / Peaceful: ").formatted(Formatting.GREEN))
                                .append(new LiteralText("0%").formatted(Formatting.GRAY))
                        ))))
                .append(new LiteralText("\nLock Villagers: ").formatted(Formatting.AQUA))
                .append(new LiteralText(String.valueOf(lock)).formatted(Formatting.GRAY, Formatting.ITALIC)
                    .styled(style -> style
                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Vanilla: ").formatted(Formatting.AQUA).append(new LiteralText("false").formatted(Formatting.GRAY))))))
                .append(new LiteralText("\nOld Trade Mechanics: ").formatted(Formatting.AQUA))
                .append(new LiteralText(String.valueOf(oldTrades.enabled)).formatted(Formatting.GRAY, Formatting.ITALIC)
                        .styled(style -> style
                                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("uses (min): ").formatted(Formatting.AQUA).append(new LiteralText(String.valueOf(oldTrades.minUses)).formatted(Formatting.GRAY))
                                .append(new LiteralText("\nuses (max): ").formatted(Formatting.AQUA).append(new LiteralText(String.valueOf(oldTrades.maxUses)).formatted(Formatting.GRAY))
                                .append(new LiteralText("\nlockchance: ").formatted(Formatting.AQUA).append(new LiteralText(String.valueOf(oldTrades.lockChance)).formatted(Formatting.GRAY))
                                .append(new LiteralText("\nunlockchance: ").formatted(Formatting.AQUA).append(new LiteralText(String.valueOf(oldTrades.unlockChance)).formatted(Formatting.GRAY)))))))));
        context.getSource().sendFeedback(text, false);
        return 1;
    }

}
