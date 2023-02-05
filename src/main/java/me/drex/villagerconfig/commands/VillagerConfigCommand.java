package me.drex.villagerconfig.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;

import static me.drex.villagerconfig.config.ConfigManager.CONFIG;
import static net.minecraft.commands.Commands.literal;

// TODO: Use https://github.com/samolego/Config2Brigadier
public class VillagerConfigCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> config = literal("config").build();
        CONFIG.generateCommand(config);
        LiteralCommandNode<CommandSourceStack> root = dispatcher.register(
                literal("villagerconfig")
                        .requires(src -> src.hasPermission(2))
                        .then(GenerateCommand.builder())
                        .then(ReloadCommand.builder())
                        .then(
                               config
                        )
                        .executes(VillagerConfigCommand::execute)
        );
        dispatcher.register(literal("vc").requires(src -> src.hasPermission(2)).executes(VillagerConfigCommand::execute).redirect(root));
    }

    private static int execute(CommandContext<CommandSourceStack> context) {

        /*ModMetadata meta = FabricLoader.getInstance().getModContainer("villagerconfig").get().getMetadata();
        Config.OldTradesGroup oldTrades = CONFIG.oldTrades;
        Config.FeaturesGroup features = CONFIG.features;
        MutableComponent text = Component.literal("")
                .append(Component.literal("VillagerConfig Version: " + meta.getVersion().getFriendlyString()).withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD)
                        .withStyle(style -> style
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to copy config file location.").withStyle(ChatFormatting.AQUA)))
                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, VillagerConfig.DATA_PATH.resolve("villagerconfig.json5").toFile().getAbsolutePath()))))
                .append(Component.literal("\n\nSettings: ").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD))
                .append(Component.literal("\nDiscount (max): ").withStyle(ChatFormatting.AQUA))
                .append(Component.literal(features.maxDiscount + "%").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
                        .withStyle(style -> style
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Vanilla: ").withStyle(ChatFormatting.AQUA).append(Component.literal("100%").withStyle(ChatFormatting.GRAY))))))
                .append(Component.literal("\nRaise (max): ").withStyle(ChatFormatting.AQUA))
                .append(Component.literal(features.maxRaise + "%").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
                        .withStyle(style -> style
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Vanilla: ").withStyle(ChatFormatting.AQUA).append(Component.literal("100%").withStyle(ChatFormatting.GRAY))))))
                .append(Component.literal("\nConversion chance: ").withStyle(ChatFormatting.AQUA))
                .append(Component.literal(features.conversionChance + "%").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
                        .withStyle(style -> style
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Vanilla: ").withStyle(ChatFormatting.AQUA)
                                        .append(Component.literal("\nHard: ").withStyle(ChatFormatting.RED))
                                        .append(Component.literal("100%").withStyle(ChatFormatting.GRAY))
                                        .append(Component.literal("\nNormal: ").withStyle(ChatFormatting.YELLOW))
                                        .append(Component.literal("50%").withStyle(ChatFormatting.GRAY))
                                        .append(Component.literal("\nEasy / Peaceful: ").withStyle(ChatFormatting.GREEN))
                                        .append(Component.literal("0%").withStyle(ChatFormatting.GRAY))
                                ))))
                .append(Component.literal("\nTrade Cycling: ").withStyle(ChatFormatting.AQUA))
                .append(Component.literal(String.valueOf(CONFIG.features.tradeCycling)).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
                        .withStyle(style -> style
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Vanilla: ").withStyle(ChatFormatting.AQUA).append(Component.literal("false").withStyle(ChatFormatting.GRAY))))))
                .append(Component.literal("\nOld Trade Mechanics: ").withStyle(ChatFormatting.AQUA))
                .append(Component.literal(String.valueOf(oldTrades.enabled)).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
                        .withStyle(style -> style
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("uses (min): ").withStyle(ChatFormatting.AQUA).append(Component.literal(String.valueOf(oldTrades.minUses)).withStyle(ChatFormatting.GRAY))
                                        .append(Component.literal("\nuses (max): ").withStyle(ChatFormatting.AQUA).append(Component.literal(String.valueOf(oldTrades.maxUses)).withStyle(ChatFormatting.GRAY))
                                                .append(Component.literal("\nlockchance: ").withStyle(ChatFormatting.AQUA).append(Component.literal(String.valueOf(oldTrades.lockChance)).withStyle(ChatFormatting.GRAY))
                                                        .append(Component.literal("\nunlockchance: ").withStyle(ChatFormatting.AQUA).append(Component.literal(String.valueOf(oldTrades.unlockChance)).withStyle(ChatFormatting.GRAY)))))))));
        context.getSource().sendSuccess(text, false);*/
        return 1;
    }

}