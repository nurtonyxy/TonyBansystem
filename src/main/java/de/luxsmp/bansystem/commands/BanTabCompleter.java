package de.luxsmp.bansystem.commands;

import de.luxsmp.bansystem.AdvancedBanSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BanTabCompleter implements TabCompleter {

    private static final List<String> TIME_EXAMPLES = List.of("30s", "5m", "1h", "7d", "30d");

    private final AdvancedBanSystem plugin;

    public BanTabCompleter(AdvancedBanSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args) {
        String cmd = command.getName().toLowerCase();

        return switch (cmd) {
            case "ban" -> handleBan(args);
            case "tempban" -> handleTempBan(args);
            case "unban" -> handleUnban(args);
            case "kick" -> handleKick(args);
            case "history" -> args.length == 1 ? filterOnlinePlayers(args[0]) : List.of();
            default -> List.of();
        };
    }

    private List<String> handleBan(String[] args) {
        if (args.length == 1)
            return filterOnlinePlayers(args[0]);
        if (args.length == 2)
            return filter(getReasons("ban"), args[1]);
        return List.of();
    }

    private List<String> handleTempBan(String[] args) {
        if (args.length == 1)
            return filterOnlinePlayers(args[0]);
        if (args.length == 2)
            return filter(TIME_EXAMPLES, args[1]);
        if (args.length == 3)
            return filter(getReasons("tempban"), args[2]);
        return List.of();
    }

    private List<String> handleUnban(String[] args) {
        if (args.length == 1)
            return filterOnlinePlayers(args[0]);
        return List.of();
    }

    private List<String> handleKick(String[] args) {
        if (args.length == 1)
            return filterOnlinePlayers(args[0]);
        if (args.length == 2)
            return filter(getReasons("kick"), args[1]);
        return List.of();
    }

    private List<String> getReasons(String type) {
        return plugin.getConfig().getStringList("reasons." + type);
    }

    private List<String> filterOnlinePlayers(String prefix) {
        List<String> result = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().toLowerCase().startsWith(prefix.toLowerCase())) {
                result.add(p.getName());
            }
        }
        return result;
    }

    private List<String> filter(List<String> options, String prefix) {
        List<String> result = new ArrayList<>();
        for (String opt : options) {
            if (opt.toLowerCase().startsWith(prefix.toLowerCase())) {
                result.add(opt);
            }
        }
        return result;
    }
}
