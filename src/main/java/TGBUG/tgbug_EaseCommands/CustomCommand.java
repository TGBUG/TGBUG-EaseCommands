package TGBUG.tgbug_EaseCommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class CustomCommand extends Command {
    private CommandExecutor executor;
    private TabCompleter tabCompleter;

    public CustomCommand(String name, CommandExecutor executor, TabCompleter tabCompleter) {
        super(name);
        this.executor = executor;
        this.tabCompleter = tabCompleter;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        return executor != null && executor.onCommand(sender, this, label, args);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        if (tabCompleter != null) {
            return tabCompleter.onTabComplete(sender, this, alias, args);
        }
        return super.tabComplete(sender, alias, args);
    }
}
