package top.zhillerdev.functions;

import org.bukkit.plugin.java.JavaPlugin;
import top.zhillerdev.functions.utils.LoggerUtils;

public final class FunctionsMain extends JavaPlugin {
  
  @Override
  public void onEnable() {
    // 注册命令
    PluginRegister pr = new PluginRegister(this);
    pr.registerConfigs();
    pr.registerCommands();
    pr.registerEvents();
    
    LoggerUtils.info("插件已经启动");
  }
  
  @Override
  public void onLoad() {
    super.onLoad();
  }
  
  @Override
  public void onDisable() {
    LoggerUtils.info("插件已经关闭");
  }
}
