package de.luxsmp.bansystem.commands;

import de.luxsmp.bansystem.AdvancedBanSystem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class UnbanCommand implements CommandExecutor {

    private final AdvancedBanSystem plugin;

    public UnbanCommand(AdvancedBanSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args) {

        if (!sender.hasPermission("unban.use")) {
            sender.sendMessage(plugin.getMessage("no-permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(plugin.getMessage("usage-unban"));
            return true;
        }

        String targetName = args[0];

        UUID uuid = plugin.getBanManager().getUUIDByName(targetName);

        if (uuid == null) {
            @SuppressWarnings("deprecation")
            OfflinePlayer offline = Bukkit.getOfflinePlayer(targetName);
            if (offline != null)
                uuid = offline.getUniqueId();
        }

        if (uuid == null || !plugin.getBanManager().isBanned(uuid)) {
            sender.sendMessage(plugin.getMessage("not-banned")
                    .replace("{player}", targetName));
            return true;
        }

        plugin.getBanManager().removeBan(uuid, sender.getName());
        sender.sendMessage(plugin.getMessage("unban-success")
                .replace("{player}", targetName));
        return true;
    }
}
