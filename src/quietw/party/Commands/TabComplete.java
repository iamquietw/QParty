package quietw.party.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TabComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String alias, @Nonnull String[] args) {
        List<String> commands = new ArrayList<>();
        if(args.length == 1) {
            List<String> completions = new ArrayList<>();
            completions.add("accept");
            completions.add("leave");
            completions.add("invite");
            completions.add("kick");
            for(String s : completions) {
                if(s.toLowerCase().startsWith(args[0].toLowerCase())) {
                    commands.add(s);
                }
            }
            return commands;
        }
        return null;
    }
}
