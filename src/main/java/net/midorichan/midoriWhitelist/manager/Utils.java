package net.midorichan.midoriWhitelist.manager;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Utils {

    public static boolean hasPermission(Player p, String permission) {
        // OPか権限を持っているか
        return p.isOp() || p.hasPermission(permission);
    }

    public static boolean hasPermission(CommandSender sender, String permission) {
        // OPか権限を持っているか
        return sender.isOp() || sender.hasPermission(permission);
    }

    public static String colorize(String message) {
        // カラーコードを変換
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}