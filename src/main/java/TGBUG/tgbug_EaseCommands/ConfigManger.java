package TGBUG.tgbug_EaseCommands;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.Map;

public class ConfigManger {
    private final TGBUG_EaseCommands plugin;
    private List<Map<?, ?>> commands;
    private boolean isbStats;

    public ConfigManger(TGBUG_EaseCommands plugin) {
        this.plugin = plugin;
    }

    public ConfigManger loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");

        checkConfigFile.checkConfigFile(configFile, plugin);

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        commands = config.getMapList("commands");
        isbStats = config.getBoolean("bStats");

        //返回实例
        return this;
    }

    public List<Map<?, ?>> getCommands() {
        return commands;
    }

    public boolean isbStats() {
        return isbStats;
    }
}
