package de.luxsmp.bansystem;

public class HistoryEntry {

    public enum Type {
        BAN, TEMPBAN
    }

    private final Type type;
    private final String reason;
    private final String moderator;
    private final String date;
    private final long expiry;
    private final String unbanDate;
    private final String unbanModerator;

    public HistoryEntry(Type type, String reason, String moderator,
            String date, long expiry,
            String unbanDate, String unbanModerator) {
        this.type = type;
        this.reason = reason;
        this.moderator = moderator;
        this.date = date;
        this.expiry = expiry;
        this.unbanDate = unbanDate;
        this.unbanModerator = unbanModerator;
    }

    public Type getType() {
        return type;
    }

    public String getReason() {
        return reason;
    }

    public String getModerator() {
        return moderator;
    }

    public String getDate() {
        return date;
    }

    public long getExpiry() {
        return expiry;
    }

    public String getUnbanDate() {
        return unbanDate;
    }

    public String getUnbanModerator() {
        return unbanModerator;
    }

    public boolean isPermanent() {
        return expiry == -1;
    }

    public boolean wasManualUnban() {
        return unbanDate != null;
    }
}
