package quietw.party.Commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import quietw.party.QParty;

import javax.annotation.Nonnull;

public abstract class Command implements CommandExecutor {

    public Command(String command) {
        PluginCommand pluginCommand = QParty.getInstance().getCommand(command);
        assert pluginCommand != null;
        pluginCommand.setExecutor(this);
    }

    public abstract void execute(CommandSender sender, String label, String[] args);

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull org.bukkit.command.Command command, @Nonnull String s, @Nonnull String[] strings) {
        execute(commandSender, s, strings);
        return true;
    }
}
