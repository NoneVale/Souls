package net.nighthawkempires.souls.command;

import net.nighthawkempires.core.CorePlugin;
import net.nighthawkempires.core.lang.Messages;
import net.nighthawkempires.souls.SoulsPlugin;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static net.nighthawkempires.core.CorePlugin.*;
import static net.nighthawkempires.core.lang.Messages.*;
import static net.nighthawkempires.souls.messages.SoulsMessages.*;
import static org.bukkit.ChatColor.*;

public class SoulChestCommand implements CommandExecutor {

    private String[] help = new String[] {
            getMessages().getMessage(CHAT_HEADER),
            DARK_GRAY + "Command" + GRAY + ": Soul Chest    " + DARK_GRAY + "    [Optional], <Required>",
            getMessages().getMessage(CHAT_FOOTER),
            getMessages().getCommand("soulchest", "give <player> [amount]", "Give a player a Soul Chest"),
            getMessages().getMessage(CHAT_FOOTER),
    };

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission("ne.souls.admin")) {
                player.sendMessage(getMessages().getChatTag(NO_PERMS));
                return true;
            }

            switch (args.length) {
                case 0:
                    player.sendMessage(help);
                    return true;
                case 2:
                    if (args[0].toLowerCase().equals("give")) {
                        String name = args[1];
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
                        if (!offlinePlayer.isOnline()) {
                            player.sendMessage(getMessages().getChatTag(PLAYER_NOT_ONLINE));
                            return true;
                        }

                        Player target = offlinePlayer.getPlayer();

                        target.getInventory().addItem(SoulsPlugin.getSoulsConfig().soulChest.toItemStack());
                        target.sendMessage(getMessages().getChatTag(RECIEVE_SOULCHEST)
                                .replaceAll("%AMOUNT%", String.valueOf(1)));
                        player.sendMessage(getMessages().getChatTag(GIVE_SOULCHEST)
                                .replaceAll("%AMOUNT%", String.valueOf(1))
                                .replaceAll("%PLAYER%", target.getName()));
                        return true;
                    } else {
                        player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
                        return true;
                    }
                case 3:
                    if (args[0].toLowerCase().equals("give")) {
                        String name = args[1];
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
                        if (!offlinePlayer.isOnline()) {
                            player.sendMessage(getMessages().getChatTag(PLAYER_NOT_ONLINE));
                            return true;
                        }

                        Player target = offlinePlayer.getPlayer();

                        if (!NumberUtils.isDigits(args[2])) {
                            player.sendMessage(getMessages().getChatMessage(GRAY + "The amount must be a valid number."));
                            return true;
                        }

                        int amount = Integer.parseInt(args[2]);

                        ItemStack itemStack = SoulsPlugin.getSoulsConfig().soulChest.toItemStack();
                        itemStack.setAmount(amount);

                        player.getInventory().addItem(itemStack);
                        target.sendMessage(getMessages().getChatMessage(GRAY + "You have been given " + ChatColor.GOLD + amount
                                + GRAY + " soul chests."));
                        player.sendMessage(getMessages().getChatMessage(GRAY + "You have given " + ChatColor.GOLD + amount
                                + GRAY + " soul chests to " + GREEN + target.getName() + GRAY + "."));
                        return true;
                    } else {
                        player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
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
