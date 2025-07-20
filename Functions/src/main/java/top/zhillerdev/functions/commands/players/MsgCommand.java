package top.zhillerdev.functions.commands.players;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class MsgCommand implements CommandExecutor {
  
  
  
  public MsgCommand(JavaPlugin plugin) {
  }
  
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
    // 1. 参数不足时的提示（红色文本）
    if (args.length < 2) {
      // 修改用法提示里的指令部分为"fmsg"
      Component usage = Component.text("用法: /fmsg <玩家> <消息>").color(NamedTextColor.RED);
      sender.sendMessage(usage);
      return true;
    }
    
    String targetPlayerName = args[0];
    Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
    OfflinePlayer offlineTarget = Bukkit.getOfflinePlayerIfCached(targetPlayerName);
    
    // 2. 无效玩家提示（红色文本）
    if (targetPlayer == null && (offlineTarget == null ||!offlineTarget.hasPlayedBefore())) {
      Component invalidPlayer = Component.text("错误: 玩家 " + targetPlayerName + " 不是有效玩家!").color(NamedTextColor.RED);
      sender.sendMessage(invalidPlayer);
      return true;
    }
    
    // 3. 玩家不在线提示（红色文本）
    if (targetPlayer == null) {
      Component notOnline = Component.text("错误: 玩家 " + targetPlayerName + " 不在线!").color(NamedTextColor.RED);
      sender.sendMessage(notOnline);
      return true;
    }
    
    // 构建消息内容
    StringBuilder messageBuilder = new StringBuilder();
    for (int i = 1; i < args.length; i++) {
      messageBuilder.append(args[i]).append(" ");
    }
    String message = messageBuilder.toString().trim();
    
    String senderName = sender instanceof Player? sender.getName() : "服务器";
    Component senderPrefix = Component.text("你向 [" + targetPlayer.getName() + "] 发送了私信：").color(NamedTextColor.GREEN);
    Component senderMessage = Component.text(message).color(NamedTextColor.WHITE);
    Component senderMsg = senderPrefix.append(senderMessage);
    sender.sendMessage(senderMsg);
    
    // 5. 接收者的私信内容（前半段绿色，message白色）
    Component targetPrefix = Component.text("[" + senderName + "] 向你发送了私信：").color(NamedTextColor.GREEN);
    Component targetMessage = Component.text(message).color(NamedTextColor.WHITE);
    Component targetMsg = targetPrefix.append(targetMessage);
    targetPlayer.sendMessage(targetMsg);
    
    return true;
  }
}