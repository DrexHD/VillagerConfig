package me.drex.villagerfix.mixin.types;

import me.drex.villagerfix.util.Helper;
import me.drex.villagerfix.villager.types.JsonSerializer;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerType;
import org.json.JSONObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(TradeOffers.TypeAwareBuyForOneEmeraldFactory.class)
public class TypeAwareBuyForOneEmeraldMixin implements JsonSerializer {

    @Shadow
    @Final
    private int maxUses;
    @Shadow
    @Final
    private int experience;
    @Shadow
    @Final
    private Map<VillagerType, Item> map;
    @Shadow
    @Final
    private int count;

    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", getType());
        JSONObject map = new JSONObject();
        for (Map.Entry<VillagerType, Item> entry : this.map.entrySet()) {
            map.put(Registry.VILLAGER_TYPE.getId(entry.getKey()).toString(), Helper.toName(entry.getValue()));
        }
        jsonObject.put("map", map);
        jsonObject.put("count", this.count);
        jsonObject.put("max_uses", this.maxUses);
        jsonObject.put("experience", this.experience);
        return jsonObject;
    }

    @Override
    public String getType() {
        return "TypeAwareBuyForOneEmerald";
    }

}
