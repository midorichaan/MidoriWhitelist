package net.midorichan.midoriWhitelist.manager;

import net.midorichan.midoriWhitelist.MidoriWhitelist;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerListener implements Listener {

    private final MidoriWhitelist plugin;
    private final String prefix = Utils.colorize(" &b&lWhitelist &2|&a>&r ");

    public PlayerListener(MidoriWhitelist plugin) {
        this.plugin = plugin;
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerConnect(PlayerLoginEvent e) {
        if (!plugin.getConfig().getBoolean("whitelist-enabled")) {
            return;
        }

        String[] kickMessage = {
                " ",
                prefix + Utils.colorize("Whitelist Enabled"),
                " ",
                Utils.colorize("You are &cNOT&r whitelisted on this network."),
                Utils.colorize("Please contact an administrator to be added."),
                " ",
        };

        if (!plugin.getDatabase().isWhitelisted(e.getPlayer().getUniqueId().toString())) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, String.join("\n", kickMessage));

            Bukkit.getOnlinePlayers().forEach(player -> {
                if (player.hasPermission("midoriwhitelist.notify")) {
                    player.sendMessage(Utils.colorize(" &a*&r " + e.getPlayer().getName() + " &7tried to join the server but is not whitelisted"));
                }
            });
        }
    }

}
