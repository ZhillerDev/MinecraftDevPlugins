package top.zhillerdev.functions.events.tpr;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TprUtils {
  // 冷却时间存储 (玩家UUID -> 冷却结束时间戳)
  private static final Map<UUID, Long> cooldowns = new HashMap<>();
  private static final int COOLDOWN_SECONDS = 60;
  private static final int RANDOM_TELEPORT_RANGE = 1000;
  
  /**
   * 执行随机传送
   * @param player 要传送的玩家
   */
  public static void randomTeleport(Player player) {
    World world = player.getWorld();
    Location currentLoc = player.getLocation();
    
    // 生成1000范围内的随机坐标
    double x = currentLoc.getX() + (Math.random() * 2000 - 1000);
    double z = currentLoc.getZ() + (Math.random() * 2000 - 1000);
    double y = world.getHighestBlockYAt((int) x, (int) z) + 1; // 获取最高方块上方
    
    Location targetLoc = new Location(world, x, y, z);
    player.teleport(targetLoc);
  }
  
  /**
   * 将玩家传送到目标玩家位置
   * @param requester 发起传送的玩家
   * @param target 目标玩家
   */
  public static void teleportToPlayer(Player requester, Player target) {
    Location targetLoc = target.getLocation().clone().add(0, 1, 0); // 目标位置上方1格
    requester.teleport(targetLoc);
  }
  
  /**
   * 设置玩家冷却时间
   * @param player 玩家
   */
  public static void setCooldown(Player player) {
    cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + (COOLDOWN_SECONDS * 1000));
  }
  
  /**
   * 检查冷却时间是否已过
   * @param player 玩家
   * @return 冷却是否已结束
   */
  public static boolean isCooldownExpired(Player player) {
    Long cooldownEnd = cooldowns.get(player.getUniqueId());
    return cooldownEnd == null || System.currentTimeMillis() >= cooldownEnd;
  }
  
  /**
   * 获取剩余冷却时间(毫秒)
   * @param player 玩家
   * @return 剩余毫秒数
   */
  public static long getRemainingCooldown(Player player) {
    Long cooldownEnd = cooldowns.get(player.getUniqueId());
    if (cooldownEnd == null) return 0;
    
    long remaining = cooldownEnd - System.currentTimeMillis();
    return Math.max(0, remaining);
  }
}
