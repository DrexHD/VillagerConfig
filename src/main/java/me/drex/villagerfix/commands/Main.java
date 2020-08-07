package me.drex.villagerfix.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.drex.villagerfix.VillagerFix;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
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
        context.getSource().sendFeedback(new LiteralText("Version: " + VillagerFix.version()).formatted(Formatting.AQUA), false);
        return 1;
    }

}
