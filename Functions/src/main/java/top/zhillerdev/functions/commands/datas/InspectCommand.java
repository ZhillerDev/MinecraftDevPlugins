package top.zhillerdev.functions.commands.datas;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.zhillerdev.functions.FunctionsMain;

import java.util.List;

public class InspectCommand implements CommandExecutor, TabCompleter {
  
  public InspectCommand(FunctionsMain plugin) {
  }
  
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, String @NotNull [] args) {
    return false;
  }
  
  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
    return List.of();
  }
}
