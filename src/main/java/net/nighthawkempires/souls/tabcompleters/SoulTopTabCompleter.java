package net.nighthawkempires.souls.tabcompleters;

import com.google.common.collect.Lists;
import net.nighthawkempires.souls.SoulsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.Collections;
import java.util.List;

import static net.nighthawkempires.souls.SoulsPlugin.getUserRegistry;

public class SoulTopTabCompleter implements TabCompleter {

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = Lists.newArrayList();
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission("ne.souls")) {
                return completions;
            }

            switch (args.length) {
                case 1:
                    List<String> arggs = Lists.newArrayList();
                    int pages = (int) Math.ceil((double) getUserRegistry().getUsers().size() / 10);
                    for (int i = 0; i < pages; i++) {
                        arggs.add((i + 1) + "");
                    }
                    StringUtil.copyPartialMatches(args[0], arggs, completions);
                    Collections.sort(completions);
                    return completions;
                default:
                    return completions;
            }
        }
        return completions;
    }
}