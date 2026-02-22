package classes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DestemidoListener implements Listener {

    private final Map<UUID, Double> danoTotal = new HashMap<>();
    private final Map<UUID, Integer> hits = new HashMap<>();

    /* ===============================
       DANO NO DRAGÃO
       =============================== */
    @EventHandler(ignoreCancelled = true)
    public void onDragonDamage(EntityDamageByEntityEvent e) {

        if (!(e.getEntity() instanceof EnderDragon)) return;
        if (!(e.getDamager() instanceof Player player)) return;
        if (!DestemidoManager.isAtivo()) return;

        UUID id = player.getUniqueId();

        danoTotal.merge(id, e.getFinalDamage(), Double::sum);
        hits.merge(id, 1, Integer::sum);

        DestemidoManager.registrarDano(player, e.getFinalDamage());
    }

    /* ===============================
       MORTE DO DRAGÃO
       =============================== */
    @EventHandler
    public void onDragonDeath(EntityDeathEvent e) {

        if (!(e.getEntity() instanceof EnderDragon)) return;
        if (!DestemidoManager.isAtivo()) return;

        Player finalizador = e.getEntity().getKiller();

        UUID maiorDano = danoTotal.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        UUID maiorParticipacao = hits.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        Bukkit.broadcastMessage("§6§lDESTEMIDO FOI DERROTADO!");

        if (maiorDano != null) {
            Player p = Bukkit.getPlayer(maiorDano);
            if (p != null)
                Bukkit.broadcastMessage("§e🥇 Maior Dano: §a" + p.getName());
        }

        if (finalizador != null) {
            Bukkit.broadcastMessage("§e🥈 Golpe Final: §a" + finalizador.getName());
        }

        if (maiorParticipacao != null) {
            Player p = Bukkit.getPlayer(maiorParticipacao);
            if (p != null)
                Bukkit.broadcastMessage("§e🥉 Maior Participação: §a" + p.getName());
        }

        // Limpa dados
        danoTotal.clear();
        hits.clear();

        // Finaliza evento corretamente
        DestemidoManager.finalizar();
    }

    /* ===============================
       JOGADOR MORRE → SAI DO EVENTO
       =============================== */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {

        Player p = e.getEntity();
        if (!DestemidoManager.isAtivo()) return;

        DestemidoManager.sair(p);
        p.sendMessage("§cVocê morreu e saiu do Destemido.");
    }

    /* ===============================
       JOGADOR QUITA → SAI DO EVENTO
       =============================== */
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {

        Player p = e.getPlayer();
        if (!DestemidoManager.isAtivo()) return;

        DestemidoManager.sair(p);
    }
}

