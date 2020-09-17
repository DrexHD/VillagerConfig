package me.drex.villagerfix.mixin;

import me.drex.villagerfix.VillagerFix;
import me.drex.villagerfix.util.ItemHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.Item;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(AbstractTraderEntity.class)
public abstract class AbstractTraderEntityMixin extends PassiveEntity {

    protected AbstractTraderEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }


    /**
     * @author Drex
     * @reason blacklist trades
     */
    @Overwrite
    protected void fillRecipesFromPool(TraderOfferList recipeList, TradeOffers.Factory[] pool, int count) {
        outer:
        for (TradeOffers.Factory factory : pool) {
            TradeOffer tradeOffer = factory.create(this, this.random);
            for (String string : VillagerFix.INSTANCE.config().blacklisted_trades) {
                Item item = ItemHelper.toItem(string);
                if (item == null) {
                    VillagerFix.LOG.error("Unable to parse " + string + " to item.");
                    continue;
                }
                if (tradeOffer == null ||
                        item == tradeOffer.getOriginalFirstBuyItem().getItem() ||
                        item == tradeOffer.getSecondBuyItem().getItem() ||
                        item == tradeOffer.getSellItem().getItem()) {
                continue outer;
                }
            }
            recipeList.add(tradeOffer);
        }
        while (recipeList.size() > count) {
            recipeList.remove(this.random.nextInt(recipeList.size()));
        }
    }
}
