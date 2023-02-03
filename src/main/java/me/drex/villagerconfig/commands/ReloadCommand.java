package me.drex.villagerconfig.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.drex.villagerconfig.VillagerConfig;
import me.drex.villagerconfig.config.Config;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.literal;

public class ReloadCommand {

    public static LiteralArgumentBuilder<ServerCommandSource> builder() {
        return literal("reload").executes(ReloadCommand::execute);
    }

    private static int execute(CommandContext<ServerCommandSource> context) {
        Config.load();
        VillagerConfig.LOGGER.info("VillagerConfig config reloaded!");
        context.getSource().sendFeedback(Text.literal("VillagerConfig config reloaded.").formatted(Formatting.GREEN), false);
        return 1;
    }

}
