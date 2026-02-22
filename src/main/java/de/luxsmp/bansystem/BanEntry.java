package de.luxsmp.bansystem;

public class BanEntry {

    private final String name;
    private final String reason;
    private final String moderator;
    private final String date;
    private final long expiry;

    public BanEntry(String name, String reason, String moderator, String date, long expiry) {
        this.name = name;
        this.reason = reason;
        this.moderator = moderator;
        this.date = date;
        this.expiry = expiry;
    }

    public String getName() {
        return name;
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

    public boolean isPermanent() {
        return expiry == -1;
    }

    public boolean isExpired() {
        return !isPermanent() && System.currentTimeMillis() > expiry;
    }
}
