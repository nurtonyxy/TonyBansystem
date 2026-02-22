package de.luxsmp.bansystem.commands;

import de.luxsmp.bansystem.AdvancedBanSystem;
import de.luxsmp.bansystem.HistoryEntry;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class HistoryCommand implements CommandExecutor {

    private final AdvancedBanSystem plugin;

    public HistoryCommand(AdvancedBanSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args) {

        if (!sender.hasPermission("history.use")) {
            sender.sendMessage(plugin.getMessage("no-permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(AdvancedBanSystem.color("&cUsage: /history <player>"));
            return true;
        }

        String targetName = args[0];

        UUID uuid = resolveUUID(targetName);
        if (uuid == null) {
            sender.sendMessage(plugin.getMessage("player-not-found")
                    .replace("{player}", targetName));
            return true;
        }

        List<HistoryEntry> history = plugin.getBanManager().getHistory(uuid);

        if (history.isEmpty()) {
            sender.sendMessage(plugin.getMessage("history-none")
                    .replace("{player}", targetName));
            return true;
        }

        sender.sendMessage(AdvancedBanSystem.color(
                "&8&m--------------------&r &6Ban-History: &e" + targetName + " &8&m--------------------"));
        sender.sendMessage(AdvancedBanSystem.color(
                "&7Total entries: &f" + history.size()));

        for (int i = history.size() - 1; i >= 0; i--) {
            HistoryEntry h = history.get(i);
            int index = history.size() - i;

            sender.sendMessage(AdvancedBanSystem.color(
                    "&8[&6#" + index + "&8] &e" + h.getType().name()
                            + " &8| &7by &f" + h.getModerator()
                            + " &8| &7on &f" + h.getDate()));

            sender.sendMessage(AdvancedBanSystem.color(
                    "  &7Reason: &f" + h.getReason()));

            if (!h.isPermanent()) {
                sender.sendMessage(AdvancedBanSystem.color(
                        "  &7Duration: &f" + AdvancedBanSystem.formatDate(h.getExpiry())));
            } else {
                sender.sendMessage(AdvancedBanSystem.color(
                        "  &7Duration: &cPermanent"));
            }

            if (h.wasManualUnban()) {
                sender.sendMessage(AdvancedBanSystem.color(
                        "  &7Unbanned: &a" + h.getUnbanDate()
                                + " &7by &f" + h.getUnbanModerator()));
            } else if (!h.isPermanent() && h.getExpiry() < System.currentTimeMillis()) {
                sender.sendMessage(AdvancedBanSystem.color(
                        "  &7Status: &aExpired"));
            } else if (h.isPermanent() && plugin.getBanManager().isBanned(uuid)) {
                sender.sendMessage(AdvancedBanSystem.color(
                        "  &7Status: &cActive"));
            }
        }

        sender.sendMessage(AdvancedBanSystem.color(
                "&8&m" + "-".repeat(60)));
        return true;
    }

    private UUID resolveUUID(String name) {
        var online = Bukkit.getPlayerExact(name);
        if (online != null)
            return online.getUniqueId();

        UUID fromHistory = plugin.getBanManager().getUUIDFromHistory(name);
        if (fromHistory != null)
            return fromHistory;

        @SuppressWarnings("deprecation")
        OfflinePlayer offline = Bukkit.getOfflinePlayer(name);
        if (offline.hasPlayedBefore())
            return offline.getUniqueId();

        return null;
    }
}
