package models;

import java.util.*;
import org.bukkit.*;


public class Exemple extends Eventos
{
    private static Location lobby;
    private static List<Location> spawns;
    
    public Exemple() {
        super("Parkour 2.0", Arrays.asList("Corra e chegue a meta em primeiro lugar!", "Se cair, será teleportado para o inicio."), 1000, 600, 60, Exemple.lobby, Exemple.spawns);
    }
    
    //@Override
    //public EventoType getType() {
        //return EventoType.EXEMPLE;
    //}
    
    static {
        Exemple.lobby = new Location(Bukkit.getWorld("eventos"), 0.463, 32.00000, -167.558, 0.7f, 0.7f);
        Exemple.spawns = Arrays.asList(new Location(Bukkit.getWorld("eventos"), 17.955, 30.00000, -716.526, -0.2f, -1.9f), new Location(Bukkit.getWorld("eventos"), 9.082, 30.00000, -716.503, 1.1f, 1.8f), new Location(Bukkit.getWorld("eventos"), 0.105, 30.00000, -715.552, 0.8f, 0.6f), new Location(Bukkit.getWorld("eventos"), -8.897, 30.0000, -716.067, 0.2f, -0.1f));
    }
}
