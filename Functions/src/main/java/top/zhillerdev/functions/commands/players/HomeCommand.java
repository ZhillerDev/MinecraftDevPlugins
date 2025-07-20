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

public class HomeCommand implements CommandExecutor {
  public HomeCommand(FunctionsMain plugin) {
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
      // 无参数 - 执行回家操作
      return teleportToHome(player);
    } else if (args.length == 1 && args[0].equalsIgnoreCase("set")) {
      // 带set参数 - 设置家的位置
      return setHomeLocation(player);
    } else {
      // 参数错误 - 显示正确用法
      sender.sendMessage(Component.text("用法错误！正确用法: /fhome [set]")
          .color(NamedTextColor.RED));
      return true;
    }
  }
  
  /**
   * 设置玩家的家为当前位置
   *
   * @param player 执行命令的玩家
   * @return 命令执行状态
   */
  private boolean setHomeLocation(Player player) {
    // 获取TpConfig实例并设置家的位置
    TpConfig.getInstance().setHome(player, player.getLocation());
    
    // 发送成功消息
    player.sendMessage(Component.text("✅ 家的位置已成功设置！")
        .color(NamedTextColor.GREEN));
    return true;
  }
  
  /**
   * 将玩家传送到     * @param player 执行命令的玩家
   *
   * @return 命令执行状态
   */
  private boolean teleportToHome(Player player) {
    TpConfig tpConfig = TpConfig.getInstance();
    
    // 检查家是否已设置
    if (!tpConfig.hasHome(player)) {
      player.sendMessage(Component.text("❌ 你还没有设置家，请先使用 /fhome set 命令设置家的位置")
          .color(NamedTextColor.RED));
      return true;
    }
    
    // 获取家的位置
    Location homeLocation = tpConfig.getHome(player);
    if (homeLocation == null) {
      player.sendMessage(Component.text("❌ 家的位置数据损坏，请重新设置家")
          .color(NamedTextColor.RED));
      return true;
    }
    
    // 检查目标位置是否安全（无方块阻挡）
    if (!isLocationSafe(homeLocation)) {
      player.sendMessage(Component.text("❌ 家的位置有方块阻挡，无法传送，请先清理该区域")
          .color(NamedTextColor.RED));
      return true;
    }
    
    // 执行传送
    player.teleport(homeLocation);
    player.sendMessage(Component.text("✅ 已成功传送回家！")
        .color(NamedTextColor.GREEN));
    return true;
  }
  
  /**
   * 检查位置是否安全（无方块阻挡）
   *
   * @param location 需要检查的位置
   * @return 位置是否安全
   */
  private boolean isLocationSafe(Location location) {
    // 检查玩家站立的方块是否为空气或液体
    Block feetBlock = location.getBlock();
    if (feetBlock.getType().isSolid()) {
      return false;
    }
    
    // 检查玩家头部位置是否有方块
    Block headBlock = location.clone().add(0, 1, 0).getBlock();
    if (headBlock.getType().isSolid()) {
      return false;
    }
    
    // 检查玩家脚下是否有实体方块（防止掉下去）
    Block belowBlock = location.clone().add(0, -1, 0).getBlock();
    return belowBlock.getType().isSolid();
  }
}
