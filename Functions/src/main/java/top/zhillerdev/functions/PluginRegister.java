package top.zhillerdev.functions;

import org.bukkit.plugin.PluginManager;
import top.zhillerdev.functions.commands.datas.HelpCommand;
import top.zhillerdev.functions.commands.datas.InspectCommand;
import top.zhillerdev.functions.commands.guis.WorkCommand;
import top.zhillerdev.functions.commands.players.*;
import top.zhillerdev.functions.config.ChainMiningConfig;
import top.zhillerdev.functions.config.TpConfig;
import top.zhillerdev.functions.events.BlockInteractEvents;
import top.zhillerdev.functions.events.PlayerActionEvents;
import top.zhillerdev.functions.utils.LoggerUtils;

import java.util.Objects;

public class PluginRegister {
  private final FunctionsMain plugin;
  private final PluginManager pm;
  
  public PluginRegister(FunctionsMain plugin) {
    this.plugin = plugin;
    pm = plugin.getServer().getPluginManager();
    
    LoggerUtils.initialize(plugin);
  }
  
  public void registerCommands() {
    Objects.requireNonNull(plugin.getCommand("fmsg")).setExecutor(new MsgCommand(plugin));
    Objects.requireNonNull(plugin.getCommand("fgift")).setExecutor(new GiftCommand(plugin));
    Objects.requireNonNull(plugin.getCommand("fhelp")).setExecutor(new HelpCommand());
    Objects.requireNonNull(plugin.getCommand("ftpr")).setExecutor(new TprCommand(plugin));
    Objects.requireNonNull(plugin.getCommand("fdig")).setExecutor(new DigCommand(plugin));
    Objects.requireNonNull(plugin.getCommand("fhome")).setExecutor(new HomeCommand(plugin));
    Objects.requireNonNull(plugin.getCommand("fnav")).setExecutor(new NavCommand(plugin));
    Objects.requireNonNull(plugin.getCommand("fins")).setExecutor(new InspectCommand(plugin));
    Objects.requireNonNull(plugin.getCommand("ftpr")).setExecutor(new TprCommand(plugin));
    Objects.requireNonNull(plugin.getCommand("fwork")).setExecutor(new WorkCommand(plugin));
  }
  
  public void registerEvents() {
    pm.registerEvents(new BlockInteractEvents(), plugin);
    pm.registerEvents(new PlayerActionEvents(), plugin);
  }
  
  
  public void registerConfigs() {
    // 初始化配置文件
    ChainMiningConfig.initialize();
    TpConfig.initialize();
  }
}
