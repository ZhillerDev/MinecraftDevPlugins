package top.zhillerdev.functions.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import top.zhillerdev.functions.FunctionsMain;
import top.zhillerdev.functions.commands.datas.HelpCommand;
import top.zhillerdev.functions.commands.players.GiftCommand;
import top.zhillerdev.functions.commands.players.MsgCommand;

public class FuncCommand implements CommandExecutor {
  private final FunctionsMain plugin;
  
  public FuncCommand(FunctionsMain plugin) {
    this.plugin = plugin;
  }
  
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
    if (args.length == 0) {
      // 当只输入 /f 时，提示可用的二级指令
      Component msgCommand = Component.text("[Functions] 请使用/f help来查看更多指令用法")
          .color(NamedTextColor.GOLD);
      sender.sendMessage(msgCommand);
      return true;
    }
    
    // 提取二级指令名称（忽略大小写）
    String subCommand = args[0].toLowerCase();
    
    // 使用switch语句判断二级指令
    switch (subCommand) {
      case "msg":
        return handleMsgCommand(sender, command, label, args);
      
      case "gift":
        return handleGitCommand(sender, command, label, args);
      
      case "help":
        return handleHelpCommand(sender, command, label, args);
      
      default:
        // 未知二级指令时的提示
        Component msgCommand = Component.text("[Functions] 当前输入的指令无法识别")
            .color(NamedTextColor.RED);
        sender.sendMessage(msgCommand);
    }
    return false;
  }
  
  /**
   * 处理msg二级指令的逻辑
   *
   * @param sender  命令发送者
   * @param command 命令对象
   * @param label   命令标签
   * @param args    原始命令参数
   * @return 命令是否处理成功
   */
  private boolean handleMsgCommand(CommandSender sender, Command command, String label, String[] args) {
    // 去除二级指令名称参数，传递剩余参数给MsgCommand
    String[] msgArgs = extractArgs(args);
    // 实例化MsgCommand并执行命令
    return new MsgCommand(plugin).onCommand(sender, command, label, msgArgs);
  }
  
  private boolean handleGitCommand(CommandSender sender, Command command, String label, String[] args) {
    // 去除二级指令名称参数，传递剩余参数给MsgCommand
    String[] msgArgs = extractArgs(args);
    // 实例化MsgCommand并执行命令
    return new GiftCommand(plugin).onCommand(sender, command, label, msgArgs);
  }
  
  
  private boolean handleHelpCommand(CommandSender sender, Command command, String label, String[] args) {
    // 添加其他二级指令处理逻辑
    return new HelpCommand().onCommand(sender, command, label, args);
  }
  
  private static String @NotNull [] extractArgs(String[] args) {
    String[] msgArgs = new String[args.length - 1];
    System.arraycopy(args, 1, msgArgs, 0, args.length - 1);
    return msgArgs;
  }
  
}
