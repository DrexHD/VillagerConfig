package me.drex.villagerconfig.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.drex.villagerconfig.VillagerConfig;
import me.drex.villagerconfig.util.TradeProvider;
import net.minecraft.SharedConstants;
import net.minecraft.data.DataGenerator;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.IOException;
import java.nio.file.Path;

public class GenerateCommand {

    private static final Path GENERATED = VillagerConfig.DATA_PATH.resolve("generated");

    public static void register(LiteralArgumentBuilder<ServerCommandSource> command) {
        LiteralArgumentBuilder<ServerCommandSource> generate = LiteralArgumentBuilder.literal("generate");
        generate.executes(GenerateCommand::execute);
        command.then(generate);
    }

    private static int execute(CommandContext<ServerCommandSource> context) {
        DataGenerator dataGenerator = new DataGenerator(GENERATED, SharedConstants.getGameVersion(), true);
        DataGenerator.Pack tradesPack = dataGenerator.createVanillaPack(true);
        tradesPack.addProvider(output -> new TradeProvider(output, context.getSource().getServer().getRegistryManager()));
        try {
            dataGenerator.run();
            context.getSource().sendFeedback(Text.literal("Successfully generated trade data to " + GENERATED).formatted(Formatting.GREEN), false);
            return 1;
        } catch (IOException e) {
            context.getSource().sendFeedback(Text.literal("An error occurred, please look into the console for more information.").formatted(Formatting.RED), false);
            VillagerConfig.LOGGER.error("An error occurred, while generating trade data", e);
            return 0;
        }
    }

}
