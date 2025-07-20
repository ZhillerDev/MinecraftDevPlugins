package top.zhillerdev.functions.commands.players;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.zhillerdev.functions.FunctionsMain;
import top.zhillerdev.functions.config.ChainMiningConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DigCommand implements CommandExecutor, TabCompleter {
  private final ChainMiningConfig config;
  
  public DigCommand(FunctionsMain plugin) {
    this.config = ChainMiningConfig.getInstance();
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
    if (args.length < 2) {
      Component usage = Component.text("用法: /fdig <mining|sowing> <on|off>")
          .color(NamedTextColor.RED);
      player.sendMessage(usage);
      player.sendMessage(Component.text("示例: /fdig mining on - 开启连锁采集")
          .color(NamedTextColor.GRAY));
      player.sendMessage(Component.text("示例: /fdig sowing off - 关闭自动播种")
          .color(NamedTextColor.GRAY));
      return true;
    }
    
    // 3. 解析模式类型 (mining/sowing)
    String mode = args[0].toLowerCase();
    if (!mode.equals("mining") && !mode.equals("sowing")) {
      Component invalidMode = Component.text("错误: 模式必须是 mining 或 sowing!")
          .color(NamedTextColor.RED);
      player.sendMessage(invalidMode);
      return true;
    }
    
    // 4. 解析开关状态 (on/off)
    if (!args[1].equalsIgnoreCase("on") && !args[1].equalsIgnoreCase("off")) {
      Component invalidArg = Component.text("错误: 状态必须是 on 或 off!")
          .color(NamedTextColor.RED);
      player.sendMessage(invalidArg);
      return true;
    }
    boolean newState = args[1].equalsIgnoreCase("on");
    
    // 5. 根据模式更新对应状态
    if (mode.equals("mining")) {
      config.setChainMiningStatus(player, newState);
    } else {
      config.setAutoSowingStatus(player, newState);
    }
    
    // 6. 发送状态状态更新提示
    String modeName = mode.equals("mining") ? "连锁锁采集" : "自动播种";
    Component message = Component.text(modeName + "功能已" + (newState ? "开启" : "关闭"))
        .color(NamedTextColor.GREEN);
    player.sendMessage(message);
    
    return true;
  }
  
  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
    // 只允许玩家使用Tab补全
    if (!(sender instanceof Player)) {
      return Collections.emptyList();
    }
    
    // 第一个参数补全：模式选择 (mining/sowing)
    if (args.length == 1) {
      List<String> modes = Arrays.asList("mining", "sowing");
      List<String> completions = new ArrayList<>();
      // 匹配输入的部分内容
      StringUtil.copyPartialMatches(args[0], modes, completions);
      return completions;
    }
    
    // 第二个参数补全：状态选择 (on/off)
    if (args.length == 2) {
      // 检查第一个参数是否为有效的模式
      String mode = args[0].toLowerCase();
      if (mode.equals("mining") || mode.equals("sowing")) {
        List<String> states = Arrays.asList("on", "off");
        List<String> completions = new ArrayList<>();
        // 匹配输入的部分内容
        StringUtil.copyPartialMatches(args[1], states, completions);
        return completions;
      }
    }
    
    // 超过2个参数时无补全
    return Collections.emptyList();
  }
}
