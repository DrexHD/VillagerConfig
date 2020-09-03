package me.drex.villagerfix.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.drex.villagerfix.VillagerFix;
import me.drex.villagerfix.config.MainConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

public class Main {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        String s = "a";
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
        final MainConfig CONFIG = VillagerFix.INSTANCE.config();
        ModMetadata meta = FabricLoader.getInstance().getModContainer("villagerfix").get().getMetadata();
        boolean lock = CONFIG.lock;
        MutableText text = new LiteralText("")
                .append(new LiteralText("VillagerFix Version: " + meta.getVersion().getFriendlyString()).formatted(Formatting.WHITE, Formatting.BOLD)
                    .styled(style -> style
                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Click to copy config file location.").formatted(Formatting.AQUA)))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, VillagerFix.configPath().resolve("villagerfix.conf").toFile().getAbsolutePath()))))
                .append(new LiteralText("\n\nSettings: ").formatted(Formatting.WHITE, Formatting.BOLD))
                .append(new LiteralText("\nDiscount (max): ").formatted(Formatting.AQUA))
                .append(new LiteralText(CONFIG.maxdiscount + "%").formatted(Formatting.GRAY, Formatting.ITALIC)
                    .styled(style -> style
                            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Vanilla: ").formatted(Formatting.AQUA).append(new LiteralText("100%").formatted(Formatting.GRAY))))))
                .append(new LiteralText("\nRaise (max): ").formatted(Formatting.AQUA))
                .append(new LiteralText(CONFIG.maxraise + "%").formatted(Formatting.GRAY, Formatting.ITALIC)
                        .styled(style -> style
                                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Vanilla: ").formatted(Formatting.AQUA).append(new LiteralText("100%").formatted(Formatting.GRAY))))))
                .append(new LiteralText("\nConversion chance: ").formatted(Formatting.AQUA))
                .append(new LiteralText(CONFIG.conversionchance + "%").formatted(Formatting.GRAY, Formatting.ITALIC)
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
                .append(new LiteralText(String.valueOf(CONFIG.oldtrades.enabled)).formatted(Formatting.GRAY, Formatting.ITALIC)
                        .styled(style -> style
                                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("uses (min): ").formatted(Formatting.AQUA).append(new LiteralText(String.valueOf(CONFIG.oldtrades.minUses)).formatted(Formatting.GRAY))
                                .append(new LiteralText("\nuses (max): ").formatted(Formatting.AQUA).append(new LiteralText(String.valueOf(CONFIG.oldtrades.maxuses)).formatted(Formatting.GRAY))
                                .append(new LiteralText("\nlockchance: ").formatted(Formatting.AQUA).append(new LiteralText(String.valueOf(CONFIG.oldtrades.lockchance)).formatted(Formatting.GRAY))
                                .append(new LiteralText("\nunlockchance: ").formatted(Formatting.AQUA).append(new LiteralText(String.valueOf(CONFIG.oldtrades.unlockchance)).formatted(Formatting.GRAY)))))))));
        context.getSource().sendFeedback(text, false);
        return 1;
    }

}
