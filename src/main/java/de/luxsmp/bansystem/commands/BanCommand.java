package de.luxsmp.bansystem.commands;

import de.luxsmp.bansystem.AdvancedBanSystem;
import de.luxsmp.bansystem.BanEntry;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BanCommand implements CommandExecutor {

    private final AdvancedBanSystem plugin;

    public BanCommand(AdvancedBanSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args) {

        if (!sender.hasPermission("ban.use")) {
            sender.sendMessage(plugin.getMessage("no-permission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(plugin.getMessage("usage-ban"));
            return true;
        }

        String targetName = args[0];
        String reason = buildReason(args, 1);
        String moderator = sender.getName();
        String date = AdvancedBanSystem.formatDate(System.currentTimeMillis());

        @SuppressWarnings("deprecation")
        OfflinePlayer offline = Bukkit.getOfflinePlayer(targetName);

        if (offline == null || (!offline.hasPlayedBefore() && !offline.isOnline())) {
            sender.sendMessage(plugin.getMessage("player-not-found")
                    .replace("{player}", targetName));
            return true;
        }

        if (plugin.getBanManager().isBanned(offline.getUniqueId())) {
            sender.sendMessage(plugin.getMessage("already-banned")
                    .replace("{player}", targetName));
            return true;
        }

        BanEntry entry = new BanEntry(offline.getName(), reason, moderator, date, -1);
        plugin.getBanManager().addBan(offline.getUniqueId(), entry);

        Player online = Bukkit.getPlayer(offline.getUniqueId());
        if (online != null) {
            String screen = plugin.getRawMessage("ban-screen")
                    .replace("{reason}", reason)
                    .replace("{moderator}", moderator)
                    .replace("{date}", date);
            online.kickPlayer(screen);
        }

        sender.sendMessage(plugin.getMessage("ban-success")
                .replace("{player}", offline.getName())
                .replace("{reason}", reason));
        return true;
    }

    private String buildReason(String[] args, int startIndex) {
        StringBuilder sb = new StringBuilder();
        for (int i = startIndex; i < args.length; i++) {
            if (i > startIndex)
                sb.append(' ');
            sb.append(args[i]);
        }
        return sb.toString();
    }
}
