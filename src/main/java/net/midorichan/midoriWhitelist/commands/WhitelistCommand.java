package net.midorichan.midoriWhitelist.commands;

import net.midorichan.midoriWhitelist.MidoriWhitelist;
import net.midorichan.midoriWhitelist.manager.Utils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class WhitelistCommand implements CommandExecutor {

    private final MidoriWhitelist plugin;

    public WhitelistCommand(MidoriWhitelist plugin) {
        this.plugin = plugin;
    }

    private String[] getHelpText() {
        return new String[]{
                plugin.getPrefix() + "===== Whitelist Help =====",
                plugin.getPrefix() + " /whitelist on - ホワイトリストを有効にします",
                plugin.getPrefix() + " /whitelist off - ホワイトリストを無効にします",
                plugin.getPrefix() + " /whitelist add <player> - プレイヤーをホワイトリストに追加します",
                plugin.getPrefix() + " /whitelist remove <player> - プレイヤーをホワイトリストから削除します",
                plugin.getPrefix() + " /whitelist list - ホワイトリストに登録されているプレイヤーを表示します",
                plugin.getPrefix() + " /whitelist clear - ホワイトリストをクリアします",
                plugin.getPrefix() + " /whitelist reload - 設定を再読み込みします",
        };
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (Utils.hasPermission(sender, "midoriwhitelist.command")) {
            if (args.length == 0) {
                sender.sendMessage(this.getHelpText());
                return true;
            }

            switch (args[0].toLowerCase()) {
                case "on":
                    // Enable whitelist
                    plugin.getConfig().set("whitelist-enabled", true);
                    plugin.saveConfig();
                    sender.sendMessage(Utils.colorize(plugin.getPrefix() + "ホワイトリストを有効にしました"));
                    break;
                case "off":
                    // Disable whitelist
                    plugin.getConfig().set("whitelist-enabled", false);
                    plugin.saveConfig();
                    sender.sendMessage(Utils.colorize(plugin.getPrefix() + "ホワイトリストを無効にしました"));
                    break;
                case "add":
                    if (args.length < 2) {
                        sender.sendMessage(Utils.colorize(plugin.getPrefix() + "&c&nエラー: プレイヤー名を指定してください"));
                        return true;
                    }

                    OfflinePlayer target = plugin.getServer().getOfflinePlayer(args[1]);
                    if (plugin.getDatabase().isWhitelisted(target.getUniqueId().toString())) {
                        sender.sendMessage(Utils.colorize(plugin.getPrefix() + "&c&nエラー: " + target.getName() + " はすでにホワイトリストに登録されています"));
                        return true;
                    }

                    plugin.getDatabase().addWhitelist(target.getUniqueId().toString(), target.getName());
                    sender.sendMessage(Utils.colorize(plugin.getPrefix() + target.getName() + " をホワイトリストに追加しました"));

                    break;
                case "remove":
                    if (args.length < 2) {
                        sender.sendMessage(Utils.colorize(plugin.getPrefix() + "&c&nエラー: プレイヤー名を指定してください"));
                        return true;
                    }

                    OfflinePlayer targetToRemove = plugin.getServer().getOfflinePlayer(args[1]);
                    if (!plugin.getDatabase().isWhitelisted(targetToRemove.getUniqueId().toString())) {
                        sender.sendMessage(Utils.colorize(plugin.getPrefix() + "&c&nエラー: " + targetToRemove.getName() + " はホワイトリストに登録されていません"));
                        return true;
                    }

                    plugin.getDatabase().removeWhitelist(targetToRemove.getUniqueId().toString());
                    sender.sendMessage(Utils.colorize(plugin.getPrefix() + targetToRemove.getName() + " をホワイトリストから削除しました"));

                    break;
                case "list":
                    // List all whitelisted players
                    List<String> whitelistedPlayers = plugin.getDatabase().getWhitelist();
                    if (whitelistedPlayers.isEmpty()) {
                        sender.sendMessage(Utils.colorize(plugin.getPrefix() + "ホワイトリストは空です"));
                        return true;
                    }

                    String[] playerList = {
                            plugin.getPrefix() + "ホワイトリストに登録されているプレイヤー:",
                            String.join(", ", whitelistedPlayers)
                    };
                    sender.sendMessage(playerList);
                    break;
                case "clear":
                    // Clear the whitelist
                    plugin.getDatabase().clearWhitelist();
                    sender.sendMessage(Utils.colorize(plugin.getPrefix() + "ホワイトリストから全てのプレイヤーを削除しました"));
                    break;
                case "reload":
                    // Reload the whitelist configuration
                    plugin.reloadConfig();
                    plugin.getDatabase().reconnectDatabase();

                    sender.sendMessage(Utils.colorize(plugin.getPrefix() + "設定を再読み込みしました"));
                    break;
                default:
                    sender.sendMessage(this.getHelpText());
                    break;
            }
        } else {
            sender.sendMessage(Utils.colorize(plugin.getPrefix() + "&c&nエラー: 権限がありません"));
        }

        return true;
    }

}
