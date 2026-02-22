package models;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import classes.EventoData;
import classes.EventoManager;
import classes.EventoState;
import eventos.Main;

public class Loteria extends Evento {

    private final int selected;
    private final int max;
    private int apostas;

    public Loteria(int maxNum, int defaultAward, int timer) {
        super(
            "Loteria",
            null,
            new EventoData(0, defaultAward, 90, timer, null, null)
        );

        this.max = maxNum;
        this.selected = new Random().nextInt(maxNum + 1); // 0 até max
    }

    public int getSelected() {
        return selected;
    }

    public void addAposta() {
        apostas++;
    }

    @Override
    public void start() {
        EventoManager.timer = getData().timer;
        EventoManager.setEvento(this);
        EventoState.INICIANDO.define();

        addTask(Bukkit.getScheduler().runTaskTimer(
            Main.getInstance(),
            () -> {
                int timer = EventoManager.timer;

                if (timer <= 0) {
                    cancelTasks();
                    started();
                    EventoState.INICIADO.define();
                    return;
                }

                if (timer % 30 == 0) {
                    broadcastInfo();
                }

                EventoManager.timer--;
            },
            0L,
            20L
        ));
    }

    public void finish(Player player) {
        String prefix = player.getDisplayName(); // compatível 1.21

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§7" + prefix + " §facertou o número e ganhou §7"
                + getAward() + " coins§f!");
        Bukkit.broadcastMessage("§fHouve §7" + apostas + "§f tentativas.");
        Bukkit.broadcastMessage("§fO número era: §6" + selected);
        Bukkit.broadcastMessage("");

        // Vault (opcional)
        // Main.ECON.depositPlayer(player, getAward());

        stop();
    }

    @Override
    public void started() {
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§fNenhum vencedor da loteria foi encontrado.");
        Bukkit.broadcastMessage("§fHouve apenas §7" + apostas + "§f tentativas.");
        Bukkit.broadcastMessage("§fO número era: §6" + selected);
        Bukkit.broadcastMessage("");

        stop();
    }

    @Override
    public void stop() {
        cancelTasks();
        EventoState.FECHADO.define();
        EventoManager.setEvento(null);
    }

    @Override
    public void broadcastInfo() {
        int time = EventoManager.timer;

        String tempoRestante;
        if (time >= 60) {
            tempoRestante = (time / 60) + " minuto(s)";
        } else {
            tempoRestante = time + " segundos";
        }

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§6Evento §e§lLOTERIA §6aberto!");
        Bukkit.broadcastMessage("§fUse §7/loteria <número> §fpara participar.");
        Bukkit.broadcastMessage("§fNúmeros de §70 §faté §7" + max);
        Bukkit.broadcastMessage("§fPremiação: §7" + getAward() + " coins");
        Bukkit.broadcastMessage("§fEncerra em §e" + tempoRestante);
        Bukkit.broadcastMessage("");

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
        }
    }
}
