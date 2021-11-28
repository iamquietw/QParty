package quietw.party.Database;

import quietw.party.QParty;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseEditor {

    public static Connection connection;
    public static DatabaseEditor instance;

    public DatabaseEditor(Connection con) {
        connection = con;
        QParty.getInstance().getLogger().info("DB Connected.");
        // username - player's name; 'partyId' - invited/joined party id; 'status' - JOINED/INVITED
        String query = "CREATE TABLE IF NOT EXISTS 'users' ('username' TEXT NOT NULL UNIQUE, 'partyLeader' TEXT NOT NULL, 'status' TEXT NOT NULL)";
        String query_parties = "CREATE TABLE IF NOT EXISTS 'parties' ('leader' TEXT NOT NULL UNIQUE)";
        String query_delete = "DELETE FROM users";
        execUpdate(query);
        execUpdate(query_parties);
        execUpdate(query_delete);
        execUpdate("DELETE FROM parties");
        QParty.getInstance().getLogger().info("DB Table created.");
        instance = this;
    }

    public static void execUpdate(String query) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseEditor getInstance() {
        return instance;
    }

}
