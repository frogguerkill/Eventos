package classes;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import models.Evento;

import java.util.*;

public final class EventoManager {

    private static Evento evento;
    public static int timer;

    private static final Set<UUID> players = new HashSet<>();

    /* =========================
       EVENTO ATUAL
       ========================= */

    public static Evento getEvento() {
        return evento;
    }

    public static void setEvento(Evento e) {
        evento = e;
    }

    /* =========================
       PLAYERS
       ========================= */

    public static List<Player> getPlayers() {
        List<Player> list = new ArrayList<>();
        for (UUID uuid : players) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline()) {
                list.add(p);
            }
        }
        return list;
    }

    public static void addPlayer(Player player) {
        players.add(player.getUniqueId());
    }

    public static void removePlayer(Player player) {
        players.remove(player.getUniqueId());
    }

    public static void clearPlayers() {
        players.clear();
    }

    /* =========================
       JOIN EVENTO
       ========================= */

    public static void join(Player player) {

        if (evento == null) {
            player.sendMessage("§cNão há nenhum evento ativo no momento.");
            return;
        }

        if (players.contains(player.getUniqueId())) {
            player.sendMessage("§cVocê já está participando do evento.");
            return;
        }

        if (players.size() >= evento.getData().maxPlayers) {
            player.sendMessage("§cO evento está lotado. Tente no próximo.");
            return;
        }

        evento.join(player);
    }

    /* =========================
       UTIL
       ========================= */

    public static void playSound(Sound sound, float volume, float pitch) {
        getPlayers().forEach(p ->
                p.playSound(p.getLocation(), sound, volume, pitch));
    }

    /* =========================
       INIT
       ========================= */

    static {
        timer = 60;
    }
}
