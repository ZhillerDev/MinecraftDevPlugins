package top.zhillerdev.functions.commands.players;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.zhillerdev.functions.FunctionsMain;
import top.zhillerdev.functions.events.tpr.TprUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TprCommand implements CommandExecutor, TabCompleter {
  public TprCommand(FunctionsMain plugin) {
  }
  
  // 用于存储玩家传送请求相关信息，键为发起请求的玩家，值为包含目标玩家、请求时间戳、定时任务ID的Map
  private Map<Player, Map<String, Object>> tprRequests = new HashMap<>();
  // 用于存储冷却时间定时任务的ID，键为玩家，值为任务ID
  private Map<Player, Integer> cooldownTasks = new HashMap<>();
  private static final int COOLDOWN_SECONDS = 60;
  private static final int REQUEST_TIMEOUT_SECONDS = 60;
  
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
    // 检查发送者是否为玩家，只有玩家能执行传送操作
    if (!(sender instanceof Player player)) {
      sender.sendMessage(Component.text("此命令只能由玩家执行！")
          .color(NamedTextColor.RED));
      return true;
    }
    
    // 无参数情况，执行随机传送相关逻辑（原代码已有部分逻辑，此处简化调用）
    if (args.length == 0) {
      return handleRandomTeleport(player);
    }
    
    // 有参数情况，判断是发起请求还是回复请求
    if (args.length == 1) {
      String input = args[0].toLowerCase();
      if ("yes".equals(input) || "no".equals(input)) {
        // 处理回复请求的情况
        handleTprResponse(player, input);
        return true;
      } else {
        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
          player.sendMessage(Component.text("找不到指定的玩家！")
              .color(NamedTextColor.RED));
          return true;
        }
        
        // 检查是否已对该玩家发起过请求且请求未过期
        if (isRequestExistsAndNotExpired(player, targetPlayer)) {
          long remainingTime = getRequestRemainingTime(player, targetPlayer);
          player.sendMessage(Component.text("你已经向该玩家发起过传送请求，剩余 " + remainingTime / 1000 + " 秒。")
              .color(NamedTextColor.YELLOW));
          return true;
        }
        
        // 发起传送请求，设置相关信息
        sendTprRequest(player, targetPlayer);
        player.sendMessage(Component.text("已向 " + targetPlayer.getName() + " 发起传送请求，请等待对方回复。")
            .color(NamedTextColor.GREEN));
        return true;
      }
    }
    
    // 参数个数不正确时，提示玩家正确的用法
    player.sendMessage(Component.text("用法: /ftpr 或者 /ftpr <玩家名字> 或者 /ftpr yes 或者 /ftpr no")
        .color(NamedTextColor.RED));
    return true;
  }
  
  // 处理随机传送逻辑，包括检查冷却时间、执行传送、设置冷却时间等
  private boolean handleRandomTeleport(Player player) {
    // 检查冷却时间是否已过
    if (!TprUtils.isCooldownExpired(player)) {
      long remainingTime = TprUtils.getRemainingCooldown(player);
      player.sendMessage(Component.text("你还在冷却时间内，剩余 " + remainingTime / 1000 + " 秒。")
          .color(NamedTextColor.YELLOW));
      return true;
    }
    
    // 执行随机传送
    TprUtils.randomTeleport(player);
    
    // 设置冷却时间，通过定时任务来实现
    int taskId = startCooldownTimer(player);
    cooldownTasks.put(player, taskId);
    
    player.sendMessage(Component.text("已随机传送到新位置！")
        .color(NamedTextColor.GREEN));
    return true;
  }
  
  // 检查是否已对指定玩家发起过请求且请求未过期
  private boolean isRequestExistsAndNotExpired(Player fromPlayer, Player toPlayer) {
    Map<String, Object> requestInfo = tprRequests.get(fromPlayer);
    if (requestInfo != null) {
      String targetPlayerName = (String) requestInfo.get("targetPlayerName");
      long requestTimestamp = (long) requestInfo.get("requestTimestamp");
      return targetPlayerName != null && targetPlayerName.equals(toPlayer.getName()) &&
          System.currentTimeMillis() - requestTimestamp < REQUEST_TIMEOUT_SECONDS * 1000;
    }
    return false;
  }
  
  // 获取传送请求剩余时间（毫秒）
  private long getRequestRemainingTime(Player fromPlayer, Player toPlayer) {
    Map<String, Object> requestInfo = tprRequests.get(fromPlayer);
    if (requestInfo != null) {
      long requestTimestamp = (long) requestInfo.get("requestTimestamp");
      return Math.max(0, REQUEST_TIMEOUT_SECONDS * 1000 - (System.currentTimeMillis() - requestTimestamp));
    }
    return 0;
  }
  
  // 发起传送请求，设置相关信息并启动定时任务来处理请求超时
  private void sendTprRequest(Player fromPlayer, Player toPlayer) {
    Map<String, Object> requestInfo = new HashMap<>();
    requestInfo.put("targetPlayerName", toPlayer.getName());
    requestInfo.put("requestTimestamp", System.currentTimeMillis());
    
    // 启动定时任务，用于处理请求超时情况
    int taskId = new BukkitRunnable() {
      @Override
      public void run() {
        tprRequests.remove(fromPlayer);
      }
    }.runTaskLater(FunctionsMain.getPlugin(FunctionsMain.class), 20 * REQUEST_TIMEOUT_SECONDS).getTaskId();
    
    requestInfo.put("taskId", taskId);
    tprRequests.put(fromPlayer, requestInfo);
    
    // 给目标玩家发送提示信息
    toPlayer.sendMessage(Component.text(fromPlayer.getName() + " 向你发起了传送请求，请回复 /ftpr yes 或 /ftpr no 。")
        .color(NamedTextColor.GREEN));
  }
  
  // 处理目标玩家回复传送请求的逻辑
  private void handleTprResponse(Player targetPlayer, String response) {
    Player fromPlayer = null;
    for (Player player : tprRequests.keySet()) {
      Map<String, Object> requestInfo = tprRequests.get(player);
      String targetName = (String) requestInfo.get("targetPlayerName");
      if (targetName != null && targetName.equals(targetPlayer.getName())) {
        fromPlayer = player;
        break;
      }
    }
    
    if (fromPlayer == null) {
      targetPlayer.sendMessage(Component.text("你没有未处理的传送请求。")
          .color(NamedTextColor.RED));
      return;
    }
    
    if ("yes".equalsIgnoreCase(response)) {
      TprUtils.teleportToPlayer(fromPlayer, targetPlayer);
      tprRequests.remove(fromPlayer);
      fromPlayer.sendMessage(Component.text("对方已同意你的传送请求，你已传送到 " + targetPlayer.getName() + " 身边。")
          .color(NamedTextColor.GREEN));
      targetPlayer.sendMessage(Component.text(fromPlayer.getName() + " 已传送到你身边。")
          .color(NamedTextColor.GREEN));
    } else if ("no".equalsIgnoreCase(response)) {
      tprRequests.remove(fromPlayer);
      fromPlayer.sendMessage(Component.text("对方拒绝了你的传送请求。")
          .color(NamedTextColor.RED));
      targetPlayer.sendMessage(Component.text("你已拒绝 " + fromPlayer.getName() + " 的传送请求。")
          .color(NamedTextColor.RED));
    } else {
      targetPlayer.sendMessage(Component.text("无效的回复，请回复 /ftpr yes 或 /ftpr no 。")
          .color(NamedTextColor.RED));
    }
  }
  
  // 启动冷却时间定时任务
  private int startCooldownTimer(Player player) {
    return new BukkitRunnable() {
      @Override
      public void run() {
        cooldownTasks.remove(player);
      }
    }.runTaskLater(FunctionsMain.getPlugin(FunctionsMain.class), 20 * COOLDOWN_SECONDS).getTaskId();
  }
  
  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
    if (args.length == 1) {
      List<String> playerNames = new ArrayList<>();
      for (Player player : Bukkit.getOnlinePlayers()) {
        playerNames.add(player.getName());
      }
      return playerNames;
    }
    return List.of();
  }
}
