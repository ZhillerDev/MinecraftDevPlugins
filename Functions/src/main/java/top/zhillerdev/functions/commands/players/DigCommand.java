package top.zhillerdev.functions.commands.players;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.zhillerdev.functions.FunctionsMain;
import top.zhillerdev.functions.config.ChainMiningConfig;

public class DigCommand implements CommandExecutor {
  private final FunctionsMain plugin;
  
  public DigCommand(FunctionsMain plugin) {
    this.plugin = plugin;
  }
  
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
    // 1. 检查是否为玩家执行命令
    if (!(sender instanceof Player player)) {
      Component error = Component.text("错误: 只有玩家可以使用此命令!").color(NamedTextColor.RED);
      sender.sendMessage(error);
      return true;
    }
    
    // 2. 参数不足时的提示
    if (args.length < 1) {
      Component usage = Component.text("用法: /fdig <on|off>").color(NamedTextColor.RED);
      player.sendMessage(usage);
      return true;
    }
    
    // 3. 验证参数是否为on或off
    if (!args[0].equalsIgnoreCase("on") && !args[0].equalsIgnoreCase("off")) {
      Component invalidArg = Component.text("错误: 参数必须是 on 或 off!").color(NamedTextColor.RED);
      player.sendMessage(invalidArg);
      return true;
    }
    
    // 4. 更新玩家的连锁采矿状态
    boolean newState = args[0].equalsIgnoreCase("on");
    ChainMiningConfig.getInstance().setStatus(player, newState);
    
    // 5. 发送状态更新提示
    Component message = newState
        ? Component.text("连锁采矿采矿功能已开启").color(NamedTextColor.GREEN)
        : Component.text("连锁采矿采矿功能已关闭").color(NamedTextColor.GREEN);
    player.sendMessage(message);
    
    return true;
  }
}
