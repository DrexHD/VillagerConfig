package me.drex.villagerfix.mixin.types;

import me.drex.villagerfix.villager.types.JsonSerializer;
import net.minecraft.item.map.MapIcon;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffers;
import net.minecraft.world.gen.feature.StructureFeature;
import org.json.JSONObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TradeOffers.SellMapFactory.class)
public class SellMapMixin implements JsonSerializer {

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
    private StructureFeature<?> structure;

    @Shadow
    @Final
    private MapIcon.Type iconType;

    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", getType());
        jsonObject.put("price", this.price);
        jsonObject.put("structure", Registry.STRUCTURE_FEATURE.getId(this.structure).toString());
        jsonObject.put("iconType", this.iconType);
        jsonObject.put("max_uses", this.maxUses);
        jsonObject.put("experience", this.experience);
        return jsonObject;
    }

    @Override
    public String getType() {
        return "SellMap";
    }

}
