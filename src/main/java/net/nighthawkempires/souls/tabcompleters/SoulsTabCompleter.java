package net.nighthawkempires.souls.tabcompleters;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.Collections;
import java.util.List;

import static net.nighthawkempires.core.CorePlugin.getMessages;
import static net.nighthawkempires.core.lang.Messages.CHAT_FOOTER;
import static net.nighthawkempires.core.lang.Messages.CHAT_HEADER;
import static org.bukkit.ChatColor.DARK_GRAY;
import static org.bukkit.ChatColor.GRAY;

public class SoulsTabCompleter implements TabCompleter {

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

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = Lists.newArrayList();
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission("ne.souls.admin")) {
                return completions;
            }

            switch (args.length) {
                case 1:
                    List<String> arggs = Lists.newArrayList("help", "reset", "set", "add", "remove");
                    for (Player players : Bukkit.getOnlinePlayers()) {
                        arggs.add(players.getName());
                    }
                    StringUtil.copyPartialMatches(args[0], arggs, completions);
                    Collections.sort(completions);
                    return completions;
                case 2:
                    switch (args[0].toLowerCase()) {
                        case "reset":
                        case "set":
                        case "add":
                        case "remove":
                            arggs = Lists.newArrayList("mob", "player");
                            if (args[0].toLowerCase().equals("reset")) {
                                arggs.add("all");
                            }
                            for (Player players : Bukkit.getOnlinePlayers()) {
                                arggs.add(players.getName());
                            }
                            StringUtil.copyPartialMatches(args[1], arggs, completions);
                            Collections.sort(completions);
                            return completions;
                        default:
                            return completions;
                    }
                case 3:
                    switch (args[0].toLowerCase()) {
                        case "reset":
                        case "set":
                        case "add":
                        case "remove":
                            arggs = Lists.newArrayList("mob", "player");
                            if (args[0].toLowerCase().equals("reset")) {
                                arggs.add("all");
                            }
                            StringUtil.copyPartialMatches(args[1], arggs, completions);
                            Collections.sort(completions);
                            return completions;
                        default:
                            return completions;
                    }
                default:
                    return completions;
            }
        }
        return completions;
    }
}