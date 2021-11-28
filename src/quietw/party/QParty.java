package quietw.party;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import quietw.party.Chats.ChatParty;
import quietw.party.Commands.PartyCommand;
import quietw.party.Commands.TabComplete;
import quietw.party.Database.DatabaseEditor;
import quietw.party.Events.EventListener;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class QParty extends JavaPlugin {

    File config = new File(getDataFolder(), "config.yml");
    private static QParty instance;
    Connection connection;

    @Override
    public void onEnable() {
        instance = this;
        new ChatParty();
        new PartyCommand();
        Objects.requireNonNull(getCommand("party")).setTabCompleter(new TabComplete());
        Bukkit.getServer().getPluginManager().registerEvents(new EventListener(), this);
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + (getDataFolder() + File.separator + "data.db"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        new DatabaseEditor(connection);
        if(!config.exists()) {
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
        }
    }

    @Override
    public void onDisable() {
        try {
            connection.close();
            getLogger().info("DB Connection closed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static QParty getInstance() {
        return instance;
    }
}
