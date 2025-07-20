package top.zhillerdev.functions.config.base;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import top.zhillerdev.functions.utils.LoggerUtils;

import java.io.File;
import java.io.IOException;

public class ConfigProvider implements IConfigProvider {
  protected File configFile;
  protected FileConfiguration config;
  protected JavaPlugin plugin;
  protected final String fileName;
  
  public ConfigProvider(String fileName) {
    this.fileName = fileName;
  }
  
  @Override
  public void setup(JavaPlugin plugin) {
    this.plugin = plugin;
    // 确保插件数据目录存在
    if (!plugin.getDataFolder().exists()) {
      plugin.getDataFolder().mkdirs();
    }
    
    configFile = new File(plugin.getDataFolder(), fileName);
    
    // 如果文件不存在则创建
    if (!configFile.exists()) {
      try {
        if (configFile.createNewFile()) {
          LoggerUtils.success("创建新配置文件: " + fileName);
        }
      } catch (IOException e) {
        LoggerUtils.warning("无法创建配置文件 " + fileName + ": " + e.getMessage());
      }
    }
    
    // 加载配置
    config = YamlConfiguration.loadConfiguration(configFile);
  }
  
  @Override
  public void save() {
    if (config == null || configFile == null) {
      return;
    }
    
    try {
      config.save(configFile);
    } catch (IOException e) {
      LoggerUtils.warning("无法保存配置文件 " + fileName + ": " + e.getMessage());
    }
  }
  
  @Override
  public void reload() {
    if (configFile != null) {
      config = YamlConfiguration.loadConfiguration(configFile);
    }
  }
  
  @Override
  public FileConfiguration getConfig() {
    return config;
  }
}
