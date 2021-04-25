package net.nighthawkempires.souls.command;

import net.nighthawkempires.core.CorePlugin;
import net.nighthawkempires.core.lang.Messages;
import net.nighthawkempires.souls.user.UserModel;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static net.nighthawkempires.core.CorePlugin.*;
import static net.nighthawkempires.souls.SoulsPlugin.getUserRegistry;
import static net.nighthawkempires.core.lang.Messages.*;
import static org.bukkit.ChatColor.*;
import static org.bukkit.ChatColor.RED;

public class SoulsCommand implements CommandExecutor {

    private String[] help = new String[] {
            getMessages().getMessage(CHAT_HEADER),
            DARK_GRAY + "Command" + GRAY + ": Souls    " + DARK_GRAY + "    [Optional], <Required>",
            getMessages().getMessage(CHAT_FOOTER),
            getMessages().getCommand("souls", "Display your souls"),
            getMessages().getCommand("souls", "help", "Show this help menu"),
            getMessages().getCommand("souls", "<player>", "Display a player's souls"),
            getMessages().getCommand("souls", "reset [player] <mob|player|all>", "Reset a player's soul balance"),
            getMessages().getCommand("souls", "set [player] <mob|player> <amount>", "Set a player's soul balance"),
            getMessages().getCommand("souls", "add [player] <mob|player> <amount>", "Add to a player's soul balance"),
            getMessages().getCommand("souls", "remove [player] <mob|player> <amount>", "Remove from a player's soul balance"),
            getMessages().getMessage(CHAT_FOOTER),
    };

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            UserModel userModel = getUserRegistry().getUser(player.getUniqueId());

            if (!player.hasPermission("ne.souls") && !player.hasPermission("ne.souls.admin")) {
                player.sendMessage(getMessages().getChatTag(NO_PERMS));
                return true;
            }

