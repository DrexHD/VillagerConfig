package me.drex.villagerconfig.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.drex.villagerconfig.common.platform.PlatformHooks;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.Commands.literal;

public class VillagerConfigCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> root = dispatcher.register(
                literal("villagerconfig")
                        .requires(src -> src.hasPermission(2))
                        .then(GenerateCommand.builder())
                        .then(ReloadCommand.builder())
                        .executes(VillagerConfigCommand::execute)
        );
        dispatcher.register(literal("vc").requires(src -> src.hasPermission(2)).executes(VillagerConfigCommand::execute).redirect(root));
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        String version = PlatformHooks.PLATFORM_HELPER.getVersion();
        context.getSource().sendSuccess(() ->
        Component.empty()
                .append(
                        Component.literal("VillagerConfig").withStyle(ChatFormatting.BOLD)
                )
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