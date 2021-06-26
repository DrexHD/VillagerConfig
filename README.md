# VillagerFix

## Setup

Run `gradlew build` and grab `villagerfix-<version>.jar` from `\build\libs`

## API
Villagerfix provides an API for other developers to implement methods for (de)serialization of custom Trade Factories.

Add this to your build.gradle (make sure to specifie the [villagerfix version](https://jitpack.io/#DrexHD/VillagerFix))
```gradle
dependencies {
    //Villagerfix
    modImplementation "com.github.DrexHD:VillagerFix:${vf_version}"
    //Json
    implementation "org.json:json:20210307"
}

repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}
``` 
Make sure your custom TradeOfferFactory has methods for (de)serialization from and to json 
```java
public class RandomInputOffer implements TradeOffers.Factory {

    private final List<ItemStack> input;
    private final ItemStack reward;

    public RandomInputOffer(List<ItemStack> input, ItemStack reward) {
        this.input = input;
        this.reward = reward;
    }

    public static JSONObject toJson(VillagerTrades.ItemListing itemListing) {
        JSONObject jsonObject = new JSONObject();
        if (itemListing instanceof RandomInputOffer offer) {
            JSONArray input = new JSONArray();
            for (ItemStack itemStack : offer.getInput()) {
                input.put(Helper.parseItemStack(itemStack));
            }
            jsonObject.put("input", input);
            jsonObject.put("reward", Helper.parseItemStack(offer.getReward()));
        }
        return jsonObject;
    }

    public static RandomInputOffer fromJson(JSONObject jsonObject) {
        ItemStack reward = parseItemStack(jsonObject.getJSONObject("reward"));
        JSONArray input = jsonObject.getJSONArray("input");
        List<ItemStack> list = new ArrayList<>();
        for (Object o : input) {
            list.add(parseItemStack((JSONObject) o));
        }
        return new RandomInputOffer(list, second, reward);
    }

    public List<ItemStack> getInput() { return input; }
    public ItemStack getReward() { return reward; }
    
    @Nullable
    @Override
    public MerchantOffer getOffer(Entity entity, Random random) {
        return new MerchantOffer(input.get(random.nextInt(input.size())), ItemStack.EMPTY, reward, 20, 5, 0.05F);
    }
}

```
Create a class that implements `VillagerFixAPI` and register your custom Trade Factories:
```java
public class VillagerFixEntry implements VillagerFixAPI {

    @Override
    public void onInitialize(TradeFactoryStorage storage) {
      //register TradesFactories
      storage.registerTradeFactory(RandomInputOffer.class, new TradeEntry(RandomInputOffer::fromJson, RandomInputOffer::toJson));
    }

}
```
Add it as a mod entry to your `fabric.mod` file
```json
"entrypoints": {
  "villagerfix": [
    "com.example.VillagerFixEntry"
  ]
}
```
