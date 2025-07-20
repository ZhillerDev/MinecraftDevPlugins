package top.zhillerdev.functions.events.mining;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class AutoSowingProcessor {
  // 判断是否是可处理的农作物（排除西瓜、南瓜这类）
  private boolean isProcessableCrop(Material material) {
    return material == Material.WHEAT ||
        material == Material.CARROTS ||
        material == Material.POTATOES ||
        material == Material.BEETROOTS ||
        material == Material.NETHER_WART;
  }
  
  // 根据被破坏的农作物方块获取对应的初始作物方块类型
  private Material getInitialCropBlockType(Material blockMaterial) {
    return switch (blockMaterial) {
      case WHEAT -> Material.WHEAT;
      case CARROTS -> Material.CARROTS;
      case POTATOES -> Material.POTATOES;
      case BEETROOTS -> Material.BEETROOTS;
      case NETHER_WART -> Material.NETHER_WART;
      default -> null;
    };
  }
  
  public void process(BlockBreakEvent e) {
    Player player = e.getPlayer();
    Block brokenBlock = e.getBlock();
    Material blockType = brokenBlock.getType();
    
    // 只处理可处理的农作物被破坏的情况（排除西瓜、南瓜等）
    if (!isProcessableCrop(blockType)) {
      return;
    }
    
    Material initialCropBlockType = getInitialCropBlockType(blockType);
    if (initialCropBlockType == null) {
      return;
    }
    
    Location plantLocation = brokenBlock.getLocation();
    // 直接将对应初始作物方块放置在原位置
    Block plantedBlock = plantLocation.getBlock();
    plantedBlock.setType(initialCropBlockType);
    plantedBlock.getState().update(true);
  }
}
