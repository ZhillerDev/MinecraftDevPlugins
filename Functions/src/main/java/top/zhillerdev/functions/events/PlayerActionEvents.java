package top.zhillerdev.functions.events;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerActionEvents implements Listener {
  
  
  @EventHandler
  public void onPlayerChat(AsyncChatEvent e) {
    // 1. 前置检查：如果事件被取消则直接返回
    if (e.isCancelled()) {
      return;
    }
    
    
  }
}
