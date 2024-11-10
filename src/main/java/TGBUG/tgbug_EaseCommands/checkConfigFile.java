package TGBUG.tgbug_EaseCommands;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public interface checkConfigFile {
    static void checkConfigFile(File file, TGBUG_EaseCommands plugin) {
        if (!file.exists()) {
            if (!plugin.getDataFolder().exists()) {
                if (!plugin.getDataFolder().mkdirs()) {
                    plugin.getLogger().severe("无法创建数据文件夹: " + plugin.getDataFolder().getAbsolutePath());
                }
            }
            try (InputStream defaultConfig = plugin.getResource(file.getName())) {
                Files.copy(defaultConfig, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                plugin.getLogger().severe("无法创建配置文件: " + e.getMessage());
            }
        }
    }
}
