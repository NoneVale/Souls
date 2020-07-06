package net.nighthawkempires.souls.enchantment;

import net.nighthawkempires.core.enchantment.CustomEnchantmentWrapper;
import net.nighthawkempires.souls.SoulsPlugin;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class MutatedSoulWrapper extends CustomEnchantmentWrapper {

    public MutatedSoulWrapper() {
        super(SoulsPlugin.getPlugin(), "mutated_soul");
    }

    public String getName() {
        return "Mutated Soul";
    }

    public int getMaxLevel() {
        return 2;
    }

    public int getStartLevel() {
        return 1;
    }

    public EnchantmentTarget getItemTarget() {
        return null;
    }

    public boolean isTreasure() {
        return false;
    }

    public boolean isCursed() {
        return false;
    }

    public boolean conflictsWith(Enchantment enchantment) {
        return false;
    }

    public boolean canEnchantItem(ItemStack itemStack) {
        return false;
    }
}
