package top.zhillerdev.functions.config.base;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public interface IConfigProvider {
  /**
   * 初始化配置文件
   *
   * @param plugin 插件实例
   */
  void setup(JavaPlugin plugin);
  
  /**
   * 保存配置文件
   */
  void save();
  
  /**
   * 重新加载配置文件
   */
  void reload();
  
  /**
   * 获取配置文件对象
   *
   * @return FileConfiguration实例
   */
  FileConfiguration getConfig();
}