            switch (args.length) {
                case 0:
                    player.sendMessage(getSoulBalance(player.getUniqueId()));
                    return true;
                case 1:
                    switch (args[0].toLowerCase()) {
                        case "help":
                            if (!player.hasPermission("ne.souls.admin")) {
                                player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
                                return true;
                            }

                            player.sendMessage(help);
                            return true;
                        default:
                            String name = args[0];
                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
                            if (!getUserRegistry().userExists(offlinePlayer.getUniqueId())) {
                                player.sendMessage(getMessages().getChatTag(PLAYER_NOT_FOUND));
                                return true;
                            }

                            player.sendMessage(getSoulBalance(offlinePlayer.getUniqueId()));
                            return true;
                    }
                case 2:
                    if (!player.hasPermission("ne.souls.admin")) {
                        player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
                        return true;
                    }

                    switch (args[0].toLowerCase()) {
                        case "reset":
                            switch (args[1].toLowerCase()) {
                                case "mob":
                                    userModel.setMobSouls(0);
                                    player.sendMessage(getMessages().getChatMessage(GRAY + "You have reset your Mob Souls."));
                                    return true;
                                case "player":
                                    userModel.setPlayerSouls(0);
                                    player.sendMessage(getMessages().getChatMessage(GRAY + "You have reset your Player Souls."));
                                    return true;
                                case "all":
                                    userModel.setMobSouls(0);
                                    userModel.setPlayerSouls(0);
                                    player.sendMessage(getMessages().getChatMessage(GRAY + "You have reset your Mob and Player Souls."));
                                    return true;
                                default:
                                    player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
                                    return true;
                            }
                        default:
                            player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
                            return true;
                    }
                case 3:
                    if (!player.hasPermission("ne.souls.admin")) {
                        player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
                        return true;
                    }

                    switch (args[0].toLowerCase()) {
                        case "reset":
                            String name = args[1];
                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
                            if (!getUserRegistry().userExists(offlinePlayer.getUniqueId())) {
                                player.sendMessage(getMessages().getChatTag(PLAYER_NOT_FOUND));
                                return true;
                            }

                            userModel = getUserRegistry().getUser(offlinePlayer.getUniqueId());

                            switch (args[2].toLowerCase()) {
                                case "mob":
                                    userModel.setMobSouls(0);
                                    player.sendMessage(getMessages().getChatMessage(GRAY + "You have reset " + GREEN + offlinePlayer.getName() + "'s"
                                            + " Mob Souls."));
                                    if (offlinePlayer.isOnline()) {
                                        offlinePlayer.getPlayer().sendMessage(getMessages().getChatMessage(GRAY + "Your Mob Souls have been reset."));
                                        return true;
                                    }
                                    return true;
                                case "player":
                                    userModel.setPlayerSouls(0);
                                    player.sendMessage(getMessages().getChatMessage(GRAY + "You have reset " + GREEN + offlinePlayer.getName() + "'s"
                                            + " Player Souls."));
                                    if (offlinePlayer.isOnline()) {
                                        offlinePlayer.getPlayer().sendMessage(getMessages().getChatMessage(GRAY + "Your Player Souls have been reset."));
                                        return true;
                                    }
                                    return true;
                                case "all":
                                    userModel.setMobSouls(0);
                                    userModel.setPlayerSouls(0);
                                    player.sendMessage(getMessages().getChatMessage(GRAY + "You have reset " + GREEN + offlinePlayer.getName() + "'s"
                                            + " Mob and Player Souls."));
                                    if (offlinePlayer.isOnline()) {
                                        offlinePlayer.getPlayer().sendMessage(getMessages().getChatMessage(GRAY + "Your Mob and Player Souls have been reset."));
                                        return true;
                                    }
                                    return true;
                                default:
                                    player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
                                    return true;
                            }
                        case "set":
                            switch (args[1].toLowerCase()) {
                                case "mob":
                                    if (!NumberUtils.isDigits(args[2])) {
                                        player.sendMessage(getMessages().getChatMessage(GRAY + "The amount must be a valid number."));
                                        return true;
                                    }

                                    int amount = Integer.parseInt(args[2]);
                                    userModel.setMobSouls(amount);
                                    player.sendMessage(getMessages().getChatMessage(GRAY + "You have set your Mob Souls to " + GOLD + amount + GRAY + "."));
                                    return true;
                                case "player":
                                    if (!NumberUtils.isDigits(args[2])) {
                                        player.sendMessage(getMessages().getChatMessage(GRAY + "The amount must be a valid number."));
                                        return true;
                                    }

                                    amount = Integer.parseInt(args[2]);
                                    userModel.setPlayerSouls(amount);
                                    player.sendMessage(getMessages().getChatMessage(GRAY + "You have set your Player Souls to " + GOLD + amount + GRAY + "."));
                                    return true;
                                default:
                                    player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
                                    return true;
                            }
                        case "add":
                            switch (args[1].toLowerCase()) {
                                case "mob":
                                    if (!NumberUtils.isDigits(args[2])) {
                                        player.sendMessage(getMessages().getChatMessage(GRAY + "The amount must be a valid number."));
                                        return true;
                                    }

                                    int amount = Integer.parseInt(args[2]);
                                    userModel.setMobSouls(userModel.getMobSouls() + amount);
                                    player.sendMessage(getMessages().getChatMessage(GRAY + "You have added " + GOLD + amount + GRAY + " souls to your Mob Souls Balance."));
                                    return true;
                                case "player":
                                    if (!NumberUtils.isDigits(args[2])) {
                                        player.sendMessage(getMessages().getChatMessage(GRAY + "The amount must be a valid number."));
                                        return true;
                                    }

                                    amount = Integer.parseInt(args[2]);
                                    userModel.setPlayerSouls(userModel.getPlayerSouls() + amount);
                                    player.sendMessage(getMessages().getChatMessage(GRAY + "You have added " + GOLD + amount + GRAY + " souls to your Player Souls Balance."));
                                    return true;
                                default:
                                    player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
                                    return true;
                            }
                        case "remove":
                            switch (args[1].toLowerCase()) {
                                case "mob":
                                    if (!NumberUtils.isDigits(args[2])) {
                                        player.sendMessage(getMessages().getChatMessage(GRAY + "The amount must be a valid number."));
                                        return true;
                                    }

                                    int amount = Integer.parseInt(args[2]);
                                    userModel.setMobSouls(userModel.getMobSouls() - amount);
                                    player.sendMessage(getMessages().getChatMessage(GRAY + "You have removed " + GOLD + amount + GRAY + " souls from your Mob Souls Balance."));
                                    return true;
                                case "player":
                                    if (!NumberUtils.isDigits(args[2])) {
                                        player.sendMessage(getMessages().getChatMessage(GRAY + "The amount must be a valid number."));
                                        return true;
                                    }

                                    amount = Integer.parseInt(args[2]);
                                    userModel.setPlayerSouls(userModel.getPlayerSouls() - amount);
                                    player.sendMessage(getMessages().getChatMessage(GRAY + "You have removed " + GOLD + amount + GRAY + " souls from your Player Souls Balance."));
                                    return true;
                                default:
                                    player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
                                    return true;
                            }
                        default:
                            player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
                            return true;
                    }
                case 4:
                    if (!player.hasPermission("ne.souls.admin")) {
                        player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
                        return true;
                    }

                    switch (args[0].toLowerCase()) {
                        case "set":
                            String name = args[1];
                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
                            if (!getUserRegistry().userExists(offlinePlayer.getUniqueId())) {
                                player.sendMessage(getMessages().getChatTag(PLAYER_NOT_FOUND));
                                return true;
                            }

                            userModel = getUserRegistry().getUser(offlinePlayer.getUniqueId());

                            switch (args[2].toLowerCase()) {
                                case "mob":
                                    if (!NumberUtils.isDigits(args[3])) {
                                        player.sendMessage(getMessages().getChatMessage(GRAY + "The amount must be a valid number."));
                                        return true;
                                    }

                                    int amount = Integer.parseInt(args[3]);
                                    userModel.setMobSouls(amount);
                                    player.sendMessage(getMessages().getChatMessage(GRAY + "You have set " + GREEN + offlinePlayer.getName() + "'s" + GRAY +
                                            " Mob Souls to " + GOLD + amount + GRAY + "."));
                                    if (offlinePlayer.isOnline()) {
                                        offlinePlayer.getPlayer().sendMessage(getMessages().getChatMessage(GRAY + "Your Mob Souls Balance has been set to " +
                                                GOLD + amount + GRAY + "."));
                                    }
                                    return true;
                                case "player":
                                    if (!NumberUtils.isDigits(args[3])) {
                                        player.sendMessage(getMessages().getChatMessage(GRAY + "The amount must be a valid number."));
                                        return true;
                                    }

                                    amount = Integer.parseInt(args[3]);
                                    userModel.setPlayerSouls(amount);
                                    player.sendMessage(getMessages().getChatMessage(GRAY + "You have set " + GREEN + offlinePlayer.getName() + "'s" + GRAY +
                                            " Player Souls to " + GOLD + amount + GRAY + "."));
                                    if (offlinePlayer.isOnline()) {
                                        offlinePlayer.getPlayer().sendMessage(getMessages().getChatMessage(GRAY + "Your Player Souls Balance has been set to " +
                                                GOLD + String.valueOf(amount) + GRAY + "."));
                                    }
                                    return true;
                                default:
                                    player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
                                    return true;
                            }
                        case "add":
                            name = args[1];
                            offlinePlayer = Bukkit.getOfflinePlayer(name);
                            if (!getUserRegistry().userExists(offlinePlayer.getUniqueId())) {
                                player.sendMessage(getMessages().getChatTag(PLAYER_NOT_FOUND));
                                return true;
                            }

                            userModel = getUserRegistry().getUser(offlinePlayer.getUniqueId());

                            switch (args[2].toLowerCase()) {
                                case "mob":
                                    if (!NumberUtils.isDigits(args[3])) {
                                        player.sendMessage(getMessages().getChatMessage(GRAY + "The amount must be a valid number."));
                                        return true;
                                    }

                                    int amount = Integer.parseInt(args[3]);
                                    userModel.setMobSouls(userModel.getMobSouls() + amount);
                                    player.sendMessage(getMessages().getChatMessage(GRAY + "You have added " + GOLD + amount + GRAY + " souls to " +
                                            GREEN + offlinePlayer.getPlayer().getName() + "'s" + GRAY + " Mob Souls Balance."));
                                    if (offlinePlayer.isOnline()) {
                                        offlinePlayer.getPlayer().sendMessage(getMessages().getChatMessage(GOLD + String.valueOf(amount)
                                                + GRAY + " souls have been added to your Mob Souls Balance."));
                                    }
                                    return true;
                                case "player":
                                    if (!NumberUtils.isDigits(args[3])) {
                                        player.sendMessage(getMessages().getChatMessage(GRAY + "The amount must be a valid number."));
                                        return true;
                                    }

                                    amount = Integer.parseInt(args[3]);
                                    userModel.setPlayerSouls(userModel.getPlayerSouls() + amount);
                                    player.sendMessage(getMessages().getChatMessage(GRAY + "You have added " + GOLD + amount + GRAY + " souls to " +
                                            GREEN + offlinePlayer.getPlayer().getName() + "'s" + GRAY + " Player Souls Balance."));
                                    if (offlinePlayer.isOnline()) {
                                        offlinePlayer.getPlayer().sendMessage(getMessages().getChatMessage(GOLD + String.valueOf(amount)
                                                + GRAY + " souls have been added to your Player Souls Balance."));
                                    }
                                    return true;
                                default:
                                    player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
                                    return true;
                            }
                        case "remove":
                            name = args[1];
                            offlinePlayer = Bukkit.getOfflinePlayer(name);
                            if (!getUserRegistry().userExists(offlinePlayer.getUniqueId())) {
                                player.sendMessage(getMessages().getChatTag(PLAYER_NOT_FOUND));
                                return true;
                            }

                            userModel = getUserRegistry().getUser(offlinePlayer.getUniqueId());

                            switch (args[2].toLowerCase()) {
                                case "mob":
                                    if (!NumberUtils.isDigits(args[3])) {
                                        player.sendMessage(getMessages().getChatMessage(GRAY + "The amount must be a valid number."));
                                        return true;
                                    }

                                    int amount = Integer.parseInt(args[3]);
                                    userModel.setMobSouls(userModel.getMobSouls() - amount);
                                    player.sendMessage(getMessages().getChatMessage(GRAY + "You have removed " + GOLD + amount + GRAY + " souls from " +
                                            GREEN + offlinePlayer.getPlayer().getName() + "'s" + GRAY +" Mob Souls Balance."));
                                    if (offlinePlayer.isOnline()) {
                                        offlinePlayer.getPlayer().sendMessage(getMessages().getChatMessage(GOLD + String.valueOf(amount)
                                                + GRAY + " souls have been removed from your Mob Souls Balance."));
                                    }
                                    return true;
                                case "player":
                                    if (!NumberUtils.isDigits(args[3])) {
                                        player.sendMessage(getMessages().getChatMessage(GRAY + "The amount must be a valid number."));
                                        return true;
                                    }

                                    amount = Integer.parseInt(args[3]);
                                    userModel.setPlayerSouls(userModel.getPlayerSouls() - amount);
                                    player.sendMessage(getMessages().getChatMessage(GRAY + "You have removed " + GOLD + amount + GRAY + " souls from " +
                                            GREEN + offlinePlayer.getPlayer().getName() + "'s" + GRAY +" Player Souls Balance."));
                                    if (offlinePlayer.isOnline()) {
                                        offlinePlayer.getPlayer().sendMessage(getMessages().getChatMessage(GOLD + String.valueOf(amount)
                                                + GRAY + " souls have been removed from your Player Souls Balance."));
                                    }
                                    return true;
                                default:
                                    player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
                                    return true;
                            }
                        default:
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

    private String[] getSoulBalance(UUID uuid) {
        UserModel userModel = getUserRegistry().getUser(uuid);

        return new String[]{
                getMessages().getMessage(CHAT_HEADER),
                DARK_GRAY + "Soul Balance" + GRAY + ": " + GREEN + Bukkit.getOfflinePlayer(uuid).getName(),
                getMessages().getMessage(CHAT_FOOTER),
                DARK_GRAY + "Mob Souls" + GRAY + ": " + GOLD + userModel.getMobSouls(),
                DARK_GRAY + "Player Souls" + GRAY + ": " + GOLD + userModel.getPlayerSouls(),
                getMessages().getMessage(CHAT_FOOTER),
        };
    }
}
