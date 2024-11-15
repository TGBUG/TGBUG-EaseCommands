package TGBUG.tgbug_EaseCommands;

import org.bukkit.Bukkit;
import org.bukkit.command.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class Commands implements CommandExecutor, TabCompleter {
    private ConfigManger configManger;
    private final TGBUG_EaseCommands plugin;
    private final List<String> cachedCommands = new ArrayList<>();

    public Commands(ConfigManger configManger, TGBUG_EaseCommands plugin) {
        this.plugin = plugin;
        this.configManger = configManger;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("使用方法: /es [reload]");
            return true;
        } else {
            switch (args[0].toLowerCase()) {
                case "reload":
                    if (unregisterCommands()) {
                        configManger = configManger.loadConfig();
                        registerCommands();
                        plugin.getLogger().info("已重新加载配置文件");
                    }
                    return true;

                default:
                    sender.sendMessage("未知命令");
                    return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("reload");
        }

        return completions;
    }

    public void registerCommands() {
        List<Map<?, ?>> commands = configManger.getCommands();

        for (Map<?, ?> commandMap : commands) {
            for (Map.Entry<?, ?> entry : commandMap.entrySet()) {
                String commandName = String.valueOf(entry.getKey());

                if (!cachedCommands.contains(commandName)) {
                    cachedCommands.add(commandName);
                    Object value = entry.getValue();

                    CommandExecutor executor = (sender, command, label, args) -> {
                        if (value instanceof Map<?, ?> dataMap) {
                            if (dataMap.containsKey("full_command")) {
                                String fullCommand = (String) dataMap.get("full_command");
                                Bukkit.dispatchCommand(sender, fullCommand);
                                return true;
                            }
                            if (dataMap.containsKey("subcommands")) {
                                List<Map<?, ?>> subCommands = (List<Map<?, ?>>) dataMap.get("subcommands");

                                if (args.length == 0) {
                                    sender.sendMessage("未知命令");
                                    return false;
                                }

                                for (Map<?, ?> subCommandMap : subCommands) {
                                    if (subCommandMap.containsKey("subcommand") && subCommandMap.containsKey("full_command")) {
                                        String subCommand = (String) subCommandMap.get("subcommand");
                                        String fullCommand = (String) subCommandMap.get("full_command");

                                        if (args[0].equalsIgnoreCase(subCommand)) {
                                            Bukkit.dispatchCommand(sender, fullCommand);
                                            return true;
                                        }
                                    }
                                }

                                sender.sendMessage("未知命令");
                                return false;
                            }
                        }
                        return false;
                    };

                    TabCompleter tabCompleter = (sender, command, alias, args) -> {
                        List<String> completions = new ArrayList<>();

                        if (value instanceof Map<?, ?> dataMap && dataMap.containsKey("subcommands") && args.length == 1) {
                            List<Map<String, Object>> subCommands = (List<Map<String, Object>>) dataMap.get("subcommands");

                            for (Map<String, Object> subCommandMap : subCommands) {
                                if (subCommandMap.containsKey("subcommand")) {
                                    String subCommand = (String) subCommandMap.get("subcommand");
                                    if (subCommand != null && subCommand.toLowerCase().startsWith(args[0].toLowerCase())) {
                                        completions.add(subCommand);
                                    }
                                }
                            }
                        }

                        return completions;
                    };

                    CustomCommand command = new CustomCommand(commandName, executor, tabCompleter);
                    Bukkit.getCommandMap().register(commandName, command);
                }
            }
        }
    }

    public boolean unregisterCommands() {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());

            Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            Map<String, Command> knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);

            Iterator<String> iterator = cachedCommands.iterator();
            while (iterator.hasNext()) {
                String commandName = iterator.next();
                Command command = knownCommands.get(commandName);
                if (command != null && command.unregister(commandMap)) {
                    iterator.remove();
                } else {
                    plugin.getLogger().warning("无法注销命令: " + commandName + " 请向插件作者反馈");
                    return false;
                }
            }
        } catch (Exception e) {
            plugin.getLogger().severe("注销命令时发生错误: " + e.getMessage() + "请向插件作者反馈");
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
