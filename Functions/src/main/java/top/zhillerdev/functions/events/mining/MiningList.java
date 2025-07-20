package top.zhillerdev.functions.events.mining;

import com.google.common.collect.ImmutableList;
import org.bukkit.Material;

public class MiningList {
  public static final ImmutableList<Material> BLOCKS_LOG = ImmutableList.of(
      Material.ACACIA_LOG,
      Material.BIRCH_LOG,
      Material.JUNGLE_LOG,
      Material.OAK_LOG,
      Material.MANGROVE_LOG,
      Material.SPRUCE_LOG,
      Material.DARK_OAK_LOG,
      Material.CHERRY_LOG,
      Material.WARPED_STEM,
      Material.CRIMSON_STEM
  );
  
  public static final ImmutableList<Material> BLOCKS_ORE = ImmutableList.of(
      Material.COAL_ORE,
      Material.COPPER_ORE,
      Material.IRON_ORE,
      Material.GOLD_ORE,
      Material.EMERALD_ORE,
      Material.DIAMOND_ORE,
      Material.LAPIS_ORE,
      Material.REDSTONE_ORE,
      Material.DEEPSLATE_COAL_ORE,
      Material.DEEPSLATE_COPPER_ORE,
      Material.DEEPSLATE_IRON_ORE,
      Material.DEEPSLATE_GOLD_ORE,
      Material.DEEPSLATE_EMERALD_ORE,
      Material.DEEPSLATE_DIAMOND_ORE,
      Material.DEEPSLATE_LAPIS_ORE,
      Material.DEEPSLATE_REDSTONE_ORE,
      Material.NETHER_QUARTZ_ORE,
      Material.NETHER_GOLD_ORE,
      Material.ANCIENT_DEBRIS
  );
  
  // 不可变列表，存储了各种类型的斧头材料，用于后续判断玩家手中工具是否为斧头
  public static final ImmutableList<Material> TOOLS_AXE = ImmutableList.of(
      Material.DIAMOND_AXE,
      Material.GOLDEN_AXE,
      Material.IRON_AXE,
      Material.STONE_AXE,
      Material.NETHERITE_AXE,
      Material.WOODEN_AXE
  );
  
  // 不可变列表，存储了各种类型的镐子材料，用于后续判断玩家手中工具是否为镐子
  public static final ImmutableList<Material> TOOLS_PICKAXE = ImmutableList.of(
      Material.DIAMOND_PICKAXE,
      Material.GOLDEN_PICKAXE,
      Material.IRON_PICKAXE,
      Material.STONE_PICKAXE,
      Material.NETHERITE_PICKAXE,
      Material.WOODEN_PICKAXE
  );
}
