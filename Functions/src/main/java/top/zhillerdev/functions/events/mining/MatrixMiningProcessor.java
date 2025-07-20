package top.zhillerdev.functions.events.mining;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import top.zhillerdev.functions.FunctionsMain;
import top.zhillerdev.functions.config.ChainMiningConfig;

import java.util.HashSet;
import java.util.Set;

public class MatrixMiningProcessor {
  public static final FunctionsMain PLUGIN = FunctionsMain.getPlugin(FunctionsMain.class);
  // 用于存储Matrix模式状态的键
  private final NamespacedKey matrixModeKey;
  
  // 用于记录正在处理右键点击事件的玩家，避免重复触发切换模式
  private final Set<Player> processingRightClick = new HashSet<>();
  
  public MatrixMiningProcessor() {
    this.matrixModeKey = new NamespacedKey(PLUGIN, "matrix_mode");
  }
  
  public void processMatrixMode(PlayerInteractEvent e) {
    // 只玩家右键点击才触发
    Player player = e.getPlayer();
    ItemStack mainHandItem = player.getInventory().getItemInMainHand();
    
    
    if (e.getAction().toString().contains("RIGHT_CLICK")) {
      // 若主手不是镐子就不进行后续处理
      if (!MiningList.TOOLS_PICKAXE.contains(mainHandItem.getType())) {
        return;
      }
      // 如果当前玩家已经在处理右键点击事件，直接返回，避免重复触发
      if (processingRightClick.contains(player)) {
        return;
      }
      processingRightClick.add(player);
      // 检查点击的是否是空气（即没有对着方块点击）
      if (e.getClickedBlock() == null) {
        toggleMatrixMode(player, mainHandItem);
      }
      // 移除正在处理的标记，允许下次右键点击正常处理
      processingRightClick.remove(player);
      // 取消事件传递，避免不必要的交互（如放置方块）
      e.setCancelled(true);
    }
  }
  
  public void processMatrixModeBlockBreak(BlockBreakEvent e) {
    // 1. 检查玩家是否开启矩阵挖掘模式
    Player player = e.getPlayer();
    boolean isEnabled = ChainMiningConfig.getInstance().getChainMiningStatus(player);
    if (!isEnabled) {
      return;
    }
    
    // 获取玩家主手物品，用于后续判断是否处于Matrix模式
    ItemStack mainHandItem = player.getInventory().getItemInMainHand();
    
    // 2. 检查是否处于Matrix模式以及工具和方块类型是否符合条件
    if (!isInMatrixMode(mainHandItem) || !isValidToolAndBlock(e)) {
      return;
    }
    
    // 3. 处理3x3区域挖掘
    process3x3Mining(e.getBlock(), player);
  }
  
  // 切换Matrix模式状态
  private void toggleMatrixMode(Player player, ItemStack pickaxe) {
    ItemMeta meta = pickaxe.getItemMeta();
    if (meta == null) return;
    
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
    boolean isInMatrixMode = dataContainer.has(matrixModeKey, PersistentDataType.BOOLEAN)
        && Boolean.TRUE.equals(dataContainer.get(matrixModeKey, PersistentDataType.BOOLEAN));
    
    // 切换模式状态
    if (isInMatrixMode) {
      // 退出Matrix模式
      dataContainer.remove(matrixModeKey);
      player.sendMessage(Component.text("已退出Matrix模式")
          .color(NamedTextColor.RED));
    } else {
      // 进入Matrix模式
      dataContainer.set(matrixModeKey, PersistentDataType.BOOLEAN, true);
      player.sendMessage(Component.text("已进入Matrix模式")
          .color(NamedTextColor.GREEN));
    }
    
    // 应用修改后的元数据
    pickaxe.setItemMeta(meta);
  }
  
  // 辅助方法：检查物品是否处于Matrix模式
  public boolean isInMatrixMode(ItemStack item) {
    if (item == null || !item.hasItemMeta()) return false;
    
    PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
    return dataContainer.has(matrixModeKey, PersistentDataType.BOOLEAN)
        && Boolean.TRUE.equals(dataContainer.get(matrixModeKey, PersistentDataType.BOOLEAN));
  }
  
