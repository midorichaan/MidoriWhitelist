package net.midorichan.midoriWhitelist.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class WhitelistTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("whitelist")) {
            return null;
        }

        if (args.length == 1) {
            return List.of("on", "off", "add", "remove", "list", "clear", "reload");
        }

        switch (args[1].toLowerCase()) {
            case "add", "remove":
                if (args.length == 2) {
                    return List.of("<player>");
                }
                break;
            case "on", "off", "clear", "reload", "list":
                if (args.length == 2) {
                    return List.of();
                }
                break;
        }

        return List.of();
    }
}
