package net.nighthawkempires.souls.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.nighthawkempires.core.datasection.DataSection;
import net.nighthawkempires.core.datasection.FJsonSection;
import net.nighthawkempires.core.entity.Entities;
import net.nighthawkempires.core.settings.SettingsModel;
import net.nighthawkempires.souls.SoulsPlugin;
import net.nighthawkempires.souls.items.MutatedSoul;
import net.nighthawkempires.souls.items.SoulArmor;
import net.nighthawkempires.souls.items.SoulChest;
import net.nighthawkempires.souls.items.SoulSword;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;

import static net.nighthawkempires.souls.items.MutatedSoul.isMutatedSoul;

public class ConfigModel extends SettingsModel {

    public final double soulMultiplier;
    public final int maxSoulSwordLevel;

    public final int damageReducedPercent;
    public final int damageIncreasedPercent;

    public final List<MutatedSoul> mutatedSouls;
    public final List<SoulSword> soulSwords;
    public final List<SoulArmor> soulArmors;

    public final List<String> soulShopLayout;

    public final SoulChest soulChest;

    public final Map<Entities.PassiveEntities, Integer> entitySoulDrops;

    public final Map<Integer, Integer> levelMap;

    public ConfigModel() {
        soulMultiplier = 1;

        maxSoulSwordLevel = 5;

        this.damageReducedPercent = 10;
        this.damageIncreasedPercent = 10;

        this.mutatedSouls = Lists.newArrayList();
        this.mutatedSouls.add(MutatedSoul.getDefaultSoul());
        this.soulSwords = Lists.newArrayList();
        this.soulSwords.add(SoulSword.getDefaultSword());
        this.soulArmors = Lists.newArrayList();
        this.soulArmors.add(SoulArmor.getDefaultArmor());

        this.soulShopLayout = Lists.newArrayList(
                "SOULSWORD", "AIR", "SOULARMOR", "AIR", "MUTATEDSOUL"
        );

        Map<String, Object> map = Maps.newHashMap();
        map.put("name", "&5Soul Chest");
        map.put("lore", Lists.newArrayList(
                "Contains %AMOUNT% souls"
        ));
        map.put("contained_souls", 5);
        DataSection dataSection = new FJsonSection(map);
        this.soulChest = new SoulChest(dataSection);

        this.entitySoulDrops = Maps.newHashMap();
        for (Entities.PassiveEntities entity : Entities.PassiveEntities.values()) {
            this.entitySoulDrops.put(entity, 1);
        }

        this.levelMap = Maps.newHashMap();
    }

    public ConfigModel(DataSection data) {
        soulMultiplier = data.getDouble("soul_multiplier");

        maxSoulSwordLevel = data.getInt("max_soul_sword_level");

        this.damageReducedPercent = data.getInt("damage_reduced_percentage");
        this.damageIncreasedPercent = data.getInt("damage_increased_percentage");

        this.mutatedSouls = Lists.newArrayList();
        for (Map<String, Object> map : data.getMapList("mutated_souls")) {
            DataSection dataSection = new FJsonSection(map);
            this.mutatedSouls.add(new MutatedSoul(dataSection));
        }

        this.soulSwords = Lists.newArrayList();
        for (Map<String, Object> map : data.getMapList("soul_swords")) {
            DataSection dataSection = new FJsonSection(map);
            this.soulSwords.add(new SoulSword(dataSection));
        }

        this.soulArmors = Lists.newArrayList();
        for (Map<String, Object> map : data.getMapList("soul_armor")) {
            DataSection dataSection = new FJsonSection(map);
            this.soulArmors.add(new SoulArmor(dataSection));
        }

        this.soulShopLayout = data.getStringList("shop_layout");

        this.soulChest = new SoulChest(data.getSectionNullable("soul_chest"));

        this.entitySoulDrops = Maps.newHashMap();
        DataSection soulData = data.getSectionNullable("entity_soul_drops");
        for (String s : soulData.keySet()) {
            Entities.PassiveEntities entity = Entities.PassiveEntities.valueOf(s);
            this.entitySoulDrops.put(entity, soulData.getInt(s));
        }

        this.levelMap = Maps.newHashMap();
        if (data.isSet("levels")) {
            DataSection levelData = data.getSectionNullable("levels");
            for (String s : levelData.keySet()) {
                this.levelMap.put(Integer.valueOf(s), levelData.getInt(s));
            }
        }
    }

