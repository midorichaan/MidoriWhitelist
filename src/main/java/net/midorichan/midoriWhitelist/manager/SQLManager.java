package net.midorichan.midoriWhitelist.manager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.midorichan.midoriWhitelist.MidoriWhitelist;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SQLManager {

    private HikariDataSource hikari = null;
    private MidoriWhitelist plugin;

    public SQLManager(MidoriWhitelist plugin) {
        this.plugin = plugin;
        init_database();
    }

    public void close() {
        if (hikari != null) {
            hikari.close();
        }
    }

    public Connection getConnection() throws SQLException {
        if (hikari == null) {
            return null;
        }

        return hikari.getConnection();
    }

    public void init_database() {
        String host = plugin.getConfig().getString("database.host");
        String port = plugin.getConfig().getString("database.port");
        String database = plugin.getConfig().getString("database.database");
        String user = plugin.getConfig().getString("database.username");
        String password = plugin.getConfig().getString("database.password");

        if (
                host == null || port == null || database == null || user == null || password == null
        ) {
            plugin.getLogger().warning("データベース接続に失敗しました。サーバーを停止します。");
            Bukkit.getServer().shutdown();
            return;
        }

        HikariConfig config = new HikariConfig();
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false");
        config.addDataSourceProperty("user", user);
        config.addDataSourceProperty("password", password);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        config.setConnectionInitSql("SELECT 1");

        hikari = new HikariDataSource(config);

        try (
                Connection connection = getConnection();
                Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS whitelist (" +
                            "uuid VARCHAR(255) NOT NULL PRIMARY KEY, " +
                            "name VARCHAR(255) NOT NULL" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;"
            );
        } catch (SQLException e) {
            this.plugin.getLogger().warning(e.getMessage());
        }
    }

    public void reconnectDatabase() {
        close();
        init_database();
    }

    public void addWhitelist(String uuid, String name) {
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO whitelist (uuid, name) VALUES (?, ?)"
                )
        ) {
            statement.setString(1, uuid);
            statement.setString(2, name);
            statement.executeUpdate();
        } catch (SQLException e) {
            this.plugin.getLogger().warning(e.getMessage());
        }
    }

    public void removeWhitelist(String uuid) {
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM whitelist WHERE uuid = ?"
                )
        ) {
            statement.setString(1, uuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            this.plugin.getLogger().warning(e.getMessage());
        }
    }

    public boolean isWhitelisted(String uuid) {
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM whitelist WHERE uuid = ?"
                )
        ) {
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            this.plugin.getLogger().warning(e.getMessage());
        }
        return false;
    }

    public void clearWhitelist() {
        try (
                Connection connection = getConnection();
                Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate("DELETE FROM whitelist");
        } catch (SQLException e) {
            this.plugin.getLogger().warning(e.getMessage());
        }
    }

    public List<String> getWhitelist() {
        try (
                Connection connection = getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM whitelist")
        ) {
            List<String> whitelist = new ArrayList<>();
            while (resultSet.next()) {
                whitelist.add(resultSet.getString("name"));
            }
            return whitelist;
        } catch (SQLException e) {
            this.plugin.getLogger().warning(e.getMessage());
        }
        return null;
    }

}