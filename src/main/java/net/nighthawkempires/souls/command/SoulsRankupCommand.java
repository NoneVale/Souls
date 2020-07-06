package net.nighthawkempires.souls.command;

import net.nighthawkempires.core.CorePlugin;
import net.nighthawkempires.core.lang.Messages;
import net.nighthawkempires.souls.SoulsPlugin;
import net.nighthawkempires.souls.user.UserModel;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.nighthawkempires.core.CorePlugin.*;
import static net.nighthawkempires.core.lang.Messages.*;
import static net.nighthawkempires.souls.SoulsPlugin.*;
import static net.nighthawkempires.souls.SoulsPlugin.getUserRegistry;
import static org.bukkit.ChatColor.*;

public class SoulsRankupCommand implements CommandExecutor {

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
                    int nextLvel = userModel.getLevel() + 1;

                    if (nextLvel > getSoulsConfig().highestLevel()) {
                        player.sendMessage(getMessages().getChatMessage(GRAY + "You have already reached the max Soul Level."));
                        return true;
                    }

                    int needed = getSoulsConfig().getRequiredSouls(nextLvel);

                    if (userModel.getPlayerSouls() < needed) {
                        int need = needed - userModel.getPlayerSouls();
                        player.sendMessage(getMessages().getChatMessage(GRAY + "You need " + GOLD + need + GRAY
                                + " more Player Souls to be able to rankup."));
                        return true;
                    }

                    userModel.removePlayerSouls(needed);
                    userModel.setLevel(nextLvel);
                    player.sendMessage(getMessages().getChatMessage(GRAY + "You are now Soul Level " + GOLD + nextLvel + GRAY + "."));
                    return true;
                default:
                    player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
                    return true;
            }
        }
        return false;
    }
}
