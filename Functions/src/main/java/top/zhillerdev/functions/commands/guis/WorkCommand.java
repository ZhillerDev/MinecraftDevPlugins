package top.zhillerdev.functions.commands.guis;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.zhillerdev.functions.FunctionsMain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorkCommand implements CommandExecutor, TabCompleter {
  public WorkCommand(FunctionsMain plugin) {
  }
  
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
    // 检查发送者是否为玩家，只有玩家能执行相关操作打开界面
    if (!(sender instanceof Player player)) {
      sender.sendMessage("此命令只能由玩家执行！");
      return true;
    }
    
    // 根据不同的命令参数执行相应功能
    if (args.length == 1) {
      if (args[0].equalsIgnoreCase("craft")) {
        // 创建一个工作台类型的物品栏界面（即工作台界面）
        Inventory workbenchInventory = Bukkit.createInventory(null, InventoryType.WORKBENCH,
            Component.text("便携式工作台")
                .color(NamedTextColor.GOLD)
                .decoration(TextDecoration.BOLD, false));
        // 打开这个工作台界面给玩家
        player.openInventory(workbenchInventory);
      } else if (args[0].equalsIgnoreCase("anvil")) {
        // 检查玩家背包中是否有铁锭
        if (hasIronIngot(player)) {
          // 消耗玩家背包中的一块铁锭
          removeIronIngot(player);
          // 创建铁砧类型的物品栏界面（即铁砧界面）
          Inventory anvilInventory = Bukkit.createInventory(null, InventoryType.ANVIL,
              Component.text("便携式铁砧")
                  .color(NamedTextColor.GOLD)
                  .decoration(TextDecoration.BOLD, false));
          // 打开铁砧界面给玩家
          player.openInventory(anvilInventory);
        } else {
          // 如果背包没有铁锭，提示玩家
          player.sendMessage(Component.text("你背包中没有铁锭，需要消耗一个铁锭才可以打开铁砧！！！")
              .color(NamedTextColor.RED));
        }
      } else {
        // 参数不正确时，提示玩家正确的用法
        player.sendMessage("用法: /fwork [craft|anvil]");
      }
    } else {
      // 参数个数不正确时，提示玩家正确的用法
      player.sendMessage("用法: /fwork [craft|anvil]");
    }
    
    return true;
  }
  
  // 检查玩家背包中是否有铁锭
  private boolean hasIronIngot(Player player) {
    ItemStack[] inventoryContents = player.getInventory().getContents();
    for (ItemStack item : inventoryContents) {
      if (item != null && item.getType() == Material.IRON_INGOT) {
        return true;
      }
    }
    return false;
  }
  
  // 从玩家背包中移除一块铁锭（简单实现，只移除找到的第一个）
  private void removeIronIngot(Player player) {
    ItemStack[] inventoryContents = player.getInventory().getContents();
    for (int i = 0; i < inventoryContents.length; i++) {
      ItemStack item = inventoryContents[i];
      if (item != null && item.getType() == Material.IRON_INGOT) {
        if (item.getAmount() > 1) {
          item.setAmount(item.getAmount() - 1);
        } else {
          inventoryContents[i] = null;
        }
        player.getInventory().setContents(inventoryContents);
        break;
      }
    }
  }
  
  
  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
    // 当输入命令参数为空时，提供"craft"和"anvil"作为补全建议
    if (args.length == 0) {
      return Arrays.asList("craft", "anvil");
    }
    
    // 当已经输入了部分内容时，进行模糊匹配补全
    if (args.length == 1) {
      List<String> completions = new ArrayList<>();
      StringUtil.copyPartialMatches(args[0], Arrays.asList("craft", "anvil"), completions);
      return completions;
    }
    
    return new ArrayList<>();
  }
}
