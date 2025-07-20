package top.zhillerdev.functions.commands.datas;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelpCommand implements CommandExecutor {
  // 每页显示的命令数量
  private static final int COMMANDS_PER_PAGE = 8;
  
  // 存储所有命令帮助信息
  private static final Map<String, String> commandHelpMap = new HashMap<>();
  
  // 转换为有序列表，保证显示顺序
  private static final List<Map.Entry<String, String>> commandList = new ArrayList<>();
  
  static {
    // 初始化帮助信息
    commandHelpMap.put("/fhelp [页码]", "显示帮助信息，可指定页码");
    commandHelpMap.put("/fmsg <玩家> <消息>", "向指定玩家发送私信");
    commandHelpMap.put("/fdig <功能> <参数>", "对多种连锁挖掘、采集功能进行设置或者开关");
    commandHelpMap.put("/fgift <玩家> [数量]", "向指定玩家赠送物品，数量输入0表示全部赠送");
    commandHelpMap.put("/fhome", "设置或者返回自己的家");
    commandHelpMap.put("/fnav <功能> <参数>", "设置一个或多个导航点，实现快速传送，至多10个导航点");
    commandHelpMap.put("/ftpr <玩家>", "请求传送到指定玩家身边，需对方许可");
    commandHelpMap.put("/fwork <功能>", "打开一个便携式的工作台或者随身口袋");
    commandHelpMap.put("/fins <参数>", "对周围数据进行调查");
    
    
    // 将命令放入有序列表，保持固定显示顺序
    commandList.addAll(commandHelpMap.entrySet());
  }
  
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, String[] args) {
    // 解析页码，默认为第一页
    int page = 1;
    if (args.length > 0) {
      try {
        page = Integer.parseInt(args[0]);
        if (page < 1) {
          page = 1;
        }
      } catch (NumberFormatException e) {
        sender.sendMessage(Component.text("页码必须是有效的数字！")
            .color(NamedTextColor.RED));
        return true;
      }
    }
    
    // 计算总页数
    int totalCommands = commandList.size();
    int totalPages = (int) Math.ceil((double) totalCommands / COMMANDS_PER_PAGE);
    
    // 检查页码是否有效
    if (page > totalPages) {
      sender.sendMessage(Component.text("没有这么多页！总页数：" + totalPages)
          .color(NamedTextColor.RED));
      return true;
    }
    
    // 发送标题和分页信息
    Component title = Component.text("=== 功能命令帮助 (第" + page + "/" + totalPages + "页) ===")
        .color(NamedTextColor.YELLOW);
    sender.sendMessage(title);
    
    // 计算当前页显示的命令范围
    int startIndex = (page - 1) * COMMANDS_PER_PAGE;
    int endIndex = Math.min(startIndex + COMMANDS_PER_PAGE, totalCommands);
    
    // 循环输出当前页的命令帮助
    for (int i = startIndex; i < endIndex; i++) {
      Map.Entry<String, String> entry = commandList.get(i);
      Component commandComponent = Component.text(entry.getKey())
          .color(NamedTextColor.GREEN);
      Component descriptionComponent = Component.text(" - " + entry.getValue())
          .color(NamedTextColor.WHITE);
      
      sender.sendMessage(commandComponent.append(descriptionComponent));
    }
    
    // 发送分页提示和底部信息
    Component pageInfo = Component.text("使用 /fhelp <页码> 查看其他页，例如: /fhelp 2")
        .color(NamedTextColor.GRAY);
    Component footer = Component.text("=====================================")
        .color(NamedTextColor.YELLOW);
    
    sender.sendMessage(pageInfo);
    sender.sendMessage(footer);
    
    return true;
  }
}
