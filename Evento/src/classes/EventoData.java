package classes;

import org.bukkit.Location;

import java.util.List;

public class EventoData {

    public final int minPlayers;
    public final int maxPlayers;
    public final int defaultAward;

    public final int timer;    // tempo até iniciar
    public final int maxTime;  // tempo máximo do evento

    public final Location lobbySpawn;
    public final List<Location> spawns;

    public EventoData(
            int minPlayers,
            int maxPlayers,
            int defaultAward,
            int timer,
            Location lobbySpawn,
            List<Location> spawns
    ) {
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.defaultAward = defaultAward;
        this.timer = timer;
        this.maxTime = timer;
        this.lobbySpawn = lobbySpawn;
        this.spawns = spawns;
    }
}
