package models;

import java.util.*;
import org.bukkit.*;

import classes.EventoType;

public class Parkour1 extends Eventos {

    private static Location lobby;
    private static List<Location> spawns;

    public Parkour1() {
        super(
            "PARKOUR",
            Arrays.asList(
                "Corra e chegue a meta em primeiro lugar!",
                "Se cair, será teleportado para o início."
            ),
            1000,
            600,
            60,
            initLobby(),
            initSpawns()
        );
    }

    @Override
    public EventoType getType() {
        return EventoType.PARKOUR1;
    }

    /* ==========================
       INIT REAL (SEM STATIC)
       ========================== */

    private static Location initLobby() {
        if (lobby != null) return lobby;

        World world = Bukkit.getWorld("eventos");

        if (world == null) {
            Bukkit.getLogger().severe("Mundo 'eventos' não encontrado.");
            return null;
        }

        lobby = new Location(world, 0.463, 32.0, -167.558, 0.7f, 0.7f);
        return lobby;
    }

    private static List<Location> initSpawns() {
        if (spawns != null) return spawns;

        World world = Bukkit.getWorld("eventos");

        if (world == null) {
            Bukkit.getLogger().severe("Mundo 'eventos' não encontrado.");
            return Collections.emptyList();
        }

        spawns = Arrays.asList(
            new Location(world, 13.993, 30.04042, -398.557, -0.6f, 2.5f),
            new Location(world, 5.060, 30.29089, -397.752, -0.5f, 0.6f),
            new Location(world, -3.867, 30.68571, -398.42, 0.7f, -1.3f),
            new Location(world, -12.694, 30.0631, -397.847, 0.1f, 0.4f)
        );
        return spawns;
    }
}
