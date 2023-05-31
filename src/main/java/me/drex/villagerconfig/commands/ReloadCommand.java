package me.drex.villagerconfig.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.drex.villagerconfig.VillagerConfig;
import me.drex.villagerconfig.config.ConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.Commands.literal;

public class ReloadCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> builder() {
        return literal("reload").executes(ReloadCommand::execute);
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        ConfigManager.load();
        VillagerConfig.LOGGER.info("VillagerConfig config reloaded!");
        context.getSource().sendSuccess(() -> Component.literal("VillagerConfig config reloaded.").withStyle(ChatFormatting.GREEN), false);
        return 1;
    }

}
