package me.realized.de.certificates;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.realized.de.certificates.util.ItemUtil;
import me.realized.de.certificates.util.StringUtil;
import me.realized.de.certificates.util.compat.Identifiers;
import me.realized.duels.api.event.match.MatchEndEvent;
import me.realized.duels.api.event.match.MatchStartEvent;
import me.realized.duels.api.extension.DuelsExtension;
import me.realized.duels.api.match.Match;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class Certificates extends DuelsExtension implements Listener {

    private final Map<UUID, String> opponents = new HashMap<>();

    private ItemStack certificate;
    private SimpleDateFormat format;

    @Override
    public void onEnable() {
        final FileConfiguration config = getConfig();
        final ConfigurationSection itemSection = config.getConfigurationSection("certificate-item");
        final String type = itemSection.getString("type");

        if (type == null) {
            error("Certificate Item Type unspecified in config!");
            return;
        }

        final Material material = Material.getMaterial(itemSection.getString("type"));

        if (material == null) {
            error(type + " is not a valid material!");
            return;
        }

        this.certificate = new ItemStack(material, itemSection.getInt("amount", 1), (short) itemSection.getInt("data", 0));

        if (itemSection.isString("name")) {
            ItemUtil.editMeta(certificate, meta -> meta.setDisplayName(StringUtil.color(itemSection.getString("name"))));
        }

        if (itemSection.isList("lore")) {
            ItemUtil.editMeta(certificate, meta -> meta.setLore(StringUtil.color(itemSection.getStringList("lore"))));
        }

        this.certificate = Identifiers.addIdentifier(certificate, api);
        this.format = config.isString("date-format") ? new SimpleDateFormat(config.getString("date-format")) : new SimpleDateFormat();
        api.registerListener(this);
    }

    @Override
    public void onDisable() {
        opponents.clear();
    }

    @Override
    public String getRequiredVersion() {
        return "3.1.2";
    }

    private void error(final String s) {
        api.error("[" + getName() + " Extension] " + s);
    }

    @EventHandler
    public void on(final MatchStartEvent event) {
        final Player[] players = event.getPlayers();
        opponents.put(players[0].getUniqueId(), players[1].getName());
        opponents.put(players[1].getUniqueId(), players[0].getName());
    }

    @EventHandler
    public void on(final MatchEndEvent event) {
        final Player winner = Bukkit.getPlayer(event.getWinner());

        if (winner != null) {
            final String loserName = opponents.remove(winner.getUniqueId());
            final ItemStack item = certificate.clone();
            ItemUtil.editMeta(item, meta -> {
                if (meta.hasDisplayName()) {
                    final String name = meta.getDisplayName();
                    meta.setDisplayName(replace(name, winner.getName(), loserName, event.getMatch()));
                }
            });
            ItemUtil.editMeta(item, meta -> {
                if (meta.hasLore()) {
                    final List<String> lore = meta.getLore();
                    lore.replaceAll(s -> s = replace(s, winner.getName(), loserName, event.getMatch()));
                    meta.setLore(lore);
                }
            });
            winner.getInventory().addItem(item);
        }
    }

    private String replace(final String s, final String winner, final String loser, final Match match) {
        return s
            .replace("%winner%", winner)
            .replace("%loser%", loser)
            .replace("%kit%", match.getKit() != null ? match.getKit().getName() : "none")
            .replace("%arena%", match.getArena().getName())
            .replace("%bet%", String.valueOf(match.getBet()))
            .replace("%date%", format.format(new Date()));
    }

    @EventHandler
    public void on(final PlayerQuitEvent event) {
        opponents.remove(event.getPlayer().getUniqueId());
    }
}
