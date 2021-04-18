package me.drex.villagerfix.mixin.types;

import me.drex.villagerfix.villager.types.JsonSerializer;
import net.minecraft.village.TradeOffers;
import org.json.JSONObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TradeOffers.EnchantBookFactory.class)
public class EnchantBookMixin implements JsonSerializer {

    @Shadow
    @Final
    private int experience;

    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", getType());
        jsonObject.put("experience", this.experience);
        return jsonObject;
    }

    @Override
    public String getType() {
        return "EnchantBook";
    }

}
