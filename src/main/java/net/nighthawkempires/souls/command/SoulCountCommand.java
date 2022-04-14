package net.nighthawkempires.souls.command;

import net.nighthawkempires.core.CorePlugin;
import net.nighthawkempires.core.lang.Messages;
import net.nighthawkempires.souls.items.SoulSword;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static net.nighthawkempires.core.CorePlugin.*;
import static net.nighthawkempires.core.lang.Messages.*;
import static org.bukkit.ChatColor.*;

public class SoulCountCommand implements CommandExecutor {

    public SoulCountCommand() {
        getCommandManager().registerCommands("soulcount", new String[] {
                "ne.souls"
        });
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (!player.hasPermission("ne.souls")) {
                player.sendMessage(getMessages().getChatTag(NO_PERMS));
                return true;
            }

            if (args.length == 0) {
                ItemStack itemStack = player.getInventory().getItemInMainHand();
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    if (SoulSword.isSoulSword(itemStack)) {
                        int souls = SoulSword.getPlayerSouls(itemStack);
                        player.sendMessage(getMessages().getChatMessage(GRAY + "The equipped soul sword contains " + GOLD + souls + GRAY + " Player Souls."));
                        return true;
                    }
                }

                player.sendMessage(getMessages().getChatMessage(GRAY + "You are not holding a Soul Sword in your hand."));
                return true;
            }
            player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
            return true;
        }
        return false;
    }
}
