package me.drex.villagerfix.mixin.types;

import me.drex.villagerfix.util.Helper;
import me.drex.villagerfix.villager.types.JsonSerializer;
import net.minecraft.item.Item;
import net.minecraft.village.TradeOffers;
import org.json.JSONObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TradeOffers.BuyForOneEmeraldFactory.class)
public class BuyForOneEmeraldMixin implements JsonSerializer {

    @Shadow
    @Final
    private Item buy;
    @Shadow
    @Final
    private int price;
    @Shadow
    @Final
    private int maxUses;
    @Shadow
    @Final
    private int experience;

    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", getType());
        jsonObject.put("buy", Helper.toName(this.buy));
        jsonObject.put("price", this.price);
        jsonObject.put("max_uses", this.maxUses);
        jsonObject.put("experience", this.experience);
        return jsonObject;
    }

    @Override
    public String getType() {
        return "BuyForOneEmerald";
    }

}
