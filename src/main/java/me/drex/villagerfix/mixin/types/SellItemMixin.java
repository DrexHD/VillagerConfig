package me.drex.villagerfix.mixin.types;

import me.drex.villagerfix.util.Helper;
import me.drex.villagerfix.villager.types.JsonSerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffers;
import org.json.JSONObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TradeOffers.SellItemFactory.class)
public class SellItemMixin implements JsonSerializer {

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
    private float multiplier;
    @Shadow
    @Final
    private int count;

    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", getType());
        jsonObject.put("sell", Helper.toName(this.sell.getItem()));
        jsonObject.put("price", this.price);
        jsonObject.put("count", this.count);
        jsonObject.put("max_uses", this.maxUses);
        jsonObject.put("experience", this.experience);
        jsonObject.put("multiplier", this.multiplier);
        return jsonObject;
    }

    @Override
    public String getType() {
        return "SellItem";
    }

}
