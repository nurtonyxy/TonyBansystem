package de.luxsmp.bansystem.commands;

import de.luxsmp.bansystem.AdvancedBanSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class KickCommand implements CommandExecutor {

    private final AdvancedBanSystem plugin;

    public KickCommand(AdvancedBanSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args) {

        if (!sender.hasPermission("kick.use")) {
            sender.sendMessage(plugin.getMessage("no-permission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(plugin.getMessage("usage-kick"));
            return true;
        }

        String targetName = args[0];
        String reason = buildReason(args, 1);
        String moderator = sender.getName();

        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            sender.sendMessage(plugin.getMessage("player-not-found")
                    .replace("{player}", targetName));
            return true;
        }

        String kickScreen = plugin.getRawMessage("kick-screen")
                .replace("{reason}", reason)
                .replace("{moderator}", moderator);

        target.kickPlayer(kickScreen);

        sender.sendMessage(plugin.getMessage("kick-success")
                .replace("{player}", target.getName())
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
