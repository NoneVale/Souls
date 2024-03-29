package net.nighthawkempires.souls.command;

import net.nighthawkempires.souls.items.SoulSword;
import net.nighthawkempires.souls.user.UserModel;
import net.nighthawkempires.souls.util.ParticleUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static net.nighthawkempires.souls.SoulsPlugin.getUserRegistry;
import static net.nighthawkempires.core.CorePlugin.*;
import static net.nighthawkempires.core.lang.Messages.*;
import static net.nighthawkempires.souls.items.SoulSword.setPlayerSouls;
import static net.nighthawkempires.souls.messages.SoulsMessages.*;

public class ExtractSoulsCommand implements CommandExecutor {

    public ExtractSoulsCommand() {
        getCommandManager().registerCommands("extractsouls", new String[] {
                "ne.souls"
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            UserModel userModel = getUserRegistry().getUser(player.getUniqueId());

            if (!player.hasPermission("ne.souls")) {
                player.sendMessage(getMessages().getChatTag(NO_PERMS));
                return true;
            }

            switch (args.length) {
                case 0:
                    ItemStack itemStack = player.getInventory().getItemInMainHand();
                    if (itemStack != null && itemStack.getType() != Material.AIR) {
                        if (!SoulSword.isSoulSword(itemStack)) {
                            player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "The equipped item is not a soul sword."));
                            return true;
                        }

                        int souls = SoulSword.getPlayerSouls(itemStack);

                        if (souls == 0) {
                            player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "The equipped soul sword does not contain any Player Souls."));
                            return true;
                        }

                        userModel.addPlayerSouls(souls);
                        ItemStack newItemStack = setPlayerSouls(itemStack, 0);
                        player.getInventory().setItemInMainHand(newItemStack);
                        player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "You have extracted "
                                + ChatColor.GOLD + souls + ChatColor.GRAY + " Player Souls from the equipped soul sword."));
                        ParticleUtil.coneEffect(player);
                        return true;
                    } else {
                        player.sendMessage(getMessages().getChatTag(NOT_SOUL_SWORD));
                        return true;
                    }
                default:
                    player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
                    return true;
            }
        }
        return false;
    }
}