package net.nighthawkempires.souls.items;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.nighthawkempires.core.datasection.DataSection;
import net.nighthawkempires.core.datasection.FJsonSection;
import net.nighthawkempires.core.util.StringUtil;
import net.nighthawkempires.souls.SoulsPlugin;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;

import static net.nighthawkempires.core.util.StringUtil.*;
import static net.nighthawkempires.souls.SoulsPlugin.*;
import static org.bukkit.ChatColor.*;

public class MutatedSoul implements SoulItem {

    private String name;

    private Material material;

    private PotionEffectType effectType;

    private int amplifier;
    private int cost;
    private int duration;
    private int cooldown;

    private List<String> lore;

    public MutatedSoul(DataSection data) {
        this.name = data.getString("name");

        this.material = Material.valueOf(data.getString("material"));

        this.effectType = PotionEffectType.getByName(data.getString("effect_type"));

        this.amplifier = data.getInt("amplifier");
        this.cost = data.getInt("cost");
        this.duration = data.getInt("duration");
        this.cooldown = data.getInt("cooldown");

        this.lore = Lists.newArrayList();
        if (data.isSet("lore"))
            this.lore = data.getStringList("lore");
    }

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public PotionEffectType getEffectType() {
        return effectType;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public int getCost() {
        return cost;
    }

    public int getDuration() {
        return duration;
    }

    public int getCooldown() {
        return cooldown;
    }

    public List<String> getLore() {
        return lore;
    }

    public ItemStack toShopItemStack() {
        ItemStack itemStack = new ItemStack(material);
        itemStack.addUnsafeEnchantment(MUTATED_SOUL_ENCHANTMENT, 1);

        ItemMeta itemMeta = itemStack.getItemMeta();

        List<String> lore = Lists.newArrayList();

        lore.add(GRAY + MUTATED_SOUL_ENCHANTMENT.getName() + " " + toRoman(1));

        lore.add(DARK_GRAY + "Price" + GRAY + ": " + GOLD + getCost() + GREEN + " Player Souls");
        lore.add(" ");

        for (String s : this.lore) {
            lore.add(translateAlternateColorCodes('&', s
                    .replaceAll("%EFFECT%", StringUtil.beautify(getEffectType().getName()))
                    .replaceAll("%AMPLIFIER%", toRoman(getAmplifier()))
                    .replaceAll("%DURATION%", String.valueOf(getDuration()))
                    .replaceAll("%COOLDOWN%", String.valueOf(getCooldown()))));
        }

        itemMeta.getPersistentDataContainer().set(EFFECT_TYPE_KEY, PersistentDataType.STRING, getEffectType().getName());

        itemMeta.setLore(lore);
        itemMeta.setDisplayName(translateAlternateColorCodes('&', name));

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public ItemStack toItemStack() {
        ItemStack itemStack = new ItemStack(material);
        itemStack.addUnsafeEnchantment(MUTATED_SOUL_ENCHANTMENT, 1);

        ItemMeta itemMeta = itemStack.getItemMeta();

        List<String> lore = Lists.newArrayList();

        lore.add(GRAY + MUTATED_SOUL_ENCHANTMENT.getName() + " " + toRoman(1));

        for (String s : this.lore) {
            lore.add(translateAlternateColorCodes('&', s
                    .replaceAll("%EFFECT%", StringUtil.beautify(getEffectType().getName()))
                    .replaceAll("%AMPLIFIER%", toRoman(getAmplifier()))
                    .replaceAll("%DURATION%", String.valueOf(getDuration()))
                    .replaceAll("%COOLDOWN%", String.valueOf(getCooldown()))));
        }

        itemMeta.getPersistentDataContainer().set(EFFECT_TYPE_KEY, PersistentDataType.STRING, getEffectType().getName());

        itemMeta.setLore(lore);
        itemMeta.setDisplayName(translateAlternateColorCodes('&', name));

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public SoulItemType getItemType() {
        return SoulItemType.MUTATED;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();

        map.put("name", this.name);

        map.put("material", this.material.name());

        map.put("effect_type", effectType.getName());

        map.put("amplifier", this.amplifier);
        map.put("cost", this.cost);
        map.put("duration", this.duration);
        map.put("cooldown", this.cooldown);

        map.put("lore", this.lore);
        return map;
    }

    public static boolean isMutatedSoul(ItemStack itemStack) {
        return itemStack.getEnchantments().containsKey(MUTATED_SOUL_ENCHANTMENT);
    }

    public static MutatedSoul getDefaultSoul() {
        Map<String, Object> map = Maps.newHashMap();

        map.put("name", "&a&l&oJump &7&l&oMutated Soul");

        map.put("material", Material.QUARTZ.name());

        map.put("effect_type", PotionEffectType.JUMP.getName());

        map.put("amplifier", 2);
        map.put("cost", 15);
        map.put("duration", 5);
        map.put("cooldown", 15);

        map.put("lore", Lists.newArrayList(
                "Grants %EFFECT% %AMPLIFIER% effect",
                "for %DURATION% minutes with",
                "a %COOLDOWN% minute cooldown."
        ));

        return new MutatedSoul(new FJsonSection(map));
    }
}