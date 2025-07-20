package top.zhillerdev.functions.config;

import org.bukkit.entity.Player;
import top.zhillerdev.functions.FunctionsMain;
import top.zhillerdev.functions.config.base.ConfigProvider;

public class ChainMiningConfig extends ConfigProvider {
  // 单例实例
  private static ChainMiningConfig instance;
  
  // 配置键前缀定义
  private static final String CHAIN_MINING_PREFIX = "players.%s.chain-mining";
  private static final String AUTO_SOWING_PREFIX = "players.%s.auto-sowing";
  
  /**
   * 私有构造函数，指定配置文件名
   */
  private ChainMiningConfig() {
    super("chain-mining.yml");
  }
  
  /**
   * 获取单例实例
   *
   * @return ChainMiningConfig实例
   */
  public static synchronized ChainMiningConfig getInstance() {
    if (instance == null) {
      instance = new ChainMiningConfig();
    }
    return instance;
  }
  
  /**
   * 初始化配置，使用插件主类
   */
  public static void initialize() {
    getInstance().setup(FunctionsMain.getPlugin(FunctionsMain.class));
  }
  
  /**
   * 获取玩家的连锁采矿状态
   *
   * @param player 玩家对象
   * @return 状态(true为开启, false为关闭, 默认为true)
   */
  public boolean getChainMiningStatus(Player player) {
    String playerName = player.getName();
    String path = String.format(CHAIN_MINING_PREFIX, playerName);
    // 配置中没有记录时默认开启
    return getConfig().getBoolean(path, true);
  }
  
  /**
   * 设置玩家的连锁采矿状态
   *
   * @param player 玩家对象
   * @param status 状态(true为开启, false为关闭)
   */
  public void setChainMiningStatus(Player player, boolean status) {
    String playerName = player.getName();
    String path = String.format(CHAIN_MINING_PREFIX, playerName);
    getConfig().set(path, status);
    save();
  }
  
  /**
   * 获取玩家的自动种田状态
   *
   * @param player 玩家对象
   * @return 状态(true为开启, false为关闭, 默认为false)
   */
  public boolean getAutoSowingStatus(Player player) {
    String playerName = player.getName();
    String path = String.format(AUTO_SOWING_PREFIX, playerName);
    // 配置中没有记录时默认关闭
    return getConfig().getBoolean(path, false);
  }
  
  /**
   * 设置玩家的自动种田状态
   *
   * @param player 玩家对象
   * @param status 状态(true为开启, false为关闭)
   */
  public void setAutoSowingStatus(Player player, boolean status) {
    String playerName = player.getName();
    String path = String.format(AUTO_SOWING_PREFIX, playerName);
    getConfig().set(path, status);
    save();
  }
}
