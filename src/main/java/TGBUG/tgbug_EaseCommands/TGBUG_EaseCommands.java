package TGBUG.tgbug_EaseCommands;

import org.bukkit.plugin.java.JavaPlugin;


public final class TGBUG_EaseCommands extends JavaPlugin {
    private Commands commands;

    @Override
    public void onEnable() {
        ConfigManger configManger = new ConfigManger(this);
        configManger.loadConfig();
        commands = new Commands(configManger, this);
        getCommand("easecommands").setExecutor(commands);
        getCommand("easecommands").setTabCompleter(commands);

        //注册命令
        commands.registerCommands();

        if (configManger.isbStats()) {
            Metrics metrics = new Metrics(this, 23852);
        }
    }

    @Override
    public void onDisable() {
        commands.unregisterCommands();
    }

}
