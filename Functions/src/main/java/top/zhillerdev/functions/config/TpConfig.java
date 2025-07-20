package top.zhillerdev.functions.config;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import top.zhillerdev.functions.FunctionsMain;
import top.zhillerdev.functions.config.base.ConfigProvider;

import java.util.ArrayList;
import java.util.List;

public class TpConfig extends ConfigProvider {
  public static final FunctionsMain PLUGIN = FunctionsMain.getPlugin(FunctionsMain.class);
  private static TpConfig instance;
  
  // 配置文件中的键前缀定义
  private static final String HOME_PREFIX = "players.%s.home";
  private static final String WAYPOINT_PREFIX = "players.%s.waypoints.%d";
  private static final String WAYPOINT_NAME_PREFIX = "players.%s.waypoints.%d.name";
  private static final String WAYPOINT_COUNT = "players.%s.waypoint-count";
  
  // 最大导航点数量限制
  public static final int MAX_WAYPOINTS = 10;
  
  private TpConfig() {
    super("player-tp.yml");
  }
  
  public static synchronized TpConfig getInstance() {
    if (instance == null) {
      instance = new TpConfig();
    }
    return instance;
  }
  
  public static void initialize() {
    getInstance().setup(PLUGIN);
  }
  
  /**
   * 保存玩家的家坐标
   *
   * @param player   玩家对象
   * @param location 家的坐标位置
   */
  public void setHome(Player player, Location location) {
    String playerName = player.getName();
    String path = String.format(HOME_PREFIX, playerName);
    
    // 保存位置信息：世界名、X、Y、Z坐标、俯仰角、偏航角
    getConfig().set(path + ".world", location.getWorld().getName());
    getConfig().set(path + ".x", location.getX());
    getConfig().set(path + ".y", location.getY());
    getConfig().set(path + ".z", location.getZ());
    getConfig().set(path + ".yaw", location.getYaw());
    getConfig().set(path + ".pitch", location.getPitch());
    
    save();
  }
  
  /**
   * 获取玩家的家坐标
   *
   * @param player 玩家对象
   * @return 家的坐标位置，未设置则返回null
   */
  public Location getHome(Player player) {
    String playerName = player.getName();
    String path = String.format(HOME_PREFIX, playerName);
    
    if (!getConfig().contains(path)) {
      return null;
    }
    
    // 从配置中读取位置信息
    String worldName = getConfig().getString(path + ".world");
    double x = getConfig().getDouble(path + ".x");
    double y = getConfig().getDouble(path + ".y");
    double z = getConfig().getDouble(path + ".z");
    float yaw = (float) getConfig().getDouble(path + ".yaw");
    float pitch = (float) getConfig().getDouble(path + ".pitch");
    
    if (worldName != null) {
      return new Location(
          PLUGIN.getServer().getWorld(worldName),
          x, y, z, yaw, pitch
      );
    }
    
    return null;
  }
  
  /**
   * 检查玩家是否设置了家
   *
   * @param player 玩家对象
   * @return 是否设置家
   */
  public boolean hasHome(Player player) {
    String playerName = player.getName();
    return getConfig().contains(String.format(HOME_PREFIX, playerName));
  }
  
  /**
   * 添加导航点
   *
   * @param player   玩家对象
   * @param location 导航点位置
   * @param name     导航点名称
   * @return 是否添加成功
   */
  public boolean addWaypoint(Player player, Location location, String name) {
    String playerName = player.getName();
    int currentCount = getWaypointCount(player);
    
    // 检查是否已达最大数量
    if (currentCount >= MAX_WAYPOINTS) {
      return false;
    }
    
    // 导航点索引从0开始
    String path = String.format(WAYPOINT_PREFIX, playerName, currentCount);
    
    // 保存导航点信息
    getConfig().set(path + ".world", location.getWorld().getName());
    getConfig().set(path + ".x", location.getX());
    getConfig().set(path + ".y", location.getY());
    getConfig().set(path + ".z", location.getZ());
    getConfig().set(path + ".yaw", location.getYaw());
    getConfig().set(path + ".pitch", location.getPitch());
    getConfig().set(String.format(WAYPOINT_NAME_PREFIX, playerName, currentCount), name);
    
    // 更新导航点计数
    getConfig().set(String.format(WAYPOINT_COUNT, playerName), currentCount + 1);
    
    save();
    return true;
  }
  
