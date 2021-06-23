package me.drex.villagerfix.api;


import net.minecraft.village.TradeOffers;
import org.json.JSONObject;

import java.util.function.Function;

public record TradeEntry (
        Function<JSONObject, TradeOffers.Factory> deserialization,
        Function<TradeOffers.Factory, JSONObject> serialization) {

    public Function<JSONObject, TradeOffers.Factory> getDeserialization() {
        return deserialization;
    }

    public Function<TradeOffers.Factory, JSONObject> getSerialization() {
        return serialization;
    }

}