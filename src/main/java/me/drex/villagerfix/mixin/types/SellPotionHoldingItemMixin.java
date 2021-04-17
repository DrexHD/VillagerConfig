package me.drex.villagerfix.mixin.types;

import me.drex.villagerfix.villager.types.JsonSerializer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffers;
import org.json.JSONObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TradeOffers.SellPotionHoldingItemFactory.class)
public class SellPotionHoldingItemMixin implements JsonSerializer {

    @Shadow
    @Final
    private int price;
    @Shadow
    @Final
    private int maxUses;
    @Shadow
    @Final
    private int experience;
    @Shadow
    @Final
    private ItemStack sell;
    @Shadow
    @Final
    private Item secondBuy;
    @Shadow
    @Final
    private float priceMultiplier;

    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", getType());
        jsonObject.put("sell", JsonSerializer.parseItemStack(this.sell));
        jsonObject.put("price", this.price);
        jsonObject.put("max_uses", this.maxUses);
        jsonObject.put("experience", this.experience);
        jsonObject.put("secondBuy", JsonSerializer.parseItemStack(new ItemStack(this.secondBuy)));
        jsonObject.put("priceMultiplier", this.priceMultiplier);
        return jsonObject;
    }

    @Override
    public String getType() {
        return "SellPotionHoldingItem";
    }

}
