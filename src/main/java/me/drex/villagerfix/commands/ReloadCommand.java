package me.drex.villagerfix.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.drex.villagerfix.VillagerFix;
import me.drex.villagerfix.config.Config;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class ReloadCommand {

    public static void register(LiteralArgumentBuilder<ServerCommandSource> command) {
        LiteralArgumentBuilder<ServerCommandSource> reload = LiteralArgumentBuilder.literal("reload");
        reload.executes(ReloadCommand::execute);
        command.then(reload);
    }

    private static int execute(CommandContext<ServerCommandSource> context) {
        Config.load();

        VillagerFix.LOGGER.info("VillagerFix config reloaded!");
        context.getSource().sendFeedback(new LiteralText("VillagerFix config reloaded.").formatted(Formatting.GREEN), false);
        return 1;
    }

}
