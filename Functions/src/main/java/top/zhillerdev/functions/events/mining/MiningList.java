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
  
  // 可挖掘的岩石类型列表（不包括矿石和基岩）
  public static final ImmutableList<Material> BLOCKS_ROCK = ImmutableList.of(
      // 基础岩石
      Material.STONE,
      Material.COBBLESTONE,
      Material.MOSSY_COBBLESTONE,
      // 花岗岩系列
      Material.GRANITE,
      Material.POLISHED_GRANITE,
      Material.GRANITE_STAIRS,
      Material.GRANITE_SLAB,
      // 闪长岩系列
      Material.DIORITE,
      Material.POLISHED_DIORITE,
      Material.DIORITE_STAIRS,
      Material.DIORITE_SLAB,
      // 安山岩系列
      Material.ANDESITE,
      Material.POLISHED_ANDESITE,
      Material.ANDESITE_STAIRS,
      Material.ANDESITE_SLAB,
      // 砂岩系列
      Material.SANDSTONE,
      Material.SMOOTH_SANDSTONE,
      Material.CHISELED_SANDSTONE,
      Material.SANDSTONE_STAIRS,
      Material.SANDSTONE_SLAB,
      // 红砂岩系列
      Material.RED_SANDSTONE,
      Material.SMOOTH_RED_SANDSTONE,
      Material.CHISELED_RED_SANDSTONE,
      Material.RED_SANDSTONE_STAIRS,
      Material.RED_SANDSTONE_SLAB,
      // 石砖系列
      Material.STONE_BRICKS,
      Material.CRACKED_STONE_BRICKS,
      Material.CHISELED_STONE_BRICKS,
      Material.MOSSY_STONE_BRICKS,
      Material.STONE_BRICK_STAIRS,
      Material.STONE_BRICK_SLAB,
      // 下界岩石
      Material.NETHERRACK,
      Material.NETHER_BRICKS,
      Material.CRACKED_NETHER_BRICKS,
      Material.CHISELED_NETHER_BRICKS,
      Material.NETHER_BRICK_STAIRS,
      Material.NETHER_BRICK_SLAB,
      // 末地岩石
      Material.END_STONE,
      Material.END_STONE_BRICKS,
      Material.END_STONE_BRICK_STAIRS,
      Material.END_STONE_BRICK_SLAB,
      // 其他岩石
      Material.OBSIDIAN,
      Material.CRYING_OBSIDIAN,
      Material.BASALT,
      Material.POLISHED_BASALT,
      Material.BLACKSTONE,
      Material.POLISHED_BLACKSTONE,
      Material.COBBLED_DEEPSLATE,
      Material.DEEPSLATE_BRICKS,
      Material.POLISHED_DEEPSLATE
  );
}
