package quietw.party.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import quietw.party.Chats.ChatParty;
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

public class PartyCommand extends Command {

    public PartyCommand() {
        super("party");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if(args.length == 0) {
            sendHelp(sender);
        }
        if(args.length == 1) {
            String subCommand = args[0];
            if(subCommand.equalsIgnoreCase("accept")) {
                try {
                    Connection connection = DatabaseEditor.connection;
                    Statement statement = connection.createStatement();
                    ResultSet rs = statement.executeQuery(String.format("SELECT status, partyLeader FROM users WHERE username='%s'", sender.getName()));
                    if(!rs.isClosed()) {
                        if(rs.getString("status").equalsIgnoreCase("invited")) {
                            String leader = rs.getString("partyLeader");
                            ResultSet res = statement.executeQuery(String.format("SELECT username, status FROM users WHERE partyLeader='%s'", leader));
                            while(res.next()) {
                                if(res.getString("status").equalsIgnoreCase("joined")) {
                                    Player receiver = Bukkit.getServer().getPlayerExact(res.getString("username"));
                                    assert receiver != null;
                                    receiver.sendMessage(getMessage("playerJoined").replace("%player", sender.getName()));
                                }
                            }
                            execUpdate(String.format("DELETE FROM users WHERE username='%s'", sender.getName()));
                            execUpdate(String.format("INSERT INTO users VALUES('%s', '%s', 'joined')", sender.getName(), leader));
                            sender.sendMessage(getMessage("playerJoined").replace("%player", sender.getName()));
                        } else {
                            sender.sendMessage(getMessage("noInvites"));
                        }
                    } else {
                        sender.sendMessage(getMessage("noInvites"));
                    }
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            else if(subCommand.equalsIgnoreCase("leave")) {
                try {
                    Connection connection = DatabaseEditor.connection;
                    Statement statement = connection.createStatement();
                    int _count = 0;
                    ResultSet rs = statement.executeQuery("SELECT COUNT(username) FROM users WHERE username='%s'".replace("%s", sender.getName()));
                    _count = rs.getInt("COUNT(username)");
                    // PARTY LEAVE CODE
                    if (_count == 1) {
                        ResultSet party = statement.executeQuery(String.format("SELECT partyLeader, status FROM users WHERE username='%s'", sender.getName()));
                        while (party.next()) {
                            String partyLeader = party.getString("partyLeader");
                            String status = party.getString("status");
                            if (!partyLeader.equalsIgnoreCase(sender.getName())) {
                                if (status.equalsIgnoreCase("joined")) {
                                    ResultSet members = statement.executeQuery(String.format("SELECT username FROM users WHERE partyLeader='%s'", partyLeader));
                                    while (members.next()) {
                                        Player member = Bukkit.getServer().getPlayerExact(members.getString("username"));
                                        assert member != null;
                                        member.sendMessage(Objects.requireNonNull(QParty.getInstance().getConfig().getString("playerLeft")).replace("%player", sender.getName()).replace("&", "§"));
                                    }
                                    sender.sendMessage(getMessage("partyLeft"));
                                }
                                execUpdate("DELETE FROM users WHERE username='" + sender.getName() + "'");
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
                                sender.sendMessage(getMessage("partyLeft"));
                            }
                        }
                        DBEditor.getInstance().selectChat(((Player)sender), ChatLocal.getInstance());
                    } else {
                        sender.sendMessage(getMessage("notInParty"));
                    }
                    statement.close();
                    // PARTY LEAVE CODE END
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        if(args.length == 2) {
            String subCommand = args[0];
            if(subCommand.equalsIgnoreCase("invite")) {
                for(Player player : Bukkit.getServer().getOnlinePlayers()) {
                    if (player.getName().equalsIgnoreCase(args[1])) {
                        try {
                                Connection connection = DatabaseEditor.connection;
                                Statement statement = connection.createStatement();
                                int _count = 0;
                                ResultSet rs = statement.executeQuery("SELECT COUNT(username) FROM users WHERE username='%s'".replace("%s", sender.getName()));
                                _count = rs.getInt("COUNT(username)");
                                // PARTY LEAVE CODE
                                if (_count == 1) {
                                    ResultSet party = statement.executeQuery(String.format("SELECT partyLeader, status FROM users WHERE username='%s'", sender.getName()));
                                    while (party.next()) {
                                        String partyLeader = party.getString("partyLeader");
                                        String status = party.getString("status");
                                        if (!partyLeader.equalsIgnoreCase(sender.getName())) {
                                            if (status.equalsIgnoreCase("joined")) {
                                                ResultSet members = statement.executeQuery(String.format("SELECT username FROM users WHERE partyLeader='%s'", partyLeader));
                                                while (members.next()) {
                                                    Player member = Bukkit.getServer().getPlayerExact(members.getString("username"));
                                                    assert member != null;
                                                    member.sendMessage(Objects.requireNonNull(QParty.getInstance().getConfig().getString("playerLeft")).replace("%player", sender.getName()).replace("&", "§"));
                                                }
                                                sender.sendMessage(getMessage("partyLeft"));
                                            }
                                            execUpdate("DELETE FROM users WHERE username='" + sender.getName() + "'");
                                        }
                                    }
                                }
                                statement.close();
                                // PARTY LEAVE CODE END
                                Connection connection_ = DatabaseEditor.connection;
                                Statement statement_ = connection_.createStatement();
                                ResultSet rs_ = statement_.executeQuery(String.format("SELECT COUNT(leader) FROM parties WHERE leader='%s'", sender.getName()));
                                if(rs_.getInt("COUNT(leader)") == 0) {
                                    // PARTY CREATE CODE START
                                    execUpdate(String.format("INSERT INTO parties VALUES('%s')", sender.getName()));
                                    execUpdate(String.format("INSERT INTO users VALUES('%s', '%s', 'joined')", sender.getName(), sender.getName()));
                                    sender.sendMessage(getMessage("partyCreated"));
                                    // PARTY CREATE END
                                }
                                statement_.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        // PARTY INVITE
                        int i = 0;
                        try {
                            Connection connection = DatabaseEditor.connection;
                            Statement statement = connection.createStatement();
                            ResultSet rs = statement.executeQuery(String.format("SELECT COUNT(username), status FROM users WHERE username='%s'", player.getName()));
                            i = rs.getInt("COUNT(username)");
                            if (rs.getString("status") != null && rs.getString("status").equalsIgnoreCase("invited")) {
                                i = 0;
                                execUpdate(String.format("DELETE FROM users WHERE username='%s'", player.getName()));
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        if (i == 1) {
                            sender.sendMessage(getMessage("alreadyInParty"));
                        } else {
                            execUpdate(String.format("INSERT INTO users VALUES('%s', '%s', 'invited')", player.getName(), sender.getName()));
                            sender.sendMessage(getMessage("playerInvited").replace("%invited", player.getDisplayName()));
                            player.sendMessage(getMessage("invitedToParty").replace("%leader", sender.getName()));
                        }
                        return;
                    }
                }
                sender.sendMessage(getMessage("playerNotFound"));
            }
            else if(subCommand.equalsIgnoreCase("kick")) {
                String kickedPlayer = args[1];
                Player player = Bukkit.getServer().getPlayerExact(kickedPlayer);
                if(player != null) {
                    try {
                        Connection connection = DatabaseEditor.connection;
                        Statement statement = connection.createStatement();
                        ResultSet resultSet = statement.executeQuery(String.format("SELECT COUNT(leader) FROM parties WHERE leader='%s'", sender.getName()));
                        if(resultSet.getInt("COUNT(leader)") == 1) {
                            ResultSet isMember = statement.executeQuery(String.format("SELECT COUNT(username) FROM users WHERE partyLeader='%s' AND username='%s'", sender.getName(), player.getName()));
                            if(isMember.getInt("COUNT(username)") == 1) {
                                execUpdate(String.format("DELETE FROM users WHERE username='%s'", player.getName()));
                                sender.sendMessage(getMessage("playerKicked"));
                                player.sendMessage(getMessage("kicked"));
                                DBEditor.getInstance().selectChat(player, ChatLocal.getInstance());
                            } else {
                                sender.sendMessage(getMessage("notMember"));
                            }
                        } else {
                            sender.sendMessage(getMessage("notLeader"));
                        }
                        statement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    sender.sendMessage(getMessage("playerNotFound"));
                }
            }
        }
        if(args.length > 0) {
            if(!args[0].equalsIgnoreCase("accept") && !args[0].equalsIgnoreCase("leave") && !args[0].equalsIgnoreCase("invite") && !args[0].equalsIgnoreCase("kick")) {
                ChatParty.getInstance().sendMessage(((Player)sender), String.join(" ", args));
            }
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "--- QParty Help ---");
        sender.sendMessage("/party invite <player> - invite <player> to your party.");
        sender.sendMessage("/party accept - invite latest party-join request.");
        sender.sendMessage("/party kick <player> - kick <player> from your party.");
        sender.sendMessage("/party leave");
        sender.sendMessage("/party <message> - send <message> to your party' chat.");
        sender.sendMessage(ChatColor.GREEN + "--- QParty Help ---");
    }
}
