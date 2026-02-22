package de.luxsmp.bansystem.commands;

import de.luxsmp.bansystem.AdvancedBanSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class BanSystemCommand implements CommandExecutor {

    private final AdvancedBanSystem plugin;

    public BanSystemCommand(AdvancedBanSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args) {

        if (!sender.hasPermission("bansystem.admin")) {
            sender.sendMessage(plugin.getMessage("no-permission"));
            return true;
        }

        if (args.length < 1 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(AdvancedBanSystem.color(
                    "&cUsage: /bansystem reload"));
            return true;
        }

        plugin.reloadConfig();
        plugin.getBanManager().load();

        sender.sendMessage(plugin.getMessage("reload-success"));
        return true;
    }
}
