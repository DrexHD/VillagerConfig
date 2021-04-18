package me.drex.villagerfix.mixin.types;

import me.drex.villagerfix.util.Helper;
import me.drex.villagerfix.villager.types.JsonSerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffers;
import org.json.JSONObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TradeOffers.SellEnchantedToolFactory.class)
public class SellEnchantedToolMixin implements JsonSerializer {

    @Shadow
    @Final
    private int maxUses;
    @Shadow
    @Final
    private int experience;
    @Shadow
    @Final
    private ItemStack tool;
    @Shadow
    @Final
    private int basePrice;
    @Shadow
    @Final
    private float multiplier;

    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", getType());
        jsonObject.put("tool", Helper.toName(this.tool.getItem()));
        jsonObject.put("basePrice", this.basePrice);
        jsonObject.put("max_uses", this.maxUses);
        jsonObject.put("experience", this.experience);
        jsonObject.put("multiplier", this.multiplier);
        return jsonObject;
    }

    @Override
    public String getType() {
        return "SellEnchantedTool";
    }

}
