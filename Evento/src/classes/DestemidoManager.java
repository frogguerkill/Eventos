package classes;

import org.bukkit.*;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import eventos.Main;
import utils.DestemidoBossBar;

import java.util.*;

public class DestemidoManager {

    /* =======================
       ESTADO
       ======================= */
    private static boolean aguardando = false;
    private static boolean ativo = false;

    private static Location spawnJogadores;
    private static Location spawnDragao;

    private static EnderDragon dragao;
    private static double coinsTotal = 1500;

    private static final Map<UUID, Double> dano = new HashMap<>();
    private static final Set<UUID> participantes = new HashSet<>();

    /* =======================
       STATUS
       ======================= */
    public static boolean isAtivo() {
        return ativo;
    }

    public static boolean isAguardando() {
        return aguardando;
    }

    /* =======================
       SPAWNS
       ======================= */
    public static void setSpawnJogadores(Location loc) {
        spawnJogadores = loc;
    }

    public static void setSpawnDragao(Location loc) {
        spawnDragao = loc;
    }

    public static Location getSpawnJogadores() {
        return spawnJogadores;
    }
    
    public static boolean isParticipante(Player p) {
        return participantes.contains(p.getUniqueId());
    }

    /* =======================
       INICIAR EVENTO
       ======================= */
    public static void iniciar(JavaPlugin plugin, double coins) {

        if (spawnJogadores == null || spawnDragao == null) {
            Bukkit.broadcastMessage("§c[Destemido] Spawns não configurados.");
            return;
        }

        if (ativo || aguardando) return;

        coinsTotal = coins > 0 ? coins : 1500;
        aguardando = true;

        Bukkit.broadcastMessage("§6[Destemido] Evento iniciado!");
        Bukkit.broadcastMessage("§eRecompensa total: §a" + coinsTotal + " coins");
        Bukkit.broadcastMessage("§eUse §a/destemido entrar");

        iniciarCountdown(plugin, 180);
    }

    /* =======================
       COUNTDOWN (SEM LAG)
       ======================= */
    private static void iniciarCountdown(JavaPlugin plugin, int tempo) {

        Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {

            int t = tempo;

            @Override
            public void run() {

                if (t == 0) {
                    iniciarCombate();
                    Bukkit.getScheduler().cancelTasks(plugin);
                    return;
                }

                if (t % 60 == 0 || t <= 30) {
                    if (t <= 3) {
                        Bukkit.broadcastMessage("§c" + t + "...");
                    } else {
                        Bukkit.broadcastMessage("§6[Destemido] Começa em §e" + t + "s");
                    }
                }

                t--;
            }
        }, 0L, 20L);
    }

    /* =======================
       COMBATE
       ======================= */
    private static void iniciarCombate() {

        aguardando = false;
        ativo = true;

        Bukkit.broadcastMessage("§c[Destemido] EVENTO COMEÇOU!");

        dragao = spawnDragao.getWorld().spawn(spawnDragao, EnderDragon.class);
        dragao.setCustomName("§c§lBAMGUELA");
        dragao.setCustomNameVisible(true);

        DestemidoBossBar.create(Main.getInstance(), dragao);;

        for (UUID id : participantes) {
            Player p = Bukkit.getPlayer(id);
            if (p != null) {
                p.teleport(spawnJogadores);
            }
        }
    }

    /* =======================
       ENTRAR
       ======================= */
    public static void entrar(Player p) {

        if (!aguardando) {
            p.sendMessage("§cEvento indisponível.");
            return;
        }

        participantes.add(p.getUniqueId());
        dano.putIfAbsent(p.getUniqueId(), 0D);

        p.teleport(spawnJogadores);
        p.sendMessage("§aVocê entrou no Destemido!");
    }

    /* =======================
       REGISTRAR DANO
       ======================= */
    public static void registrarDano(Player p, double valor) {
        if (!ativo) return;
        dano.put(p.getUniqueId(), dano.getOrDefault(p.getUniqueId(), 0D) + valor);
    }

    /* =======================
       SAIR DO EVENTO
       ======================= */
    public static void sair(Player p) {
        participantes.remove(p.getUniqueId());
        dano.remove(p.getUniqueId());
    }

    /* =======================
       FINALIZAR
       ======================= */
    public static void finalizar() {

        ativo = false;
        aguardando = false;

        if (dragao != null) dragao.remove();
        DestemidoBossBar.remove();;

        participantes.clear();
        dano.clear();

        Bukkit.broadcastMessage("§6[Destemido] Evento encerrado.");
    }
}
