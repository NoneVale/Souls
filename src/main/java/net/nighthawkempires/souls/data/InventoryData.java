package net.nighthawkempires.souls.data;

import com.google.common.collect.Lists;
import net.nighthawkempires.souls.SoulsPlugin;
import net.nighthawkempires.souls.items.MutatedSoul;
import net.nighthawkempires.souls.items.SoulArmor;
import net.nighthawkempires.souls.items.SoulSword;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class InventoryData {

    private List<Inventory> soulShopInventories;

    public InventoryData() {
        this.soulShopInventories = Lists.newArrayList();
    }

    public List<Inventory> getSoulShopInventories() {
        return soulShopInventories;
    }

    public Inventory getSoulShopInventory() {
        List<MutatedSoul> mutatedSouls = Lists.newArrayList(SoulsPlugin.getSoulsConfig().mutatedSouls);
        List<SoulArmor> soulArmors = Lists.newArrayList(SoulsPlugin.getSoulsConfig().soulArmors);
        List<SoulSword> soulSwords = Lists.newArrayList(SoulsPlugin.getSoulsConfig().soulSwords);

        List<String> layout = SoulsPlugin.getSoulsConfig().soulShopLayout;

        int size = 9;
        int contentSize = layout.size();
        while (size < contentSize) {
            size += 9;
        }

        Inventory inventory = Bukkit.createInventory(null, size, "Soul SHop");

        for (int i = 0; i < layout.size(); i++) {
            String s = layout.get(i);
            switch (s) {
                case "SOULSWORD":
                    inventory.setItem(i, soulSwords.get(0).toShopItemStack());
                    soulSwords.remove(0);
                    break;
                case "SOULARMOR":
                    inventory.setItem(i, soulArmors.get(0).toShopItemStack());
                    soulArmors.remove(0);
                    break;
                case "MUTATEDSOUL":
                    inventory.setItem(i, mutatedSouls.get(0).toShopItemStack());
                    mutatedSouls.remove(0);
                    break;
                default:
                    inventory.setItem(i, new ItemStack(Material.valueOf(s.toUpperCase())));
                    break;
            }
        }

        this.soulShopInventories.add(inventory);
        return inventory;
    }
}