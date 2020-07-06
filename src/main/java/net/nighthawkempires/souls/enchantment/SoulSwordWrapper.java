package net.nighthawkempires.souls.enchantment;

import net.nighthawkempires.core.enchantment.CustomEnchantmentWrapper;
import net.nighthawkempires.souls.SoulsPlugin;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

import static net.nighthawkempires.souls.SoulsPlugin.*;

public class SoulSwordWrapper extends CustomEnchantmentWrapper {

    public SoulSwordWrapper() {
        super(getPlugin(), "soul_sword");
    }

    public String getName() {
        return "Soul Sword";
    }

    public int getMaxLevel() {
        return getSoulsConfig().maxSoulSwordLevel;
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
