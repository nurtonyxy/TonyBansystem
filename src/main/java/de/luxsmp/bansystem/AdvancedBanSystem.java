package de.luxsmp.bansystem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public final class AdvancedBanSystem extends JavaPlugin {

    private static AdvancedBanSystem instance;
    private BanManager banManager;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        banManager = new BanManager(this);

        getCommand("ban").setExecutor(new de.luxsmp.bansystem.commands.BanCommand(this));
        getCommand("tempban").setExecutor(new de.luxsmp.bansystem.commands.TempBanCommand(this));
        getCommand("unban").setExecutor(new de.luxsmp.bansystem.commands.UnbanCommand(this));
        getCommand("kick").setExecutor(new de.luxsmp.bansystem.commands.KickCommand(this));
        getCommand("bansystem").setExecutor(new de.luxsmp.bansystem.commands.BanSystemCommand(this));
        getCommand("history").setExecutor(new de.luxsmp.bansystem.commands.HistoryCommand(this));

        de.luxsmp.bansystem.commands.BanTabCompleter tabCompleter = new de.luxsmp.bansystem.commands.BanTabCompleter(
                this);
        getCommand("ban").setTabCompleter(tabCompleter);
        getCommand("tempban").setTabCompleter(tabCompleter);
        getCommand("unban").setTabCompleter(tabCompleter);
        getCommand("kick").setTabCompleter(tabCompleter);
        getCommand("history").setTabCompleter(tabCompleter);

        Bukkit.getPluginManager().registerEvents(
                new de.luxsmp.bansystem.listeners.JoinListener(this), this);

        Bukkit.getScheduler().runTaskTimerAsynchronously(this,
                () -> banManager.purgeExpired(), 20L * 60, 20L * 60);

        getLogger().info("AdvancedBanSystem v" + getDescription().getVersion() + " enabled.");
    }

    @Override
    public void onDisable() {
        if (banManager != null)
            banManager.save();
        getLogger().info("AdvancedBanSystem disabled.");
    }

    public static AdvancedBanSystem getInstance() {
        return instance;
    }

    public BanManager getBanManager() {
        return banManager;
    }

    public String getMessage(String key) {
        FileConfiguration cfg = getConfig();
        String prefix = color(cfg.getString("prefix", "&8[&cBanSystem&8] &r"));
        String msg = cfg.getString("messages." + key, "&cMissing message: " + key);
        return prefix + color(msg);
    }

    public String getRawMessage(String key) {
        String msg = getConfig().getString("messages." + key, "&cMissing message: " + key);
        return color(msg);
    }

    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String formatDate(long millis) {
        return DATE_FORMAT.format(new Date(millis));
    }

    public static String formatDuration(long millis) {
        if (millis <= 0)
            return "0s";
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        long hours = TimeUnit.MILLISECONDS.toHours(millis) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0)
            sb.append(days).append("d ");
        if (hours > 0)
            sb.append(hours).append("h ");
        if (minutes > 0)
            sb.append(minutes).append("m ");
        if (seconds > 0 || sb.isEmpty())
            sb.append(seconds).append("s");
        return sb.toString().trim();
    }

    public static long parseTime(String input) {
        if (input == null || input.isEmpty())
            return -1;
        String lower = input.toLowerCase();
        try {
            long value = Long.parseLong(lower.substring(0, lower.length() - 1));
            char unit = lower.charAt(lower.length() - 1);
            return switch (unit) {
                case 's' -> TimeUnit.SECONDS.toMillis(value);
                case 'm' -> TimeUnit.MINUTES.toMillis(value);
                case 'h' -> TimeUnit.HOURS.toMillis(value);
                case 'd' -> TimeUnit.DAYS.toMillis(value);
                default -> -1;
            };
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
