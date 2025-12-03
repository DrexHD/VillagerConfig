package me.drex.villagerconfig.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.drex.villagerconfig.common.platform.PlatformHooks;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.Commands.literal;

public class VillagerConfigCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext) {
        LiteralCommandNode<CommandSourceStack> root = dispatcher.register(
            literal("villagerconfig")
                .requires(/*? if > 1.21.10 {*/ Commands.hasPermission(Commands.LEVEL_GAMEMASTERS) /*?} else {*/ /*src -> src.hasPermission(2)*/ /*?}*/)
                .then(GenerateCommand.builder())
                .then(ReloadCommand.builder())
                .then(TestCommand.builder(commandBuildContext))
                .executes(VillagerConfigCommand::execute)
        );
        dispatcher.register(literal("vc").requires(/*? if > 1.21.10 {*/ Commands.hasPermission(Commands.LEVEL_GAMEMASTERS) /*?} else {*/ /*src -> src.hasPermission(2)*/ /*?}*/).executes(VillagerConfigCommand::execute).redirect(root));
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        String version = PlatformHooks.PLATFORM_HELPER.getVersion();
        context.getSource().sendSuccess(() ->
                Component.empty()
                    .append(Component.literal("VillagerConfig").withStyle(ChatFormatting.BOLD))
                    .append("\n")
                    .append(Component.literal("Version: "))
                    .append(Component.literal(version).withStyle(version.contains("beta") ? ChatFormatting.GOLD : ChatFormatting.GREEN))
                    .append("\n")
                    .append(Component.literal("Author: "))
                    .append(Component.literal("DrexHD").withStyle(ChatFormatting.AQUA))
            , false);
        return 1;
    }

}