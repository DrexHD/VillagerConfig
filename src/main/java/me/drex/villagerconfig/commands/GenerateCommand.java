package me.drex.villagerconfig.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.drex.villagerconfig.VillagerConfig;
import me.drex.villagerconfig.util.TradeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;

public class GenerateCommand {

    private static final Path GENERATED = VillagerConfig.DATA_PATH.resolve("generated");

    public static void register(LiteralArgumentBuilder<ServerCommandSource> command) {
        LiteralArgumentBuilder<ServerCommandSource> generate = LiteralArgumentBuilder.literal("generate");
        generate.executes(GenerateCommand::execute);
        command.then(generate);
    }

    private static int execute(CommandContext<ServerCommandSource> context) {
        DataGenerator dataGenerator = new DataGenerator(GENERATED, Collections.emptyList());
        dataGenerator.addProvider(new TradeProvider(dataGenerator));
        try {
            dataGenerator.run();
            context.getSource().sendFeedback(new LiteralText("Successfully generated trade data to " + GENERATED).formatted(Formatting.GREEN), false);
            return 1;
        } catch (IOException e) {
            context.getSource().sendFeedback(new LiteralText("An error occurred, please look into the console for more information.").formatted(Formatting.RED), false);
            VillagerConfig.LOGGER.error("An error occurred, while generating trade data", e);
            return 0;
        }
    }

}
