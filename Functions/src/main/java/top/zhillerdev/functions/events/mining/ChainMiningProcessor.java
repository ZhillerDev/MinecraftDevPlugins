package top.zhillerdev.functions.events.mining;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import top.zhillerdev.functions.config.ChainMiningConfig;

public class ChainMiningProcessor {
  
  public void process(BlockBreakEvent e) {
    // 1. 检查玩家是否开启连锁采矿
    boolean isEnabled = ChainMiningConfig.getInstance().getChainMiningStatus(e.getPlayer());
    if (!isEnabled) {
      return;
    }
    
    // 2. 检查工具和方块类型是否符合连锁条件
    if (!isValidToolAndBlock(e)) {
      return;
    }
    
    checkNearbyBlocks(e.getBlock(), e.getPlayer());
  }
  
  /**
   * 检查在方块破坏事件中，玩家手中的工具与被破坏的方块是否符合要求（即工具是斧头或镐子，方块是木头或矿石）
   *
   * @param e 方块破坏事件对象
   * @return 如果符合要求则返回true，否则返回false
   */
  private boolean isValidToolAndBlock(BlockBreakEvent e) {
    Player player = e.getPlayer();
    ItemStack itemInHand = player.getInventory().getItemInMainHand();
    
    Material toolMaterial = itemInHand.getType();
    Material blockMaterial = e.getBlock().getType();
    
    // 判断工具是否是斧头且方块是木头类型
    boolean isAxeAndLog = MiningList.TOOLS_AXE.contains(toolMaterial) && MiningList.BLOCKS_LOG.contains(blockMaterial);
    // 判断工具是否是镐子且方块是矿石类型
    boolean isPickaxeAndOre = MiningList.TOOLS_PICKAXE.contains(toolMaterial) && MiningList.BLOCKS_ORE.contains(blockMaterial);
    
    return isAxeAndLog || isPickaxeAndOre;
  }
  
  // 用于检查给定方块周围的方块情况，并根据规则进行相应处理，例如破坏周围符合条件的方块并处理工具的耐久度等
  private void checkNearbyBlocks(Block block, Player player) {
    // 初始化三个循环变量，用于控制在方块周围的三维空间中遍历查找
    int x = -1;
    int y = -1;
    int z = -1;
    
    // 标记这个外层循环，方便后续在内层循环中跳出多层循环时使用（break outerLoop）
    outerLoop:
    for (int k = 0; k < 3; k++) {
      
      for (int i = 0; i < 3; i++) {
        
        for (int j = 0; j < 3; j++) {
          // 获取给定方块的位置信息，并克隆一份（避免直接修改原方块位置对象）
          Location center = block.getLocation().clone();
          
          // 根据当前循环变量的值，在克隆的位置上进行偏移，定位到周围的方块位置
          center.add(x, y, z);
          
          // 获取位于偏移后位置的方块对象
          Block nearbyBlock = center.getBlock();
          
          // 判断周围方块的类型是否在TreeFella.LOGS列表或者TreeFella.ORES列表中，如果是则执行以下逻辑
          if (MiningList.BLOCKS_LOG.contains(nearbyBlock.getType()) || MiningList.BLOCKS_ORE.contains(nearbyBlock.getType())) {
            // 获取玩家主手中持有的工具物品
            ItemStack tool = player.getInventory().getItemInMainHand();
            
            // 以自然的方式（类似玩家手动破坏的效果）破坏周围这个符合条件的方块，传入工具物品作为参数（可能会根据工具的属性影响破坏过程）
            nearbyBlock.breakNaturally(tool);
            
            // 获取当前工具的最大耐久度（不同类型工具的最大耐久度不同）
            int toolDurability = tool.getType().getMaxDurability();
            
            // 获取工具的元数据（包含物品的各种额外属性信息，如显示名称、自定义模型数据等）
            ItemMeta meta = tool.getItemMeta();
            
            // 将元数据对象强制转换为Damageable类型（用于处理可损坏的物品，获取和设置其损坏值等操作）
            Damageable dmg = (Damageable) meta;
            
            // 判断Damageable对象不为空且当前工具的损坏值小于等于最大耐久度（即工具还没损坏到无法使用的程度）
            if (dmg != null && dmg.getDamage() <= toolDurability) {
              // 将工具的损坏值增加1，表示工具使用后耐久度减少了1
              dmg.setDamage(dmg.getDamage() + 1);
              
              // 将更新后的Damageable对象（包含新的损坏值）设置回工具的元数据中，实现对工具耐久度的更新
              tool.setItemMeta(dmg);
              
              // 将更新后的工具放回玩家主手物品栏中，完成工具在玩家物品栏中的耐久度更新显示等操作
              player.getInventory().setItemInMainHand(tool);
              // 如果工具已经损坏到无法使用（Damageable对象为空或者损坏值超过最大耐久度），执行以下逻辑
            } else {
              // 将玩家主手物品栏中的物品设置为空，表示工具已损坏消失
              player.getInventory().setItemInMainHand(null);
              // 在玩家所在位置播放一个物品损坏的音效，音效的音量为1F，音调为1F（具体音效效果由游戏内资源决定）
              player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
              // 直接跳出outerLoop标记的外层循环，停止对周围方块的检查（可能因为工具损坏了就不再继续处理了）
              break outerLoop;
            }
            
            // 递归调用checkNearbyBlocks方法，继续检查当前被破坏的这个附近方块周围的方块情况，实现连锁破坏等效果
            this.checkNearbyBlocks(nearbyBlock, player);
            
          }
          
          // Z坐标值自增，用于在每次内层循环中遍历不同的Z位置的方块
          z++;
        }
        
        // X坐标值自增，用于在每次中层循环中遍历不同的X位置的方块，同时将Z坐标重置为 -1，准备下一轮内层循环对Z坐标的遍历
        x++;
        z = -1;
      }
      
      // Y坐标值自增，用于在每次外层循环中遍历不同的Y位置的方块，同时将X坐标和Z坐标重置为 -1，准备下一轮中层和内层循环对X、Z坐标的遍历
      y++;
      x = -1;
    }
  }
}