    public ImmutableList<SoulSword> getSoulSwords() {
        return ImmutableList.copyOf(soulSwords);
    }

    public SoulSword getSoulSword(ItemStack itemStack) {
        if (SoulSword.isSoulSword(itemStack)) {
            return getSoulSword(itemStack.getEnchantmentLevel(SoulsPlugin.SOUL_SWORD_ENCHANTMENT));
        }
        return null;
    }

    public SoulSword getSoulSword(int tier) {
        for (SoulSword soulSword : soulSwords) {
            if (soulSword.getTier() == tier) {
                return soulSword;
            }
        }
        return null;
    }

    public SoulArmor getSoulArmor(ItemStack itemStack) {
        if (SoulArmor.isSoulArmor(itemStack)) {
            for (SoulArmor soulArmor : soulArmors) {
                if (soulArmor.getMaterial() == itemStack.getType()) {
                    return soulArmor;
                }
            }
        }
        return null;
    }

    public MutatedSoul getMutatedSoul(ItemStack itemStack) {
        if (isMutatedSoul(itemStack)) {
            ItemMeta itemMeta = itemStack.getItemMeta();

            if (itemMeta.getPersistentDataContainer().has(SoulsPlugin.EFFECT_TYPE_KEY, PersistentDataType.STRING)) {
                PotionEffectType effectType = PotionEffectType.getByName(
                        itemMeta.getPersistentDataContainer().get(SoulsPlugin.EFFECT_TYPE_KEY, PersistentDataType.STRING)
                );

                return getMutatedSoul(effectType);
            }
        }
        return null;
    }

    public MutatedSoul getMutatedSoul(PotionEffectType effectType) {
        for (MutatedSoul mutatedSoul : mutatedSouls) {
            if (mutatedSoul.getEffectType() == effectType) {
                return mutatedSoul;
            }
        }
        return null;
    }

    public int highestLevel() {
        int highest = -1;
        for  (int i : levelMap.keySet()) {
            if (highest < i || highest == -1) highest = i;
        }
        return highest;
    }

    public int getRequiredSouls(int level) {
        if (levelMap.containsKey(level))
            return levelMap.get(level);
        return -1;
    }

    @Override
    public String getKey() {
        return "souls_config";
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();

        map.put("soul_multiplier", soulMultiplier);
        map.put("max_soul_sword_level", maxSoulSwordLevel);
        map.put("damage_reduced_percentage", damageReducedPercent);
        map.put("damage_increased_percentage", damageIncreasedPercent);

        List<Map<String, Object>> mutatedSoulMapList = Lists.newArrayList();
        for (MutatedSoul mutatedSoul : this.mutatedSouls) {
            mutatedSoulMapList.add(mutatedSoul.serialize());
        }
        map.put("mutated_souls", mutatedSoulMapList);

        List<Map<String, Object>> soulSwordsMapList = Lists.newArrayList();
        for (SoulSword soulSword : this.soulSwords) {
            soulSwordsMapList.add(soulSword.serialize());
        }
        map.put("soul_swords", soulSwordsMapList);

        List<Map<String, Object>> soulArmorMapList = Lists.newArrayList();
        for (SoulArmor soulArmor : this.soulArmors) {
            soulArmorMapList.add(soulArmor.serialize());
        }
        map.put("soul_armor", soulArmorMapList);

        map.put("shop_layout", soulShopLayout);

        map.put("soul_chest", soulChest.serialize());

        Map<String, Integer> mobSoulDropMap = Maps.newHashMap();
        for (Entities.PassiveEntities entity : this.entitySoulDrops.keySet()) {
            mobSoulDropMap.put(entity.name(), this.entitySoulDrops.get(entity));
        }
        map.put("entity_soul_drops", mobSoulDropMap);

        Map<String, Integer> levelsMap = Maps.newHashMap();
        for (int i : this.levelMap.keySet()) {
            levelsMap.put(String.valueOf(i), this.levelMap.get(i));
        }
        map.put("levels", levelsMap);

        return map;
    }
}
