package net.nighthawkempires.souls.listener;

import com.google.common.collect.Lists;
import net.nighthawkempires.core.CorePlugin;
import net.nighthawkempires.core.cooldown.Cooldown;
import net.nighthawkempires.core.entity.Entities;
import net.nighthawkempires.souls.SoulsPlugin;
import net.nighthawkempires.souls.items.MutatedSoul;
import net.nighthawkempires.souls.items.SoulArmor;
import net.nighthawkempires.souls.items.SoulChest;
import net.nighthawkempires.souls.items.SoulSword;
import net.nighthawkempires.souls.user.UserModel;
import net.nighthawkempires.souls.util.ParticleUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.TimeUnit;

import static net.nighthawkempires.core.CorePlugin.*;
import static net.nighthawkempires.souls.SoulsPlugin.*;
import static net.nighthawkempires.souls.SoulsPlugin.getUserRegistry;
import static net.nighthawkempires.souls.items.SoulChest.*;
import static net.nighthawkempires.souls.items.SoulSword.*;
import static org.bukkit.ChatColor.*;

public class PlayerListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (SoulArmor.hasFullSet(player)) {
                double percent = (double) getSoulsConfig().damageReducedPercent / 100;
                double damage = event.getDamage();
                double remove = damage * percent;
                double newDamage = damage - remove;
                event.setDamage(newDamage);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();

            if (SoulArmor.hasFullSet(player)) {
                double percent = (double) getSoulsConfig().damageIncreasedPercent / 100;
                double damage = event.getDamage();
                double add = damage * percent;
                double newDamage = damage + add;
                event.setDamage(newDamage);
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player player = event.getEntity().getKiller();
            UserModel userModel = getUserRegistry().getUser(player.getUniqueId());

            if (Entities.isAggressive(event.getEntityType()))return;

            if (Entities.isPassive(event.getEntityType())) {
                int droppedSouls = getSoulsConfig().entitySoulDrops.get(Entities.PassiveEntities.valueOf(event.getEntityType().name()));

                userModel.setMobSouls(userModel.getMobSouls() + (droppedSouls * userModel.getLevel()));

                if (userModel.getMobSouls() >= 25) {
                    userModel.setPlayerSouls(userModel.getPlayerSouls() + 1);
                    userModel.setMobSouls(userModel.getMobSouls() - 25);
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player killed = event.getEntity();

        if (killed.getKiller() != null) {
            Player player = killed.getKiller();

            ItemStack itemStack = player.getInventory().getItemInMainHand();
            if (itemStack != null && itemStack.getType() != null) {
                if (isSoulSword(itemStack)) {
                    SoulSword soulSword = getSoulsConfig().getSoulSword(itemStack);

                    if (soulSword != null) {
                        int removes = soulSword.getRemovesPerKill();

                        UserModel userModel = getUserRegistry().getUser(killed.getUniqueId());
                        int killedHas = userModel.getPlayerSouls();

                        if (killedHas < removes) removes = killedHas;

                        ItemStack newItemStack = setPlayerSouls(itemStack, getPlayerSouls(itemStack) + removes);
                        player.getInventory().setItemInMainHand(newItemStack);
                        userModel.removePlayerSouls(removes);
                        ParticleUtil.coneEffect(player);
                        player.sendMessage(getMessages().getChatMessage(GRAY + "You have harvested " + GOLD + removes + GRAY + " Player Souls by killing "
                                + GREEN + killed.getName() + GRAY + "."));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UserModel userModel = getUserRegistry().getUser(player.getUniqueId());

        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack != null && itemStack.getType() != Material.AIR) {
            if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) || (event.getAction() == Action.RIGHT_CLICK_AIR)) {
                if (isSoulChest(itemStack)) {
                    userModel.addPlayerSouls(getSoulsConfig().soulChest.getContainedSouls());
                    player.sendMessage(getMessages().getChatMessage(GRAY + "You have redeemed "
                            + GOLD + getSoulsConfig().soulChest.getContainedSouls() + GRAY + " Player Souls."));
                    player.getInventory().getItemInMainHand().setAmount(itemStack.getAmount() - 1);
                    player.updateInventory();
                    event.setCancelled(true);
                } else if (MutatedSoul.isMutatedSoul(itemStack)) {
                    if (getCooldowns().hasActiveCooldown(player.getUniqueId(), "mutated_soul")) {
                        Cooldown cooldown = getCooldowns().getActive(player.getUniqueId(), "mutated_soul");
                        player.sendMessage(getMessages().getChatMessage(GRAY + "I'm sorry, but you have " + GOLD + cooldown.timeLeft()
                                + GRAY + " before you can use another Mutated Soul."));
                        return;
                    }

                    MutatedSoul mutatedSoul = getSoulsConfig().getMutatedSoul(itemStack);
                    int cooldown = mutatedSoul.getCooldown();
                    int amplifier = mutatedSoul.getAmplifier() - 1;
                    int duration = mutatedSoul.getDuration();
                    PotionEffectType effectType = mutatedSoul.getEffectType();
                    String name = translateAlternateColorCodes('&', mutatedSoul.getName());

                    getCooldowns().addCooldown(new Cooldown(player.getUniqueId(), "mutated_soul",
                            System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(cooldown)));

                    player.getActivePotionEffects().stream()
                            .filter(effect -> effect.getType().equals(effectType) && effect.getAmplifier() <= effect.getAmplifier())
                            .forEach(effect -> player.removePotionEffect(effect.getType()));
                    player.addPotionEffect(new PotionEffect(effectType, duration * 1200, amplifier, false, false, true));

                    player.sendMessage(getMessages().getChatMessage(GRAY + "You have activated Mutated Soul " + name + GRAY + "."));
                    player.getInventory().getItemInMainHand().setAmount(itemStack.getAmount() - 1);
                    player.updateInventory();
                }
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack != null || itemStack.getType() != Material.AIR) {
            if (SoulChest.isSoulChest(itemStack)) {
                event.setCancelled(true);
            }
        }
    }
}