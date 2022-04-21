package me.drex.villagerconfig.json.behavior;

import me.drex.villagerconfig.VillagerConfig;
import me.drex.villagerconfig.util.TradeTableReporter;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class VC_EnchantBookFactory implements TradeOffers.Factory, IValidate {

    private Enchantments enchantments;
    private TradeItem[] wants;
    private float treasure_multiplier;
    private int base_price;
    private int level_price;
    private int random_base_price;
    private int random_level_price;
    final int trader_exp;
    final int max_uses;
    final boolean reward_exp;

    public VC_EnchantBookFactory(Enchantments enchantments, int treasure_multiplier, int base_price, int level_price, int random_base_price,
                                 int random_level_price, int trader_exp, int max_uses, boolean reward_exp, TradeItem[] wants) {
        this.enchantments = enchantments;
        this.treasure_multiplier = treasure_multiplier;
        this.base_price = base_price;
        this.level_price = level_price;
        this.random_base_price = random_base_price;
        this.random_level_price = random_level_price;
        this.trader_exp = trader_exp;
        this.max_uses = max_uses;
        this.reward_exp = reward_exp;
        this.wants = wants;
    }

    @Override
    public @NotNull TradeOffer create(Entity entity, AbstractRandom random) {
        TradeItem first = wants[0];
        ItemStack firstBuyItem = first.generateItem(entity, random);
        ItemStack secondBuyItem = ItemStack.EMPTY;
        LootContext.Builder builder = new LootContext.Builder((ServerWorld) entity.world).random(random);
        LootContext lootContext = builder.build(LootContextTypes.EMPTY);
        float priceMultiplier = first.price_multiplier.nextFloat(lootContext);
        if (wants.length > 1) {
            TradeItem second = wants[1];
            secondBuyItem = second.generateItem(entity, random);
        }
        List<Enchantment> enchantments = this.enchantments.getAvailableEnchantments();
        ItemStack sell;
        boolean isTreasure;
        int level;
        if (enchantments.isEmpty()) {
            level = 0;
            isTreasure = false;
            sell = new ItemStack(Items.ENCHANTED_BOOK);
            VillagerConfig.LOGGER.warn("No eligible enchantment for {} found", this.getClass().getSimpleName());
        } else {
            Enchantment enchantment = enchantments.get(random.nextInt(enchantments.size()));
            level = MathHelper.nextInt(random, enchantment.getMinLevel(), enchantment.getMaxLevel());
            sell = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, level));
            isTreasure = enchantment.isTreasure();
        }
        int priceCount = this.base_price + random.nextInt(this.random_base_price + level * this.random_level_price) + this.level_price * level;
        if (isTreasure) {
            priceCount *= this.treasure_multiplier;
        }
        firstBuyItem.setCount(Math.min(priceCount, firstBuyItem.getMaxCount()));
        return new TradeOffer(firstBuyItem, secondBuyItem, sell, this.max_uses, this.trader_exp, priceMultiplier);
    }

    public static class Enchantments {

        private boolean treasure;
        private Enchantment[] blacklist;
        private Enchantment.Rarity[] rarities;

        public Enchantments(boolean treasure, Enchantment[] blacklist, Enchantment.Rarity[] rarities) {
            this.treasure = treasure;
            this.blacklist = blacklist;
            this.rarities = rarities;
        }

        private List<Enchantment> getAvailableEnchantments() {
            return Registry.ENCHANTMENT.stream()
                    .filter(Enchantment::isAvailableForEnchantedBookOffer)
                    .filter(enchantment -> (this.treasure || !enchantment.isTreasure()))
                    .filter(enchantment -> !isBlacklisted(enchantment))
                    .filter(this::hasRarity)
                    .toList();
        }

        private boolean isBlacklisted(Enchantment enchantment) {
            if (blacklist == null) return false;
            for (Enchantment blackListedEnchantment : blacklist) {
                if (blackListedEnchantment.equals(enchantment)) return true;
            }
            return false;
        }

        private boolean hasRarity(Enchantment enchantment) {
            if (rarities == null || rarities.length == 0) return true;
            for (Enchantment.Rarity rarity : rarities) {
                if (enchantment.getRarity().equals(rarity)) return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Enchantments{" +
                    "treasure=" + treasure +
                    ", blacklist=" + Arrays.toString(blacklist) +
                    ", rarities=" + Arrays.toString(rarities) +
                    '}';
        }
    }

    public void validate(TradeTableReporter reporter) {
        if (wants == null) {
            reporter.error("Missing wants[]");
        } else if (wants.length == 0) {
            reporter.error("wants[] is empty");
        } else {
            if (wants.length > 2) {
                reporter.warn("wants[] contains more than two entries");
            }
            for (int i = 0; i < wants.length; i++) {
                wants[i].validate(reporter.makeChild(".wants[" + i + "]"));
            }
        }
    }

    @Override
    public String toString() {
        return "VF_EnchantBookFactory{" +
                "enchantments=" + enchantments +
                ", wants=" + Arrays.toString(wants) +
                ", treasure_multiplier=" + treasure_multiplier +
                ", base_price=" + base_price +
                ", level_price=" + level_price +
                ", random_base_price=" + random_base_price +
                ", random_level_price=" + random_level_price +
                ", trader_exp=" + trader_exp +
                ", max_uses=" + max_uses +
                ", reward_exp=" + reward_exp +
                '}';
    }
}