  /**
   * 检查工具和方块是否符合挖掘条件
   */
  private boolean isValidToolAndBlock(BlockBreakEvent e) {
    Player player = e.getPlayer();
    ItemStack tool = player.getInventory().getItemInMainHand();
    Material blockType = e.getBlock().getType();
    
    // 检查是否持有镐子
    if (!MiningList.TOOLS_PICKAXE.contains(tool.getType())) {
      return false;
    }
    
    // 检查方块是否为矿石、木头或岩石
    return isMineableBlock(blockType);
  }
  
  /**
   * 处理3x3区域内的方块挖掘
   */
  private void process3x3Mining(Block centerBlock, Player player) {
    ItemStack tool = player.getInventory().getItemInMainHand();
    Location centerLoc = centerBlock.getLocation();
    int blocksBroken = 0;
    
    // 遍历3x3x1区域（x轴、z轴各±1范围，y轴±1范围）
    for (int x = -1; x <= 1; x++) {
      for (int y = -1; y <= 1; y++) {
        for (int z = -1; z <= 1; z++) {
          // 跳过中心方块（已经被玩家破坏）
          if (x == 0 && y == 0 && z == 0) {
            continue;
          }
          
          // 获取当前位置的方块
          Block targetBlock = centerLoc.clone().add(x, y, z).getBlock();
          
          // 检查是否是可挖掘的方块类型
          if (isMineableBlock(targetBlock.getType())) {
            // 破坏方块
            targetBlock.breakNaturally(tool);
            blocksBroken++;
            
            // 播放破坏音效
            centerBlock.getWorld().playSound(targetBlock.getLocation(),
                Sound.BLOCK_STONE_BREAK, 0.8f, 1.0f);
          }
          // 不可挖掘的方块会自动保留，无需额外处理
        }
      }
    }
    
    // 消耗工具耐久度（每破坏一个方块消耗1点耐久）
    if (blocksBroken > 0) {
      consumeToolDurability(tool, blocksBroken, player);
    }
  }
  
  /**
   * 检查方块是否为可挖掘类型
   */
  private boolean isMineableBlock(Material blockType) {
    return MiningList.BLOCKS_ROCK.contains(blockType);
  }
  
  /**
   * 消耗工具耐久度
   */
  private void consumeToolDurability(ItemStack tool, int amount, Player player) {
    if (tool == null || tool.getType() == Material.AIR) {
      return;
    }
    
    ItemMeta meta = tool.getItemMeta();
    if (!(meta instanceof Damageable damageable)) {
      return;
    }
    
    // 检查工具是否不可破坏
    if (meta.isUnbreakable()) {
      return;
    }
    
    // 计算耐久消耗（考虑耐久附魔）
    int unbreakingLevel = tool.getEnchantmentLevel(org.bukkit.enchantments.Enchantment.UNBREAKING);
    int actualDamage = calculateActualDamage(amount, unbreakingLevel);
    
    // 应用耐久消耗
    damageable.setDamage(damageable.getDamage() + actualDamage);
    
    // 检查工具是否损坏
    if (damageable.getDamage() >= tool.getType().getMaxDurability()) {
      // 工具损坏消失
      player.getInventory().setItemInMainHand(null);
      player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
    } else {
      tool.setItemMeta(meta);
    }
  }
  
  /**
   * 计算实际耐久消耗（考虑耐久附魔）
   */
  private int calculateActualDamage(int baseDamage, int unbreakingLevel) {
    if (unbreakingLevel <= 0) {
      return baseDamage;
    }
    
    int actualDamage = 0;
    for (int i = 0; i < baseDamage; i++) {
      // 耐久附魔减少消耗概率：1/(等级+1)
      if (Math.random() < 1.0 / (unbreakingLevel + 1)) {
        actualDamage++;
      }
    }
    return actualDamage;
  }
}
