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

public class SoulsTabCompleter implements TabCompleter {

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = Lists.newArrayList();
        if (sender instanceof Player player) {
            if (!player.hasPermission("ne.souls.admin"))return completions;

            switch (args.length) {
                case 1 -> {
                    List<String> options = Lists.newArrayList("help", "reset", "add", "remove");
                    Bukkit.getOnlinePlayers().stream().forEach(online -> options.add(online.getName()));
                    StringUtil.copyPartialMatches(args[0], options, completions);
                    Collections.sort(completions);
                    return completions;
                }
                case 2 -> {
                    List<String> options = Lists.newArrayList("mob", "player");
                    if (args[0].toLowerCase().equals("reset")) options.add("all");
                    Bukkit.getOnlinePlayers().stream().forEach(online -> options.add(online.getName()));
                    StringUtil.copyPartialMatches(args[1], options, completions);
                    Collections.sort(completions);
                    return completions;
                }
                case 3 -> {
                    List<String> options = Lists.newArrayList("mob", "player", "amount");
                    if (args[0].toLowerCase().equals("reset")) options.add("all");
                    StringUtil.copyPartialMatches(args[2], options, completions);
                    Collections.sort(completions);
                    return completions;
                }
                default -> {
                    return completions;
                }
            }
        }
        return completions;
    }
}
