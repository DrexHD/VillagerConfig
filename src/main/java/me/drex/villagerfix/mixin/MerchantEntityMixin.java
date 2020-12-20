package me.drex.villagerfix.mixin;

import com.google.common.collect.Sets;
import me.drex.villagerfix.VillagerFix;
import me.drex.villagerfix.config.ConfigEntries;
import me.drex.villagerfix.util.ItemHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.Item;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradeOffers;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Iterator;
import java.util.Set;

@Mixin(MerchantEntity.class)
public abstract class MerchantEntityMixin extends PassiveEntity {

    protected MerchantEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }


    /**
     * @author Drex
     * @reason blacklist trades
     */
    @Overwrite
    protected void fillRecipesFromPool(TradeOfferList recipeList, TradeOffers.Factory[] pool, int count) {
        Set<Integer> set = Sets.newHashSet();
        int failed = 0;
        if (pool.length > count) {
            outer:
            while (set.size() < count && failed < 100) {
                int i = this.random.nextInt(pool.length);
                TradeOffers.Factory factory = pool[i];
                TradeOffer tradeOffer = factory.create(this, this.random);
                for (String string : ConfigEntries.features.blacklistedTrades) {
                    Item item = ItemHelper.toItem(string);
                    if (item == null) {
                        VillagerFix.LOG.error("Unable to parse " + string + " to item.");
                        continue;
                    }
                    if (tradeOffer == null ||
                            item == tradeOffer.getOriginalFirstBuyItem().getItem() ||
                            item == tradeOffer.getSecondBuyItem().getItem() ||
                            item == tradeOffer.getSellItem().getItem()) {
                        failed++;
                        continue outer;
                    }
                }
                set.add(i);
            }
        } else {
            for (int i = 0; i < pool.length; ++i) {
                set.add(i);
            }
        }

        Iterator var9 = set.iterator();

        while(var9.hasNext()) {
            Integer integer = (Integer)var9.next();
            TradeOffers.Factory factory = pool[integer];
            TradeOffer tradeOffer = factory.create(this, this.random);
            if (tradeOffer != null) {
                recipeList.add(tradeOffer);
            }
        }
    }
}
