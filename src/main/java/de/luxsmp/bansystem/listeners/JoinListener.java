package de.luxsmp.bansystem.listeners;

import de.luxsmp.bansystem.AdvancedBanSystem;
import de.luxsmp.bansystem.BanEntry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class JoinListener implements Listener {

    private final AdvancedBanSystem plugin;

    public JoinListener(AdvancedBanSystem plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent event) {
        BanEntry ban = plugin.getBanManager().getBan(event.getPlayer().getUniqueId());
        if (ban == null)
            return;

        if (ban.isPermanent()) {
            String screen = plugin.getRawMessage("ban-screen")
                    .replace("{reason}", ban.getReason())
                    .replace("{moderator}", ban.getModerator())
                    .replace("{date}", ban.getDate());
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, screen);

        } else {
            long remaining = ban.getExpiry() - System.currentTimeMillis();
            String remainingStr = AdvancedBanSystem.formatDuration(remaining);
            String expiryStr = AdvancedBanSystem.formatDate(ban.getExpiry());

            String screen = plugin.getRawMessage("tempban-screen")
                    .replace("{reason}", ban.getReason())
                    .replace("{moderator}", ban.getModerator())
                    .replace("{date}", ban.getDate())
                    .replace("{expiry}", expiryStr);

            String remainingLine = "\n" + plugin.getRawMessage("tempban-remaining")
                    .replace("{remaining}", remainingStr);

            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, screen + remainingLine);
        }
    }
}
