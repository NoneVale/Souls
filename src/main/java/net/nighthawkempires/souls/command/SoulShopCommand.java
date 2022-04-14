package net.nighthawkempires.souls.command;

import net.nighthawkempires.core.CorePlugin;
import net.nighthawkempires.core.lang.Messages;
import net.nighthawkempires.souls.SoulsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.nighthawkempires.core.CorePlugin.getCommandManager;
import static net.nighthawkempires.core.lang.Messages.*;

public class SoulShopCommand implements CommandExecutor {

    public SoulShopCommand() {
        getCommandManager().registerCommands("soulshop", new String[] {
                "ne.souls"
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {

            if  (!player.hasPermission("ne.souls")) {
                player.sendMessage(CorePlugin.getMessages().getChatTag(NO_PERMS));
                return true;
            }

            if (args.length == 0) {
                player.openInventory(SoulsPlugin.getInventoryData().getSoulShopInventory());
                return true;
            }
            player.sendMessage(CorePlugin.getMessages().getChatTag(INVALID_SYNTAX));
            return true;
        }
        return false;
    }
}