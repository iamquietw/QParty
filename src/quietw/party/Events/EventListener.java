package quietw.party.Events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import quietw.party.Database.DatabaseEditor;
import quietw.party.QParty;
import quietw.qchat.Chat.ChatLocal;
import quietw.qchat.Database.DBEditor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

import static quietw.party.Database.DatabaseEditor.execUpdate;
import static quietw.party.Utils.ConfigMessages.getMessage;

public class EventListener implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        try {
            Connection connection = DatabaseEditor.connection;
            Statement statement = connection.createStatement();
            int _count = 0;
            ResultSet rs = statement.executeQuery("SELECT COUNT(username) FROM users WHERE username='%s'".replace("%s", player.getName()));
            _count = rs.getInt("COUNT(username)");
            // PARTY LEAVE CODE
            if (_count == 1) {
                ResultSet party = statement.executeQuery(String.format("SELECT partyLeader, status FROM users WHERE username='%s'", player.getName()));
                while (party.next()) {
                    String partyLeader = party.getString("partyLeader");
                    String status = party.getString("status");
                    if (!partyLeader.equalsIgnoreCase(player.getName())) {
                        if (status.equalsIgnoreCase("joined")) {
                            ResultSet members = statement.executeQuery(String.format("SELECT username FROM users WHERE partyLeader='%s'", partyLeader));
                            while (members.next()) {
                                Player member = Bukkit.getServer().getPlayerExact(members.getString("username"));
                                assert member != null;
                                member.sendMessage(Objects.requireNonNull(QParty.getInstance().getConfig().getString("playerLeft")).replace("%player", player.getName()).replace("&", "§"));
                            }
                            player.sendMessage(getMessage("partyLeft"));
                        }
                        execUpdate("DELETE FROM users WHERE username='" + player.getName() + "'");
                    } else {
                        ResultSet members = statement.executeQuery(String.format("SELECT username, status FROM users WHERE partyLeader='%s'", partyLeader));
                        while(members.next()) {
                            String username = members.getString("username");
                            String partyStatus = members.getString("status");
                            if(partyStatus.equalsIgnoreCase("joined") && !username.equalsIgnoreCase(partyLeader)) {
                                Player member = Bukkit.getServer().getPlayerExact(username);
                                assert member != null;
                                member.sendMessage(getMessage("leaderLeft"));
                                DBEditor.getInstance().selectChat(member, ChatLocal.getInstance());
                            }
                        }
                        execUpdate(String.format("DELETE FROM parties WHERE leader='%s'", partyLeader));
                        execUpdate(String.format("DELETE FROM users WHERE partyLeader='%s'", partyLeader));
                    }
                }
            }
            statement.close();
            // PARTY LEAVE CODE END
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
