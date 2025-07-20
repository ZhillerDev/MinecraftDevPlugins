package top.zhillerdev.functions.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import top.zhillerdev.functions.events.mining.ChainMiningProcessor;
import top.zhillerdev.functions.events.mining.MatrixMiningProcessor;

public class BlockInteractEvents implements Listener {
  
  private final ChainMiningProcessor chainMiningProcessor = new ChainMiningProcessor();
  private final MatrixMiningProcessor matrixMiningProcessor = new MatrixMiningProcessor();
  
  @EventHandler
  public void onBlockPlaceEvent(BlockPlaceEvent e) {
  
  }
  
  @EventHandler
  public void onBlockBreakEvent(BlockBreakEvent e) {
    // 1. 前置检查：如果事件被取消则直接返回
    if (e.isCancelled()) {
      return;
    }
    
    chainMiningProcessor.process(e); // 连锁砍树与采矿功能
    matrixMiningProcessor.processMatrixModeBlockBreak(e);
  }
}
