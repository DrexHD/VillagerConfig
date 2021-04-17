package me.drex.villagerfix.mixin.types;

import me.drex.villagerfix.villager.types.JsonSerializer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffers;
import org.json.JSONObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TradeOffers.SellSuspiciousStewFactory.class)
public class SellSuspiciousStewMixin implements JsonSerializer {

    @Shadow
    @Final
    private StatusEffect effect;

    @Shadow
    @Final
    private int duration;

    @Shadow
    @Final
    private int experience;

    @Shadow
    @Final
    private float multiplier;

    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", getType());
        jsonObject.put("effect", Registry.STATUS_EFFECT.getId(this.effect));
        jsonObject.put("duration", this.duration);
        jsonObject.put("experience", this.experience);
        jsonObject.put("multiplier", this.multiplier);
        return jsonObject;
    }

    @Override
    public String getType() {
        return "SellSuspiciousStew";
    }

}
