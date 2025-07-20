package top.zhillerdev.functions.commands.players;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import top.zhillerdev.functions.FunctionsMain;

public class GiftCommand implements CommandExecutor {
  
  public GiftCommand(FunctionsMain plugin) {
  }
  
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, String[] args) {
    // 验证参数数量
    if (args.length < 2) {
      // 更新用法提示里的指令部分为"fgift"
      sender.sendMessage(Component.text("用法: /fgift <玩家> <数量>")
          .color(NamedTextColor.RED));
      return true;
    }
    
    // 解析目标玩家
    String targetName = args[0];
    Player targetPlayer = Bukkit.getPlayer(targetName);
    if (targetPlayer == null) {
      sender.sendMessage(Component.text("错误: 玩家 " + targetName + " 不在线!")
          .color(NamedTextColor.RED));
      return true;
    }
    
    // 解析数量参数
    int count;
    try {
      count = Integer.parseInt(args[1]);
      if (count < 0) {
        throw new NumberFormatException();
      }
    } catch (NumberFormatException e) {
      sender.sendMessage(Component.text("错误: 数量必须是大于等于零的数!")
          .color(NamedTextColor.RED));
      return true;
    }
    
    // 检查目标玩家是否有至少一个空格子
    if (!hasAtLeastOneEmptySlot(targetPlayer)) {
      sender.sendMessage(Component.text("无法赠送物品: 玩家 " + targetName + " 的物品栏已满!")
          .color(NamedTextColor.RED));
      return true;
    }
    
    // 处理控制台发送的情况
    if (!(sender instanceof Player senderPlayer)) {
      // 创建特殊物品"[点亮你的心]"
      ItemStack touchItem = new ItemStack(Material.LIGHT, 1); // 使用光源作为基础物品
      ItemMeta meta = touchItem.getItemMeta();
      meta.displayName(Component.text("[点亮你的心]")
          .color(NamedTextColor.YELLOW));
      touchItem.setItemMeta(meta);
      
      // 发送物品
      targetPlayer.getInventory().addItem(touchItem);
      
      // 发送提示信息
      sender.sendMessage(Component.text("已向 " + targetName + " 发送特殊物品: [点亮你的心]")
          .color(NamedTextColor.GREEN));
      targetPlayer.sendMessage(Component.text("你收到了来自服务器的特殊物品: [点亮你的心]")
          .color(NamedTextColor.GREEN));
      return true;
    }
    
    // 处理玩家发送的情况
    ItemStack handItem = senderPlayer.getInventory().getItemInMainHand();
    
    // 检查手中是否有物品
    if (handItem.getType() == Material.AIR) {
      sender.sendMessage(Component.text("错误: 你的主手没有物品可赠送!")
          .color(NamedTextColor.RED));
      return true;
    }
    
    // 检查物品数量是否足够
    if (handItem.getAmount() < count) {
      sender.sendMessage(Component.text("错误: 你的主手物品数量不足" + count + "个!")
          .color(NamedTextColor.RED));
      return true;
    }
    
    // 玩家准备发送全部的物品
    if (count == 0) {
      count = handItem.getAmount();
    }
    
    // 准备要赠送的物品
    ItemStack giftItem = handItem.clone();
    giftItem.setAmount(count);
    
    // 从发送者手中扣除物品
    handItem.setAmount(handItem.getAmount() - count);
    
    // 向目标玩家添加物品
    targetPlayer.getInventory().addItem(giftItem);
    
    // 发送提示信息
    Component itemName = giftItem.getItemMeta().hasDisplayName()
        ? giftItem.getItemMeta().displayName()
        : Component.text(giftItem.getType().name().toLowerCase().replace("_", " "));
    
    if (itemName != null) {
      sender.sendMessage(Component.text("你向 " + targetName + " 赠送了 ")
          .color(NamedTextColor.GREEN)
          .append(Component.text(count + "个 ")
              .color(NamedTextColor.WHITE))
          .append(itemName.color(NamedTextColor.WHITE)));
    }
    
    if (itemName != null) {
      targetPlayer.sendMessage(Component.text("你收到了来自 " + senderPlayer.getName() + " 的 ")
          .color(NamedTextColor.GREEN)
          .append(Component.text(count + "个 ")
              .color(NamedTextColor.WHITE))
          .append(itemName.color(NamedTextColor.WHITE)));
    }
    
    return true;
  }
  
  /**
   * 检查玩家物品栏是否至少有一个完全空的格子
   *
   * @param player 目标玩家
   * @return 是否有至少一个空格子
   */
  private boolean hasAtLeastOneEmptySlot(Player player) {
    Inventory inventory = player.getInventory();
    
    // 检查物品栏中的每个格子
    for (ItemStack item : inventory.getContents()) {
      // 如果找到空格子，立即返回true
      if (item == null || item.getType() == Material.AIR) {
        return true;
      }
    }
    
    // 没有找到任何空格子
    return false;
  }
}