package net.nighthawkempires.souls.items;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.nighthawkempires.core.datasection.DataSection;
import net.nighthawkempires.core.datasection.FJsonSection;
import net.nighthawkempires.core.util.StringUtil;
import net.nighthawkempires.souls.SoulsPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import static org.bukkit.ChatColor.*;
import static org.bukkit.ChatColor.translateAlternateColorCodes;

public class SoulArmor implements SoulItem {

    private String name;

    private Material material;

    private int cost;

    private List<String> lore;

    private ConcurrentMap<Enchantment, Integer> enchantments;

    public SoulArmor(DataSection data) {
        this.name = data.getString("name");

        this.lore = Lists.newArrayList();
        if (data.isSet("lore"))
            this.lore = data.getStringList("lore");

        this.cost = data.getInt("cost");

        this.material = Material.valueOf(data.getString("material"));

        this.enchantments = Maps.newConcurrentMap();
        if (data.isSet("enchantments")) {
            Map<String, Object> map = data.getMap("enchantments");
            for (String s : map.keySet()) {
                this.enchantments.put(Enchantment.getByKey(NamespacedKey.minecraft(name)), Integer.parseInt(map.get(name).toString()));
            }
        }
    }

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public int getCost() {
        return cost;
    }

    public List<String> getLore() {
        return lore;
    }

    public ConcurrentMap<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    public ItemStack toShopItemStack() {
        ItemStack itemStack = new ItemStack(material);
        itemStack.addUnsafeEnchantment(SoulsPlugin.SOUL_ARMOR_ENCHANTMENT, 1);
        for (Enchantment enchantment : this.enchantments.keySet()) {
            itemStack.addUnsafeEnchantment(enchantment, this.enchantments.get(enchantment));
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        List<String> lore = Lists.newArrayList();

        lore.add(GRAY + SoulsPlugin.SOUL_ARMOR_ENCHANTMENT.getName() + " " + StringUtil.toRoman(1));

        lore.add(DARK_GRAY + "Price" + GRAY + ": " + GOLD + getCost() + GREEN + " Player Souls");
        lore.add(" ");

        for (String s : this.lore) {
            lore.add(translateAlternateColorCodes('&', s
                    .replaceAll("%REDUCE%", String.valueOf(SoulsPlugin.getSoulsConfig().damageReducedPercent))
                    .replaceAll("%INCREASE%", String.valueOf(SoulsPlugin.getSoulsConfig().damageIncreasedPercent))));
        }

        itemMeta.setLore(lore);
        itemMeta.setDisplayName(translateAlternateColorCodes('&', name));

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public ItemStack toItemStack() {
        ItemStack itemStack = new ItemStack(material);
        itemStack.addUnsafeEnchantment(SoulsPlugin.SOUL_ARMOR_ENCHANTMENT, 1);
        for (Enchantment enchantment : this.enchantments.keySet()) {
            itemStack.addUnsafeEnchantment(enchantment, this.enchantments.get(enchantment));
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        List<String> lore = Lists.newArrayList();

        lore.add(GRAY + SoulsPlugin.SOUL_ARMOR_ENCHANTMENT.getName() + " " + StringUtil.toRoman(1));

        for (String s : this.lore) {
            lore.add(translateAlternateColorCodes('&', s
                    .replaceAll("%REDUCE%", String.valueOf(SoulsPlugin.getSoulsConfig().damageReducedPercent))
                    .replaceAll("%INCREASE%", String.valueOf(SoulsPlugin.getSoulsConfig().damageIncreasedPercent))));
        }

        itemMeta.setLore(lore);
        itemMeta.setDisplayName(translateAlternateColorCodes('&', name));

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public SoulItemType getItemType() {
        return SoulItemType.ARMOR;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();

        map.put("name", this.name);

        map.put("material", this.material.name());

        map.put("cost", this.cost);

        map.put("lore", this.lore);

        if (!this.enchantments.isEmpty()) {
            Map<String, Object> enchantmentMap = Maps.newHashMap();
            for (Enchantment enchantment : this.enchantments.keySet()) {
                enchantmentMap.put(enchantment.getKey().getKey(), this.enchantments.get(enchantment));
            }

            map.put("enchantments", enchantmentMap);
        }
        return map;
    }

    public static boolean isSoulArmor(ItemStack itemStack) {
        return itemStack.getEnchantments().containsKey(SoulsPlugin.SOUL_ARMOR_ENCHANTMENT);
    }

    public static SoulArmor getDefaultArmor() {
        Map<String, Object> map = Maps.newHashMap();

        map.put("name", "&8&l&oSoul Helmet");

        map.put("material", Material.DIAMOND_HELMET.name());

        map.put("cost", 35);

        map.put("lore", Lists.newArrayList(
                "Reduces %REDUCE%% of damage,",
                "Increases damage dealt by %INCREASE%%",
                "upon wearing a full set."
        ));
        return new SoulArmor(new FJsonSection(map));
    }

    public static boolean hasFullSet(Player player) {
        for (ItemStack itemStack : player.getInventory().getArmorContents()) {
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                if (!isSoulArmor(itemStack)) return false;
            } else return false;
        }
        return true;
    }
}