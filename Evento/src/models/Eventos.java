package models;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import classes.EventoData;
import classes.EventoManager;
import classes.Ranking;

import java.util.List;

public abstract class Eventos extends Evento {
	
	public int getPremio() {
	    return getAward();
	}

    public Eventos(
            String name,
            List<String> description,
            int defaultAward,
            int maxTime,
            int timer,
            Location waitingRoom,
            List<Location> spawns
    ) {
    	
        super(
            name,
            description,
            new EventoData(0, defaultAward, maxTime, timer, waitingRoom, spawns)
        );
    }

    @Override
    public synchronized void handle(Player p) {

        int win =
                (getWinner(1) == null) ? 1 :
                (getWinner(2) == null) ? 2 :
                (getWinner(3) == null) ? 3 : -1;

        if (win == -1) {
            return;
        }

        Ranking.updateHead(win, p.getName());

        Evento evento = EventoManager.getEvento();
        EventoManager.removePlayer(p);
        setWinner(p.getName(), win);

        int premio = evento.getAward();
        int valorRecebido = premio / win;

        String nomeFormatado = p.getDisplayName();

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(
            "§7" + nomeFormatado +
            " §fchegou ao final em §7" + win +
            "º §flugar e recebeu §7" + valorRecebido +
            " coins§f de prêmio!"
        );
        Bukkit.broadcastMessage("");

        // Economia (Vault)
        // Main.ECON.depositPlayer(p, valorRecebido);

        if (Bukkit.getWorlds().isEmpty()) {
            p.teleport(p.getWorld().getSpawnLocation());
        } else {
            p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        }

        if (win == 3 || EventoManager.getPlayers().isEmpty()) {
            finish();
        }
    }

    @Override
    public void started() {
        super.started();
        super.endTask();
    }
}
