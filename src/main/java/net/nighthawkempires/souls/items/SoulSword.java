package net.nighthawkempires.souls.items;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.nighthawkempires.core.CorePlugin;
import net.nighthawkempires.core.datasection.DataSection;
import net.nighthawkempires.core.datasection.FJsonSection;
import net.nighthawkempires.core.util.IntegerUtil;
import net.nighthawkempires.core.util.StringUtil;
import net.nighthawkempires.souls.SoulsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import static net.nighthawkempires.core.CorePlugin.*;
import static net.nighthawkempires.souls.SoulsPlugin.CONTAINED_SOULS_KEY;
import static org.bukkit.ChatColor.*;

public class SoulSword implements SoulItem {

    private String name;

    private Material material;

    private int cost;
    private int tier;
    private int maxSouls;
    private int removesPerKill;

    private List<String> lore;

    private ConcurrentMap<Enchantment, Integer> enchantments;

    public SoulSword(DataSection data) {
        this.name = data.getString("name");

        this.material = Material.valueOf(data.getString("material"));

        this.cost = data.getInt("cost");
        this.tier = data.getInt("tier");
        this.maxSouls = data.getInt("max_souls");
        this.removesPerKill = data.getInt("removes_per_kill");

        this.lore = Lists.newArrayList();
        if (data.isSet("lore"))
            this.lore = data.getStringList("lore");

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

    public int getMaxSouls() {
        return maxSouls;
    }

    public int getRemovesPerKill() {
        return removesPerKill;
    }

    public int getTier() {
        return tier;
    }

    public List<String> getLore() {
        return lore;
    }

    public ConcurrentMap<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    public ItemStack toShopItemStack() {
        ItemStack itemStack = new ItemStack(material);
        itemStack.addUnsafeEnchantment(SoulsPlugin.SOUL_SWORD_ENCHANTMENT, tier);
        for (Enchantment enchantment : this.enchantments.keySet()) {
            itemStack.addUnsafeEnchantment(enchantment, this.enchantments.get(enchantment));
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        List<String> lore = Lists.newArrayList();

        lore.add(GRAY + SoulsPlugin.SOUL_SWORD_ENCHANTMENT.getName() + " " + StringUtil.toRoman(tier));

        lore.add(DARK_GRAY + "Price" + GRAY + ": " + GOLD + getCost() + GREEN + " Player Souls");
        lore.add(" ");

        for (String s : this.lore) {
            lore.add(translateAlternateColorCodes('&', s
                    .replaceAll("%REMOVES%", String.valueOf(removesPerKill))
                    .replaceAll("%MAX%", String.valueOf(maxSouls))));
        }

        lore.add(" ");
        lore.add(GRAY + SoulsPlugin.SOUL_SWORD_ENCHANTMENT.getName() + " " + StringUtil.toRoman(tier));

        itemMeta.setLore(lore);
        itemMeta.setDisplayName(translateAlternateColorCodes('&', name));

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public ItemStack toItemStack() {
        ItemStack itemStack = new ItemStack(material);
        itemStack.addUnsafeEnchantment(SoulsPlugin.SOUL_SWORD_ENCHANTMENT, tier);
        for (Enchantment enchantment : this.enchantments.keySet()) {
            itemStack.addUnsafeEnchantment(enchantment, this.enchantments.get(enchantment));
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        List<String> lore = Lists.newArrayList();

        lore.add(GRAY + SoulsPlugin.SOUL_SWORD_ENCHANTMENT.getName() + " " + StringUtil.toRoman(tier));

        for (String s : this.lore) {
            lore.add(translateAlternateColorCodes('&', s
                    .replaceAll("%REMOVES%", String.valueOf(removesPerKill))
                    .replaceAll("%MAX%", String.valueOf(maxSouls))));
        }

        itemMeta.getPersistentDataContainer().set(CONTAINED_SOULS_KEY, PersistentDataType.INTEGER, 0);

        itemMeta.setLore(lore);
        itemMeta.setDisplayName(translateAlternateColorCodes('&', name));

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public SoulItemType getItemType() {
        return SoulItemType.SWORD;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();

        map.put("name", this.name);

        map.put("material", this.material.name());

        map.put("cost", this.cost);
        map.put("tier", this.tier);
        map.put("max_souls", this.maxSouls);
        map.put("removes_per_kill", this.removesPerKill);

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

    public static boolean isSoulSword(ItemStack itemStack) {
        return itemStack.getEnchantments().containsKey(SoulsPlugin.SOUL_SWORD_ENCHANTMENT);
    }

    public static int getPlayerSouls(ItemStack itemStack) {
        if (isSoulSword(itemStack)) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta.getPersistentDataContainer().has(CONTAINED_SOULS_KEY, PersistentDataType.INTEGER)) {
                return itemMeta.getPersistentDataContainer().get(CONTAINED_SOULS_KEY, PersistentDataType.INTEGER);
            }
            return -1;
        }
        return -1;
    }

    public static ItemStack setPlayerSouls(ItemStack itemStack, int souls) {
        if (isSoulSword(itemStack)) {
            SoulSword soulSword = SoulsPlugin.getSoulsConfig().getSoulSword(itemStack);

            if (soulSword == null) return null;
            if (souls > soulSword.getMaxSouls()) souls = soulSword.getMaxSouls();

            ItemMeta itemMeta = itemStack.getItemMeta();
            List<String> lore = Lists.newArrayList();
            lore.addAll(itemMeta.getLore());
            //lore.set(0, DARK_GRAY + "Contains " + GOLD + souls + GREEN + " Player Souls");

            itemMeta.getPersistentDataContainer().set(CONTAINED_SOULS_KEY, PersistentDataType.INTEGER, souls);

            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);

            return itemStack;
        }
        return null;
    }

    public static ItemStack addPlayerSouls(ItemStack itemStack, int souls) {
        if (isSoulSword(itemStack)) {
            setPlayerSouls(itemStack, getPlayerSouls(itemStack) + souls);
        }
        return null;
    }

    public static ItemStack removePlayerSouls(ItemStack itemStack, int souls) {
        if (isSoulSword(itemStack)) {
            setPlayerSouls(itemStack, getPlayerSouls(itemStack) - souls);
        }
        return null;
    }

    public static SoulSword getDefaultSword() {
        Map<String, Object> map = Maps.newHashMap();

        map.put("name", "&8&l&oSoul &7&l&oSword &c&l&oTier I");

        map.put("material", Material.IRON_SWORD.name());

        map.put("cost", 20);
        map.put("tier", 1);
        map.put("max_souls", 20);
        map.put("removes_per_kill", 5);

        map.put("lore", Lists.newArrayList(
                "Holds up to %MAX% Player Souls,",
                "Removes %REMOVES% Player Souls",
                "upon killing a player."
        ));
        return new SoulSword(new FJsonSection(map));
    }
}