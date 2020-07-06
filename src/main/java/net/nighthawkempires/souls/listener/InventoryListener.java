package net.nighthawkempires.souls.listener;

import net.nighthawkempires.core.CorePlugin;
import net.nighthawkempires.souls.items.MutatedSoul;
import net.nighthawkempires.souls.items.SoulArmor;
import net.nighthawkempires.souls.items.SoulSword;
import net.nighthawkempires.souls.user.UserModel;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import static net.nighthawkempires.core.CorePlugin.*;
import static net.nighthawkempires.souls.SoulsPlugin.*;
import static net.nighthawkempires.souls.SoulsPlugin.getUserRegistry;
import static org.bukkit.ChatColor.*;

public class InventoryListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            UserModel userModel = getUserRegistry().getUser(player.getUniqueId());
            if (getInventoryData().getSoulShopInventories().contains(event.getView().getTopInventory())) {
                event.setCancelled(true);
                if (getInventoryData().getSoulShopInventories().contains(event.getClickedInventory())) {
                    if (event.getCurrentItem() != null) {
                        ItemStack itemStack = event.getCurrentItem();

                        if (SoulSword.isSoulSword(itemStack)) {
                            SoulSword soulSword = getSoulsConfig().getSoulSword(itemStack);

                            if (soulSword == null) {
                                player.sendMessage(getMessages().getChatMessage(RED + "Please report this error to a member of the Staff Team."));
                                return;
                            }

                            int cost = soulSword.getCost();

                            if (userModel.getPlayerSouls() < cost) {
                                int need = cost - userModel.getPlayerSouls();
                                player.sendMessage(getMessages().getChatMessage(GRAY + "You need " + GOLD + need + GRAY
                                        + " more Player Souls to be able to buy this item."));
                                return;
                            }

                            player.getInventory().addItem(soulSword.toItemStack());
                            userModel.removePlayerSouls(cost);
                            String name = translateAlternateColorCodes('&', soulSword.getName());
                            player.sendMessage(getMessages().getChatMessage(GRAY + "You bought " + name + GRAY + " for " + GOLD
                                    + cost + GRAY + " Player Souls."));
                        } else if (SoulArmor.isSoulArmor(itemStack)) {
                            SoulArmor soulArmor = getSoulsConfig().getSoulArmor(itemStack);

                            if (soulArmor == null) {
                                player.sendMessage(getMessages().getChatMessage(RED + "Please report this error to a member of the Staff Team."));
                                return;
                            }

                            int cost = soulArmor.getCost();

                            if (userModel.getPlayerSouls() < cost) {
                                int need = cost - userModel.getPlayerSouls();
                                player.sendMessage(getMessages().getChatMessage(GRAY + "You need " + GOLD + need + GRAY
                                        + " more Player Souls to be able to buy this item."));
                                return;
                            }

                            player.getInventory().addItem(soulArmor.toItemStack());
                            userModel.removePlayerSouls(cost);
                            String name = translateAlternateColorCodes('&', soulArmor.getName());
                            player.sendMessage(getMessages().getChatMessage(GRAY + "You bought " + name + GRAY + " for " + GOLD
                                    + cost + GRAY + " Player Souls."));
                        } else if (MutatedSoul.isMutatedSoul(itemStack)) {
                            MutatedSoul mutatedSoul = getSoulsConfig().getMutatedSoul(itemStack);

                            if (mutatedSoul == null) {
                                player.sendMessage(getMessages().getChatMessage(RED + "Please report this error to a member of the Staff Team."));
                                return;
                            }

                            int cost = mutatedSoul.getCost();

                            if (userModel.getPlayerSouls() < cost) {
                                int need = cost - userModel.getPlayerSouls();
                                player.sendMessage(getMessages().getChatMessage(GRAY + "You need " + GOLD + need + GRAY
                                        + " more Player Souls to be able to buy this item."));
                                return;
                            }

                            player.getInventory().addItem(mutatedSoul.toItemStack());
                            userModel.removePlayerSouls(cost);
                            String name = translateAlternateColorCodes('&', mutatedSoul.getName());
                            player.sendMessage(getMessages().getChatMessage(GRAY + "You bought " + name + GRAY + " for " + GOLD
                                    + cost + GRAY + " Player Souls."));
                        }
                    }
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (getInventoryData().getSoulShopInventories().contains(event.getInventory())) {
            getInventoryData().getSoulShopInventories().remove(event.getInventory());
        }
    }
}