  /**
   * 获取玩家的导航点数量
   *
   * @param player 玩家对象
   * @return 导航点数量
   */
  public int getWaypointCount(Player player) {
    String playerName = player.getName();
    return getConfig().getInt(String.format(WAYPOINT_COUNT, playerName), 0);
  }
  
  /**
   * 获取指定索引的导航点
   *
   * @param player 玩家对象
   * @param index  导航点索引(0开始)
   * @return 导航点位置，不存在则返回null
   */
  public Location getWaypointLocation(Player player, int index) {
    String playerName = player.getName();
    
    // 检查索引是否有效
    if (index < 0 || index >= getWaypointCount(player)) {
      return null;
    }
    
    String path = String.format(WAYPOINT_PREFIX, playerName, index);
    
    String worldName = getConfig().getString(path + ".world");
    double x = getConfig().getDouble(path + ".x");
    double y = getConfig().getDouble(path + ".y");
    double z = getConfig().getDouble(path + ".z");
    float yaw = (float) getConfig().getDouble(path + ".yaw");
    float pitch = (float) getConfig().getDouble(path + ".pitch");
    
    return new Location(
        PLUGIN.getServer().getWorld(worldName),
        x, y, z, yaw, pitch
    );
  }
  
  /**
   * 获取指定索引的导航点名称
   *
   * @param player 玩家对象
   * @param index  导航点索引(0开始)
   * @return 导航点名称，不存在则返回null
   */
  public String getWaypointName(Player player, int index) {
    String playerName = player.getName();
    
    if (index < 0 || index >= getWaypointCount(player)) {
      return null;
    }
    
    return getConfig().getString(String.format(WAYPOINT_NAME_PREFIX, playerName, index),
        "未命名导航点" + (index + 1));
  }
  
  /**
   * 获取玩家的所有导航点
   *
   * @param player 玩家对象
   * @return 导航点列表，包含名称和位置信息
   */
  public List<Waypoint> getAllWaypoints(Player player) {
    List<Waypoint> waypoints = new ArrayList<>();
    int count = getWaypointCount(player);
    
    for (int i = 0; i < count; i++) {
      Location loc = getWaypointLocation(player, i);
      String name = getWaypointName(player, i);
      
      if (loc != null) {
        waypoints.add(new Waypoint(i, name, loc));
      }
    }
    
    return waypoints;
  }
  
  /**
   * 删除指定索引的导航点
   *
   * @param player 玩家对象
   * @param index  导航点索引
   * @return 是否删除成功
   */
  public boolean removeWaypoint(Player player, int index) {
    String playerName = player.getName();
    int count = getWaypointCount(player);
    
    if (index < 0 || index >= count) {
      return false;
    }
    
    // 移除指定索引的导航点
    getConfig().set(String.format(WAYPOINT_PREFIX, playerName, index), null);
    getConfig().set(String.format(WAYPOINT_NAME_PREFIX, playerName, index), null);
    
    // 重新排序剩余导航点
    for (int i = index + 1; i < count; i++) {
      String sourcePath = String.format(WAYPOINT_PREFIX, playerName, i);
      String targetPath = String.format(WAYPOINT_PREFIX, playerName, i - 1);
      
      getConfig().set(targetPath, getConfig().get(sourcePath));
      getConfig().set(String.format(WAYPOINT_NAME_PREFIX, playerName, i - 1),
          getConfig().getString(String.format(WAYPOINT_NAME_PREFIX, playerName, i)));
      
      // 清除原位置数据
      getConfig().set(sourcePath, null);
      getConfig().set(String.format(WAYPOINT_NAME_PREFIX, playerName, i), null);
    }
    
    // 更新导航点计数
    getConfig().set(String.format(WAYPOINT_COUNT, playerName), count - 1);
    
    save();
    return true;
  }
  
  /**
   * 导航点数据类
   */
  public static class Waypoint {
    private final int index;
    private final String name;
    private final Location location;
    
    public Waypoint(int index, String name, Location location) {
      this.index = index;
      this.name = name;
      this.location = location;
    }
    
    public int getIndex() {
      return index;
    }
    
    public String getName() {
      return name;
    }
    
    public Location getLocation() {
      return location;
    }
  }
}
