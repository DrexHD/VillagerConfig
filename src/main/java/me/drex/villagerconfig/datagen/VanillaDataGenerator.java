package me.drex.villagerconfig.datagen;

import me.drex.villagerconfig.util.TradeProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.world.flag.FeatureFlags;

public class VanillaDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider((fabricDataOutput, completableFuture) ->
            new TradeProvider(fabricDataOutput, completableFuture, false, FeatureFlags.VANILLA_SET));

    }
}
