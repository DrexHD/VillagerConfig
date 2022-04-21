package me.drex.villagerconfig.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.drex.villagerconfig.VillagerConfig;
import me.drex.villagerconfig.config.Config;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ReloadCommand {

    public static void register(LiteralArgumentBuilder<ServerCommandSource> command) {
        LiteralArgumentBuilder<ServerCommandSource> reload = LiteralArgumentBuilder.literal("reload");
        reload.executes(ReloadCommand::execute);
        command.then(reload);
    }

    private static int execute(CommandContext<ServerCommandSource> context) {
        Config.load();
        VillagerConfig.LOGGER.info("VillagerConfig config reloaded!");
        context.getSource().sendFeedback(Text.literal("VillagerConfig config reloaded.").formatted(Formatting.GREEN), false);
        return 1;
    }

}
