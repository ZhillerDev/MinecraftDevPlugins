package top.zhillerdev.functions.events.mining;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import top.zhillerdev.functions.config.ChainMiningConfig;

public class MatrixMiningProcessor {
  public void process(BlockBreakEvent e) {
    Player player = e.getPlayer();
    Block block = e.getBlock();
    ItemStack item = player.getInventory().getItemInMainHand();
    if (!MiningList.TOOLS_PICKAXE.contains(item.getType())) {
      return;
    }
    if (!ChainMiningConfig.getInstance().getStatus(player)) {
      return;
    }
    ChainMiningConfig.getInstance().setStatus(player, false);
  }
}
