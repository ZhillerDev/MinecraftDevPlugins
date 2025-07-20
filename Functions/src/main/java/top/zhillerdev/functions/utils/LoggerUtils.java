package top.zhillerdev.functions.utils;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class LoggerUtils {
  private static Logger logger;
  
  // 彩色代码常量
  public static final String ANSI_RESET = "\u001B[0m";
  public static final String ANSI_RED = "\u001B[31m";
  public static final String ANSI_GREEN = "\u001B[32m";
  public static final String ANSI_YELLOW = "\u001B[33m";
  public static final String ANSI_BLUE = "\u001B[34m";
  public static final String ANSI_PURPLE = "\u001B[35m";
  
  /**
   * 初始化全局Logger
   *
   * @param plugin 插件主类实例
   */
  public static void initialize(JavaPlugin plugin) {
    if (logger == null) {
      logger = plugin.getLogger();
    }
  }
  
  /**
   * 获取全局Logger实例
   *
   * @return 全局Logger
   */
  public static Logger getLogger() {
    if (logger == null) {
      throw new IllegalStateException("GlobalLogger尚未初始化，请先调用initialize方法");
    }
    return logger;
  }
  
  // 彩色日志方法
  public static void info(String message) {
    logger.info(ANSI_GREEN + message + ANSI_RESET);
  }
  
  public static void warning(String message) {
    logger.warning(ANSI_YELLOW + message + ANSI_RESET);
  }
  
  public static void severe(String message) {
    logger.severe(ANSI_RED + message + ANSI_RESET);
  }
  
  public static void debug(String message) {
    logger.info(ANSI_BLUE + "[DEBUG] " + message + ANSI_RESET);
  }
  
  public static void success(String message) {
    logger.info(ANSI_PURPLE + "[SUCCESS] " + message + ANSI_RESET);
  }
}
