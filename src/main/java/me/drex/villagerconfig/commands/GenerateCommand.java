package me.drex.villagerconfig.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.drex.villagerconfig.VillagerConfig;
import me.drex.villagerconfig.util.TradeProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.Component;

import java.nio.file.Path;

public class GenerateCommand {

    private static final Path GENERATED = VillagerConfig.DATA_PATH.resolve("generated");

    public static LiteralArgumentBuilder<CommandSourceStack> builder() {
        return Commands.literal("generate")
            .executes(ctx -> execute(ctx.getSource()));
    }

    private static int execute(CommandSourceStack src) {
        DataGenerator dataGenerator = new DataGenerator(GENERATED, SharedConstants.getCurrentVersion(), true);
        DataGenerator.PackGenerator tradesPack = dataGenerator.getVanillaPack(true);
        tradesPack.addProvider(TradeProvider::new);
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
