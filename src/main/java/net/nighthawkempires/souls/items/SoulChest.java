package net.nighthawkempires.souls.items;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.nighthawkempires.core.datasection.DataSection;
import net.nighthawkempires.souls.SoulsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public class SoulChest implements SoulItem {

    private String name;
    private List<String> lore;
    private int containedSouls;

    public SoulChest(DataSection data) {
        this.name = data.getString("name");
        this.lore = data.getStringList("lore");
        this.containedSouls = data.getInt("contained_souls");
    }

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }

    public int getContainedSouls() {
        return containedSouls;
    }

    public ItemStack toItemStack() {
        ItemStack itemStack = new ItemStack(Material.ENDER_CHEST);
        itemStack.addUnsafeEnchantment(SoulsPlugin.SOUL_CHEST_ENCHANTMENT, 1);

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        List<String> lore = Lists.newArrayList();
        for (String s : this.lore) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s
                    .replaceAll("%AMOUNT%", String.valueOf(containedSouls))));
        }
        itemMeta.setLore(lore);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public SoulItemType getItemType() {
        return SoulItemType.CHEST;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();

        map.put("name", name);
        map.put("lore", lore);
        map.put("contained_souls", containedSouls);

        return map;
    }

    public static boolean isSoulChest(ItemStack itemStack) {
        return itemStack.getEnchantments().containsKey(SoulsPlugin.SOUL_CHEST_ENCHANTMENT);
    }
}