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

    public SoulsRankupCommand() {
        getCommandManager().registerCommands("soulsrankup", new String[] {
                "ne.souls"
        });
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            UserModel userModel = getUserRegistry().getUser(player.getUniqueId());

            if (!player.hasPermission("ne.souls")) {
                player.sendMessage(getMessages().getChatTag(NO_PERMS));
                return true;
            }

            if (args.length == 0) {
                int nextLevel = userModel.getLevel() + 1;

                if (nextLevel > getSoulsConfig().highestLevel()) {
                    player.sendMessage(getMessages().getChatMessage(GRAY + "You have already reached the max Soul Level."));
                    return true;
                }

                int needed = getSoulsConfig().getRequiredSouls(nextLevel);

                if (userModel.getPlayerSouls() < needed) {
                    int need = needed - userModel.getPlayerSouls();
                    player.sendMessage(getMessages().getChatMessage(GRAY + "You need " + GOLD + need + GRAY
                            + " more Player Souls to be able to rankup."));
                    return true;
                }

                userModel.removePlayerSouls(needed);
                userModel.setLevel(nextLevel);
                player.sendMessage(getMessages().getChatMessage(GRAY + "You are now Soul Level " + GOLD + nextLevel + GRAY + "."));
                return true;
            }
            player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
            return true;
        }
        return false;
    }
}
