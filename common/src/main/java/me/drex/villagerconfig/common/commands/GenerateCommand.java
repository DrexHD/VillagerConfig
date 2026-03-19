package me.drex.villagerconfig.common.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.drex.villagerconfig.common.VillagerConfig;
import me.drex.villagerconfig.common.platform.PlatformHooks;
import me.drex.villagerconfig.common.util.TradeProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.Component;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class GenerateCommand {

    private static final Path GENERATED = PlatformHooks.PLATFORM_HELPER.getModConfigDir().resolve("generated");

    public static LiteralArgumentBuilder<CommandSourceStack> builder() {
        return Commands.literal("generate")
            .executes(ctx -> execute(ctx.getSource()));
    }

    private static int execute(CommandSourceStack src) {
        DataGenerator dataGenerator = new DataGenerator.Uncached(GENERATED);
        DataGenerator.PackGenerator tradesPack = dataGenerator.getVanillaPack(true);

        tradesPack.addProvider(packOutput -> new TradeProvider(packOutput, CompletableFuture.completedFuture(src.getServer().registryAccess())));
        try {
            dataGenerator.run();
            src.sendSuccess(() -> Component.literal("Successfully generated trade data to " + GENERATED).withStyle(ChatFormatting.GREEN), false);
            return 1;
        } catch (Throwable e) {
            src.sendFailure(Component.literal("An error occurred, please look into the console for more information."));
            VillagerConfig.LOGGER.error("An error occurred, while generating trade data", e);
            return 0;
        }
    }

}
