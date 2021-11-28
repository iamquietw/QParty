package quietw.party.Chats;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import quietw.party.Database.DatabaseEditor;
import quietw.qchat.Chat.Chat;
import quietw.qchat.Chat.ChatLocal;
import quietw.qchat.Database.DBEditor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static quietw.party.Utils.ConfigMessages.getMessage;

public class ChatParty extends Chat {
    private static ChatParty instance;

    public ChatParty() {
        instance = this;
    }

    @Override
    public String getName() {
        return "party";
    }

    @Override
    public void sendMessage(Player sender, String message) {
        try {
            Connection connection = DatabaseEditor.connection;
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(String.format("SELECT COUNT(username) FROM users WHERE username='%s' AND status='joined'", sender.getName()));
            if(rs.getInt("COUNT(username)") == 1) {
                ResultSet leaderResult = statement.executeQuery(String.format("SELECT partyLeader FROM users WHERE username='%s'", sender.getName()));
                String leader = leaderResult.getString("partyLeader");
                ResultSet members = statement.executeQuery(String.format("SELECT username FROM users WHERE partyLeader='%s' AND status='joined'", leader));
                while (members.next()) {
                    Player receiver = Bukkit.getServer().getPlayerExact(members.getString("username"));
                    assert receiver != null;
                    receiver.sendMessage(getMessage("partyChatFormat").replace("%player", sender.getDisplayName()).replace("%message", message));
                }
            } else {
                DBEditor.getInstance().selectChat(sender, ChatLocal.getInstance());
                ChatLocal.getInstance().sendMessage(sender, message);
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ChatParty getInstance() {
        return instance;
    }
}
