package me.drex.villagerconfig.common.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.drex.villagerconfig.common.VillagerConfig;
import me.drex.villagerconfig.common.data.TradeTable;
import me.drex.villagerconfig.common.mixin.VillagerAccessor;
import me.drex.villagerconfig.common.util.TestMerchantMenu;
import me.drex.villagerconfig.common.util.TradeProvider;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
//? if >= 1.21.2 {
import net.minecraft.world.entity.EntitySpawnReason;
 //?}
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.*;
import net.minecraft.world.item.trading.MerchantOffers;

import java.util.OptionalInt;

public class TestCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> builder(CommandBuildContext commandBuildContext) {
        return Commands.literal("test")
            .then(Commands.literal("villager")
                .then(
                    Commands.argument("profession", ResourceArgument.resource(commandBuildContext, Registries.VILLAGER_PROFESSION))
                        .executes(context ->
                            testVillager(
                                context.getSource(),
                                /*? if >= 1.21.5 {*/ commandBuildContext.getOrThrow(VillagerType.PLAINS) /*?} else {*/ /*Holder.direct(VillagerType.PLAINS) *//*?}*/,
                                ResourceArgument.getResource(context, "profession", Registries.VILLAGER_PROFESSION),
                                -1
                            )
                        )
                        .then(
                            Commands.argument("type", ResourceArgument.resource(commandBuildContext, Registries.VILLAGER_TYPE))
                                .executes(context ->
                                    testVillager(
                                        context.getSource(),
                                        ResourceArgument.getResource(context, "type", Registries.VILLAGER_TYPE),
                                        ResourceArgument.getResource(context, "profession", Registries.VILLAGER_PROFESSION)
                                        , -1
                                    )
                                )
                                .then(
                                    Commands.argument("level", IntegerArgumentType.integer(1))
                                        .executes(context ->
                                            testVillager(
                                                context.getSource(),
                                                ResourceArgument.getResource(context, "type", Registries.VILLAGER_TYPE),
                                                ResourceArgument.getResource(context, "profession", Registries.VILLAGER_PROFESSION)
                                                , IntegerArgumentType.getInteger(context, "level")
                                            )
                                        )
                                )
                        )
                )
            ).then(
                Commands.literal(TradeProvider.WANDERING_TRADER_ID.getPath())
                    .executes(context -> testWanderingTrader(context.getSource()))
            );
    }

    private static int testVillager(CommandSourceStack source, Holder<VillagerType> villagerType, Holder.Reference<VillagerProfession> professionHolder, int level) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        Villager fakeVillager = EntityType.VILLAGER.create(source.getLevel()/*? if >= 1.21.2 {*/, EntitySpawnReason.COMMAND /*?}*/);
        assert fakeVillager != null;
        fakeVillager.setPos(player.position());
        //? if >= 1.21.5 {
        VillagerData villagerData = Villager.createDefaultVillagerData();
        villagerData = villagerData.withProfession(professionHolder).withType(villagerType);
        //?} else {
        /*VillagerData villagerData = new VillagerData(villagerType.value(), professionHolder.value(), 1);
        *///?}
        fakeVillager.setVillagerData(villagerData);

        if (level < 0) {
            TradeTable tradeTable = VillagerConfig.TRADE_MANAGER.getTrade(professionHolder.key().location());
            if (tradeTable != null) {
                level = tradeTable.maxLevel();
            } else {
                level = VillagerData.MAX_VILLAGER_LEVEL;
            }
        }

        // initialize first level
        fakeVillager.getOffers();

        for (int i = 0; i < level - 1; i++) {
            ((VillagerAccessor) fakeVillager).invokeIncreaseMerchantCareer();
        }
        openMenu(fakeVillager, fakeVillager.getVillagerData()./*? if >= 1.21.5 {*/ level() /*?} else {*/ /*getLevel() *//*?}*/, player);
        return 1;
    }

    private static int testWanderingTrader(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        WanderingTrader fakeTrader = EntityType.WANDERING_TRADER.create(source.getLevel()/*? if >= 1.21.2 {*/, EntitySpawnReason.COMMAND /*?}*/);
        fakeTrader.setPos(player.position());
        fakeTrader.getOffers();
        openMenu(fakeTrader, 1, player);
        return 1;
    }

    private static void openMenu(AbstractVillager villager, int level, ServerPlayer player) {
        OptionalInt optionalInt = player.openMenu(new SimpleMenuProvider((ix, inventory, playerx) -> new TestMerchantMenu(ix, inventory, villager), villager.getDisplayName()));
        if (optionalInt.isPresent()) {
            MerchantOffers merchantOffers = villager.getOffers();
            if (!merchantOffers.isEmpty()) {
                player.sendMerchantOffers(optionalInt.getAsInt(), merchantOffers, level, villager.getVillagerXp(), villager.showProgressBar(), villager.canRestock());
            }
        }
    }

}
