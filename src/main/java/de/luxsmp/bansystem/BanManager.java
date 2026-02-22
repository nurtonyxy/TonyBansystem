package de.luxsmp.bansystem;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BanManager {

    private final AdvancedBanSystem plugin;
    private final File bansFile;
    private FileConfiguration bansConfig;

    private final Map<UUID, BanEntry> banCache = new HashMap<>();

    private final Map<UUID, List<HistoryEntry>> historyCache = new HashMap<>();

    public BanManager(AdvancedBanSystem plugin) {
        this.plugin = plugin;
        this.bansFile = new File(plugin.getDataFolder(), "bans.yml");
        load();
    }

    public void load() {
        if (!bansFile.exists()) {
            plugin.saveResource("bans.yml", false);
        }
        bansConfig = YamlConfiguration.loadConfiguration(bansFile);
        banCache.clear();
        historyCache.clear();

        if (bansConfig.isConfigurationSection("bans")) {
            for (String key : bansConfig.getConfigurationSection("bans").getKeys(false)) {
                String path = "bans." + key;
                try {
                    UUID uuid = UUID.fromString(key);
                    BanEntry entry = new BanEntry(
                            bansConfig.getString(path + ".name", key),
                            bansConfig.getString(path + ".reason", "No reason given"),
                            bansConfig.getString(path + ".moderator", "Console"),
                            bansConfig.getString(path + ".date", "Unknown"),
                            bansConfig.getLong(path + ".expiry", -1));
                    banCache.put(uuid, entry);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in bans.yml (bans): " + key);
                }
            }
        }

        if (bansConfig.isConfigurationSection("history")) {
            for (String key : bansConfig.getConfigurationSection("history").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    List<HistoryEntry> entries = new ArrayList<>();
                    List<Map<?, ?>> list = bansConfig.getMapList("history." + key);
                    for (Map<?, ?> raw : list) {
                        entries.add(new HistoryEntry(
                                HistoryEntry.Type.valueOf(getStr(raw, "type", "BAN")),
                                getStr(raw, "reason", "No reason given"),
                                getStr(raw, "moderator", "Console"),
                                getStr(raw, "date", "Unknown"),
                                getLong(raw, "expiry", -1L),
                                getStr(raw, "unbanDate", null),
                                getStr(raw, "unbanModerator", null)));
                    }
                    historyCache.put(uuid, entries);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in bans.yml (history): " + key);
                }
            }
        }
    }

    public void save() {
        bansConfig.set("bans", null);
        bansConfig.set("history", null);

        for (Map.Entry<UUID, BanEntry> entry : banCache.entrySet()) {
            String path = "bans." + entry.getKey();
            BanEntry ban = entry.getValue();
            bansConfig.set(path + ".name", ban.getName());
            bansConfig.set(path + ".reason", ban.getReason());
            bansConfig.set(path + ".moderator", ban.getModerator());
            bansConfig.set(path + ".date", ban.getDate());
            bansConfig.set(path + ".expiry", ban.getExpiry());
        }

        for (Map.Entry<UUID, List<HistoryEntry>> entry : historyCache.entrySet()) {
            List<Map<String, Object>> list = new ArrayList<>();
            for (HistoryEntry h : entry.getValue()) {
                Map<String, Object> map = new HashMap<>();
                map.put("type", h.getType().name());
                map.put("reason", h.getReason());
                map.put("moderator", h.getModerator());
                map.put("date", h.getDate());
                map.put("expiry", h.getExpiry());
                if (h.getUnbanDate() != null)
                    map.put("unbanDate", h.getUnbanDate());
                if (h.getUnbanModerator() != null)
                    map.put("unbanModerator", h.getUnbanModerator());
                list.add(map);
            }
            bansConfig.set("history." + entry.getKey(), list);
        }

        try {
            bansConfig.save(bansFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save bans.yml: " + e.getMessage());
        }
    }

    public void addBan(UUID uuid, BanEntry entry) {
        banCache.put(uuid, entry);
        HistoryEntry.Type type = entry.isPermanent()
                ? HistoryEntry.Type.BAN
                : HistoryEntry.Type.TEMPBAN;
        HistoryEntry histEntry = new HistoryEntry(
                type, entry.getReason(), entry.getModerator(),
                entry.getDate(), entry.getExpiry(), null, null);
        historyCache.computeIfAbsent(uuid, k -> new ArrayList<>()).add(histEntry);
        save();
    }

    public void removeBan(UUID uuid, String moderator) {
        banCache.remove(uuid);
        List<HistoryEntry> hist = historyCache.get(uuid);
        if (hist != null && !hist.isEmpty()) {
            HistoryEntry last = hist.get(hist.size() - 1);
            hist.set(hist.size() - 1, new HistoryEntry(
                    last.getType(), last.getReason(), last.getModerator(),
                    last.getDate(), last.getExpiry(),
                    AdvancedBanSystem.formatDate(System.currentTimeMillis()),
                    moderator));
        }
        save();
    }

    public void removeBan(UUID uuid) {
        removeBan(uuid, null);
    }

    public boolean isBanned(UUID uuid) {
        BanEntry entry = banCache.get(uuid);
        if (entry == null)
            return false;
        if (entry.isExpired()) {
            removeBan(uuid);
            return false;
        }
        return true;
    }

    public BanEntry getBan(UUID uuid) {
        if (!isBanned(uuid))
            return null;
        return banCache.get(uuid);
    }

    public UUID getUUIDByName(String name) {
        for (Map.Entry<UUID, BanEntry> entry : banCache.entrySet()) {
            if (entry.getValue().getName().equalsIgnoreCase(name)) {
                return entry.getKey();
            }
        }
        var offline = Bukkit.getOfflinePlayerIfCached(name);
        return (offline != null) ? offline.getUniqueId() : null;
    }

    public List<HistoryEntry> getHistory(UUID uuid) {
        return Collections.unmodifiableList(
                historyCache.getOrDefault(uuid, Collections.emptyList()));
    }

    public UUID getUUIDFromHistory(String name) {
        for (Map.Entry<UUID, List<HistoryEntry>> e : historyCache.entrySet()) {
            if (!e.getValue().isEmpty()) {
            }
        }
        var offline = Bukkit.getOfflinePlayerIfCached(name);
        return (offline != null) ? offline.getUniqueId() : null;
    }

    public void purgeExpired() {
        boolean changed = banCache.entrySet().removeIf(e -> e.getValue().isExpired());
        if (changed)
            save();
    }

    private static String getStr(Map<?, ?> map, String key, String def) {
        Object val = map.get(key);
        return (val instanceof String s) ? s : def;
    }

    private static long getLong(Map<?, ?> map, String key, long def) {
        Object val = map.get(key);
        return (val instanceof Number n) ? n.longValue() : def;
    }
}
