package classes;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;

import models.Evento;


public class EventoListener implements Listener {

    /* ==========================
       MOVIMENTO
       ========================== */

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {

        // Evita rodar se não mudou de bloco
        if (e.getFrom().getBlockX() == e.getTo().getBlockX()
                && e.getFrom().getBlockY() == e.getTo().getBlockY()
                && e.getFrom().getBlockZ() == e.getTo().getBlockZ()) {
            return;
        }

        Player p = e.getPlayer();

        if (!EventoManager.getPlayers().contains(p)) return;

        Location to = e.getTo();
        if (to == null) return;

        Evento evento = EventoManager.getEvento();
        if (evento == null) return;

        /* === Chegou ao final === */
        Block below = to.getBlock().getRelative(BlockFace.DOWN);

        if (below.getType() == Material.SEA_LANTERN) {
            evento.handle(p);
            return;
        }

        /* === Caiu === */
        if (to.getY() <= -1) {
            List<Location> spawns = evento.getData().spawns;
            if (spawns == null || spawns.isEmpty()) return;

            Location spawn = spawns.get(ThreadLocalRandom.current().nextInt(spawns.size()));
            if (spawn != null && spawn.getWorld() != null) {
                p.teleport(spawn);
            }
        }

        /* === Limpa efeitos === */
        if (!p.getActivePotionEffects().isEmpty()) {
            for (PotionEffect pot : p.getActivePotionEffects()) {
                p.removePotionEffect(pot.getType());
            }
        }
    }

    /* ==========================
       MORTE
       ========================== */

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (EventoManager.getPlayers().contains(p)) {
            EventoManager.removePlayer(p);
        }
    }

    /* ==========================
       QUIT
       ========================== */

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (EventoManager.getPlayers().contains(p)) {
            EventoManager.removePlayer(p);
        }
    }

    /* ==========================
       TELEPORT
       ========================== */

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();

        Location from = e.getFrom();
        Location to = e.getTo();

        if (from == null || to == null) return;
        if (from.getWorld() == null || to.getWorld() == null) return;

        if (!from.getWorld().getName().equalsIgnoreCase("eventos")) return;

        Evento evento = EventoManager.getEvento();
        if (evento == null) return;

        // Saiu do mundo eventos
        if (!to.getWorld().getName().equalsIgnoreCase("eventos")) {

            if (p.getGameMode() != GameMode.CREATIVE) {
                p.setFlying(false);
                p.setAllowFlight(false);
            }

            EventoManager.removePlayer(p);

            for (PotionEffect eff : p.getActivePotionEffects()) {
                p.removePotionEffect(eff.getType());
            }
        }
    }
}
