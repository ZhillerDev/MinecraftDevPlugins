package top.zhillerdev.functions.commands.players;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.zhillerdev.functions.FunctionsMain;
import top.zhillerdev.functions.config.TpConfig;

import java.util.List;

public class NavCommand implements CommandExecutor {
  public NavCommand(FunctionsMain plugin) {
  }
  
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, String[] args) {
    // 检查是否为玩家执行命令
    if (!(sender instanceof Player player)) {
      sender.sendMessage(Component.text("此命令只能由玩家执行！")
          .color(NamedTextColor.RED));
      return true;
    }
    
    // 处理不同的命令参数
    if (args.length == 0) {
      showHelp(player);
      return true;
    }
    
    return switch (args[0].toLowerCase()) {
      case "set" -> handleSetWaypoint(player, args);
      case "go" -> handleGoToWaypoint(player, args);
      case "show" -> handleShowWaypoints(player);
      case "del", "delete" -> handleDeleteWaypoint(player, args);
      default -> {
        player.sendMessage(Component.text("未知命令！使用 /fnav 查看帮助")
            .color(NamedTextColor.RED));
        yield true;
      }
    };
  }
  
  /**
   * 设置导航点
   */
  private boolean handleSetWaypoint(Player player, String[] args) {
    // 检查参数
    if (args.length < 2) {
      player.sendMessage(Component.text("用法: /fnav set [名称]")
          .color(NamedTextColor.RED));
      return true;
    }
    
    TpConfig tpConfig = TpConfig.getInstance();
    // 检查是否已达最大导航点数量
    if (tpConfig.getWaypointCount(player) >= TpConfig.MAX_WAYPOINTS) {
      player.sendMessage(Component.text("已达到最大导航点数量限制 (" + TpConfig.MAX_WAYPOINTS + "个)")
          .color(NamedTextColor.RED));
      return true;
    }
    
    // 合并名称参数（支持带空格的名称）
    StringBuilder nameBuilder = new StringBuilder();
    for (int i = 1; i < args.length; i++) {
      if (i > 1) nameBuilder.append(" ");
      nameBuilder.append(args[i]);
    }
    String waypointName = nameBuilder.toString();
    
    // 添加导航点
    boolean success = tpConfig.addWaypoint(player, player.getLocation(), waypointName);
    if (success) {
      player.sendMessage(Component.text("✅ 已设置导航点: " + waypointName)
          .color(NamedTextColor.GREEN));
      player.sendMessage(Component.text("当前已设置 " + tpConfig.getWaypointCount(player) + "/" + TpConfig.MAX_WAYPOINTS + " 个导航点")
          .color(NamedTextColor.GRAY));
    } else {
      player.sendMessage(Component.text("❌ 设置导航点失败")
          .color(NamedTextColor.RED));
    }
    return true;
  }
  
  /**
   * 前往导航点
   */
  private boolean handleGoToWaypoint(Player player, String[] args) {
    // 检查参数
    if (args.length < 2) {
      player.sendMessage(Component.text("用法: /fnav go [名称]")
          .color(NamedTextColor.RED));
      return true;
    }
    
    TpConfig tpConfig = TpConfig.getInstance();
    List<TpConfig.Waypoint> waypoints = tpConfig.getAllWaypoints(player);
    
    // 检查是否有导航点
    if (waypoints.isEmpty()) {
      player.sendMessage(Component.text("你还没有设置任何导航点！")
          .color(NamedTextColor.RED));
      return true;
    }
    
    // 查找匹配的导航点（模糊匹配）
    String targetName = args[1];
    TpConfig.Waypoint targetWaypoint = findWaypointByName(player, targetName);
    
    // 检查是否找到导航点
    if (targetWaypoint == null) {
      player.sendMessage(Component.text("未找到名为 " + targetName + " 的导航点")
          .color(NamedTextColor.RED));
      return true;
    }
    
    // 检查目标位置是否安全
    if (!isLocationSafe(targetWaypoint.getLocation())) {
      player.sendMessage(Component.text("❌ 目标位置不安全，无法传送")
          .color(NamedTextColor.RED));
      return true;
    }
    
    // 执行传送
    player.teleport(targetWaypoint.getLocation());
    player.sendMessage(Component.text("✅ 已传送到导航点: " + targetWaypoint.getName())
        .color(NamedTextColor.GREEN));
    return true;
  }
  
  /**
   * 展示所有导航点
   */
  private boolean handleShowWaypoints(Player player) {
    TpConfig tpConfig = TpConfig.getInstance();
    List<TpConfig.Waypoint> waypoints = tpConfig.getAllWaypoints(player);
    
    if (waypoints.isEmpty()) {
      player.sendMessage(Component.text("你还没有设置任何导航点，使用 /fnav set [名称] 来添加")
          .color(NamedTextColor.YELLOW));
      return true;
    }
    
    // 显示导航点列表
    player.sendMessage(Component.text("=== 你的导航点列表 (" + waypoints.size() + "/" + TpConfig.MAX_WAYPOINTS + ") ===")
        .color(NamedTextColor.YELLOW));
    
    for (TpConfig.Waypoint waypoint : waypoints) {
      Location loc = waypoint.getLocation();
      String coords = String.format("(X: %.1f, Y: %.1f, Z: %.1f)",
          loc.getX(), loc.getY(), loc.getZ());
      
      Component waypointInfo = Component.text("[" + waypoint.getIndex() + "] ")
          .color(NamedTextColor.AQUA)
          .append(Component.text(waypoint.getName())
              .color(NamedTextColor.GREEN))
          .append(Component.text(" - " + coords)
              .color(NamedTextColor.GRAY));
      
      player.sendMessage(waypointInfo);
    }
    
    player.sendMessage(Component.text("使用 /fnav go [名称] 前往导航点，/fnav del [名称] 删除导航点")
        .color(NamedTextColor.GRAY));
    return true;
  }
  
  /**
   * 删除导航点
   */
  private boolean handleDeleteWaypoint(Player player, String[] args) {
    // 检查参数
    if (args.length < 2) {
      player.sendMessage(Component.text("用法: /fnav del [名称]")
          .color(NamedTextColor.RED));
      return true;
    }
    
    TpConfig tpConfig = TpConfig.getInstance();
    List<TpConfig.Waypoint> waypoints = tpConfig.getAllWaypoints(player);
    
    // 检查是否有导航点
    if (waypoints.isEmpty()) {
      player.sendMessage(Component.text("你还没有设置任何导航点，无法删除！")
          .color(NamedTextColor.RED));
      return true;
    }
    
    // 查找匹配的导航点
    String targetName = args[1];
    TpConfig.Waypoint targetWaypoint = findWaypointByName(player, targetName);
    
    // 检查是否找到导航点
    if (targetWaypoint == null) {
      player.sendMessage(Component.text("未找到名为 " + targetName + " 的导航点")
          .color(NamedTextColor.RED));
      return true;
    }
    
    // 执行删除操作
    boolean success = tpConfig.removeWaypoint(player, targetWaypoint.getIndex());
    if (success) {
      player.sendMessage(Component.text("✅ 已删除导航点: " + targetWaypoint.getName())
          .color(NamedTextColor.GREEN));
      player.sendMessage(Component.text("当前剩余 " + tpConfig.getWaypointCount(player) + "/" + TpConfig.MAX_WAYPOINTS + " 个导航点")
          .color(NamedTextColor.GRAY));
    } else {
      player.sendMessage(Component.text("❌ 删除导航点失败")
          .color(NamedTextColor.RED));
    }
    return true;
  }
  
  /**
   * 显示帮助信息
   */
  private void showHelp(Player player) {
    player.sendMessage(Component.text("=== 导航点命令帮助 ===")
        .color(NamedTextColor.YELLOW));
    player.sendMessage(Component.text("/fnav set [名称] - 设置当前位置为导航点")
        .color(NamedTextColor.WHITE));
    player.sendMessage(Component.text("/fnav go [名称] - 前往指定导航点")
        .color(NamedTextColor.WHITE));
    player.sendMessage(Component.text("/fnav show - 展示所有导航点")
        .color(NamedTextColor.WHITE));
    player.sendMessage(Component.text("/fnav del [名称] - 删除指定导航点")
        .color(NamedTextColor.WHITE));
    player.sendMessage(Component.text("最多可设置 " + TpConfig.MAX_WAYPOINTS + " 个导航点")
        .color(NamedTextColor.GRAY));
  }
  
  /**
   * 检查位置是否安全（防止传送进方块）
   */
  private boolean isLocationSafe(Location location) {
    // 检查脚下是否有实体方块
    Block belowBlock = location.clone().add(0, -1, 0).getBlock();
    if (!belowBlock.getType().isSolid()) {
      return false;
    }
    
    // 检查站立位置是否有方块
    Block feetBlock = location.getBlock();
    if (feetBlock.getType().isSolid()) {
      return false;
    }
    
    // 检查头部位置是否有方块
    Block headBlock = location.clone().add(0, 1, 0).getBlock();
    return !headBlock.getType().isSolid();
  }
  
  /**
   * 根据名称查找导航点（支持模糊匹配）
   */
  private TpConfig.Waypoint findWaypointByName(Player player, String name) {
    List<TpConfig.Waypoint> waypoints = TpConfig.getInstance().getAllWaypoints(player);
    for (TpConfig.Waypoint waypoint : waypoints) {
      if (waypoint.getName().equalsIgnoreCase(name) ||
          waypoint.getName().contains(name)) {
        return waypoint;
      }
    }
    return null;
  }
}
