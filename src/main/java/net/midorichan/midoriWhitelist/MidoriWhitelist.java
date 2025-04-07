package net.midorichan.midoriWhitelist;

import net.midorichan.midoriWhitelist.commands.WhitelistCommand;
import net.midorichan.midoriWhitelist.commands.WhitelistTabCompleter;
import net.midorichan.midoriWhitelist.manager.PlayerListener;
import net.midorichan.midoriWhitelist.manager.SQLManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class MidoriWhitelist extends JavaPlugin {

    private MidoriWhitelist plugin;
    private final String prefix = " §l§2|§a> §r ";
    private SQLManager database;

    public MidoriWhitelist getPlugin() {
        return plugin;
    }

    public String getPrefix() {
        return prefix;
    }

    public SQLManager getDatabase() {
        return database;
    }

    @Override
    public void onEnable() {
        plugin = this;

        // Load Config
        saveDefaultConfig();

        // Register Commands
        getCommand("whitelist").setExecutor(new WhitelistCommand(this));
        getCommand("whitelist").setTabCompleter(new WhitelistTabCompleter());

        // Register Events
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        // Initialize Database
        database = new SQLManager(this);

        Bukkit.getServer().getLogger().info(prefix + "Enabled MidoriWhitelist v" + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        database.close();

        Bukkit.getServer().getLogger().info(prefix + "Disabled MidoriWhitelist v" + getDescription().getVersion());
    }

}
