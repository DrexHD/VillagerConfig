package me.drex.villagerfix.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.drex.villagerfix.VillagerFix;
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
        boolean lock = VillagerFix.INSTANCE.config().lock;
        MutableText text = new LiteralText("")
                .append(new LiteralText("VillagerFix Version: " + meta.getVersion().getFriendlyString()).formatted(Formatting.WHITE, Formatting.BOLD))
                .append(new LiteralText("\n\nSettings: ").formatted(Formatting.WHITE, Formatting.BOLD))
                .styled(style -> style
                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Click to copy config file location.").formatted(Formatting.AQUA)))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, VillagerFix.configPath().resolve("villagerfix.conf").toFile().getAbsolutePath())))
                .append(new LiteralText("\nDiscount (max): ").formatted(Formatting.AQUA))
                .append(new LiteralText(VillagerFix.INSTANCE.config().maxdiscount + "%").formatted(Formatting.GRAY, Formatting.ITALIC))
                .append(new LiteralText("\nLock Villagers: ").formatted(Formatting.AQUA))
                .append(new LiteralText(String.valueOf(lock)).formatted(Formatting.GRAY, Formatting.ITALIC));
        context.getSource().sendFeedback(text, false);
        return 1;
    }

}
